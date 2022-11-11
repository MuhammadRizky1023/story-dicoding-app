package com.example.storyapp.UI

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyaipp.ui.ViewModel.ListStoryUserViewModel
import com.example.storyapp.Adapter.ListStoryUserAdapter
import com.example.storyapp.Adapter.LoadingListUserAdapter
import com.example.storyapp.Model.ListStoryUserPage
import com.example.storyapp.Preference.StoryUserPreference
import com.example.storyapp.R
import com.example.storyapp.UI.Activities.MapUserActivity
import com.example.storyapp.Preference.ListStoryRepositoryPrefernces
import com.example.storyapp.UI.ViewModel.StoryUserViewModel
import com.example.storyapp.Preference.ViewModelAccountPreferences
import com.example.storyapp.databinding.ActivityListStoryUserBinding

class ListStoryUserActivity : AppCompatActivity() {
    private lateinit var listStoryUserBinding: ActivityListStoryUserBinding
    private var isEnding = false
    private lateinit var token: String
    private val Context.data: DataStore<Preferences> by preferencesDataStore(name = "list")
    private val listStoryUserViewModel: ListStoryUserViewModel by viewModels {
        ListStoryRepositoryPrefernces(this)
    }


    @OptIn(ExperimentalPagingApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        listStoryUserBinding = ActivityListStoryUserBinding.inflate(layoutInflater)
        setContentView(listStoryUserBinding.root)


        observation()
        setOnClick()
        goToLocation()

        val preference = StoryUserPreference.getAccountUser(data)
        val storyUserViewModel =
            ViewModelProvider(this,
                ViewModelAccountPreferences(preference))[StoryUserViewModel::class.java]

        storyUserViewModel.getTokenAuth().observe(this) {
            token = it
            checkUserData(it)
        }


    }

    private fun observation() {
        val listStoryLayout = LinearLayoutManager(this)
        listStoryUserBinding.rvListStories.layoutManager = listStoryLayout
        val listStoryItem = DividerItemDecoration(this, listStoryLayout.orientation)
        listStoryUserBinding.rvListStories.addItemDecoration(listStoryItem)


    }


    @OptIn(ExperimentalPagingApi::class)
    private fun setOnClick() {
        listStoryUserBinding.addStory.setOnClickListener {
            startActivity(Intent(this, AddListStoryActivity::class.java))
        }

        listStoryUserBinding.pullRefresh.setOnRefreshListener {
            checkUserData(token)
            listStoryUserBinding.pullRefresh.isRefreshing = false
        }
    }

    private fun  goToLocation(){
        listStoryUserBinding.addLocationStory.setOnClickListener {
            startActivity(Intent(this, MapUserActivity::class.java))
        }
    }


    @ExperimentalPagingApi
    private fun checkUserData(token: String) {

        val listStoryAdapter = ListStoryUserAdapter(this)
        listStoryUserBinding.rvListStories.adapter = listStoryAdapter.withLoadStateFooter(
            footer = LoadingListUserAdapter {
                listStoryAdapter.retry()
            })
            listStoryUserViewModel.getListStoryUserPage(token).observe(this) {
            listStoryAdapter.submitData(lifecycle, it)
        }

        listStoryAdapter.listStoryItemClickCallback(object :
            ListStoryUserAdapter.ListStoryUserClickCallback {
            override fun onListStoryUserItemClicked(listStory: ListStoryUserPage) {
                selectListUserStory(listStory)
            }
        })

    }



    private fun selectListUserStory(lisStoryUser: ListStoryUserPage) {
        val detailActivity = Intent(this@ListStoryUserActivity, DetailListStoryActivity::class.java)
        detailActivity.putExtra(DetailListStoryActivity.LIST_EXTRA_STORIES, lisStoryUser)
        startActivity(detailActivity)
        clickedListStory()
    }

    private  fun clickedListStory(){
        AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.information))
            setMessage(getString(R.string.clicked_user_story))
            setPositiveButton(getString(R.string.continue_)) { _, _ ->
                val intent = Intent(this@ListStoryUserActivity, ListStoryUserActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
            create()
            show()
        }
    }

    override fun onCreateOptionsMenu(menuItem: Menu): Boolean {
        val optionMenu = menuInflater
        optionMenu.inflate(R.menu.option_menu_item, menuItem)
        return super.onCreateOptionsMenu(menuItem)
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        return  when(menuItem.itemId) {
            R.id.language_menu -> {
                languageMenu()
                true
            }
            R.id.logout_menu -> {
                logOutDialog()
                true
            }
            R.id.menu_map -> {
                goToMap()
                true
            }


            else -> true
        }
    }
    private  fun languageMenu(){
        val  language = Intent(Settings.ACTION_LOCALE_SETTINGS)
        startActivity(language)
    }
    private  fun goToMap(){
        val mapActivity = Intent(this@ListStoryUserActivity, MapUserActivity::class.java)
        startActivity(mapActivity)

    }

    private fun logOutDialog() {
        val build = AlertDialog.Builder(this)
        val alertLogOut = build.create()
        build
            .setTitle(getString(R.string.check_logOut))
            .setMessage(getString(R.string.are_you_sure_to_logout))
            .setPositiveButton(getString(R.string.no)) { _, _ ->
                alertLogOut.cancel()
            }
            .setNegativeButton(getString(R.string.yes)) { _, _ ->
                logoutUser()
            }
            .show()
    }


    private fun logoutUser() {
        val preference = StoryUserPreference.getAccountUser(data)
        val userStoryViewModel =
            ViewModelProvider(this, ViewModelAccountPreferences(preference))[StoryUserViewModel::class.java]
            userStoryViewModel.apply {
                saveUserLoginAuth(false)
                saveTokenAuth("")
                saveUserName("")
            }
        logoutUserFromList()
    }

    private  fun logoutUserFromList(){
        isEnding = true
        startActivity(Intent(this, LoginUserActivity::class.java))
        finish()
    }

}