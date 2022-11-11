package com.example.storyapp.UI

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.storyapp.Helper.LocationConvertData
import com.example.storyapp.Helper.MapConvertData
import com.example.storyapp.Model.ListStoryUserPage
import com.example.storyapp.Preference.StoryUserPreference

import com.example.storyapp.R
import com.example.storyapp.UI.Activities.MapUserActivity
import com.example.storyapp.UI.ViewModel.StoryUserViewModel
import com.example.storyapp.Preference.ViewModelAccountPreferences
import com.example.storyapp.databinding.ActivityDetailListStoryBinding
import java.text.SimpleDateFormat

class DetailListStoryActivity : AppCompatActivity() {
    private lateinit var detailListStoryBinding: ActivityDetailListStoryBinding
    private var isEnding = false
    private val Context.data: DataStore<Preferences> by preferencesDataStore(name = "detail")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        detailListStoryBinding= ActivityDetailListStoryBinding.inflate(layoutInflater)
        setContentView(detailListStoryBinding.root)
        showDetailStoryUser()
        addStory()

    }

    private  fun showDetailStoryUser(){
        val detailStory = intent.getParcelableExtra<ListStoryUserPage>(LIST_EXTRA_STORIES) as ListStoryUserPage
        setStory(detailStory)
    }

    private  fun addStory(){
        detailListStoryBinding.addStory.setOnClickListener {
            startActivity(Intent(this, AddListStoryActivity::class.java))
        }
    }

    private fun setStory(detailStory: ListStoryUserPage) {
        detailListStoryBinding.tvDetailName.text = detailStory.name
        detailListStoryBinding.tvDetailDescription.text = detailStory.description
        formatDateDetailStory(detailStory)
        detailStoryLocation(detailStory)
        photoStory(detailStory)
    }

    private  fun  formatDateDetailStory(detailStory: ListStoryUserPage){
        val pastDateDetailFormat = detailStory.createdAt?.let {
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS'Z'").parse(it)
        }
        val presentDateDetailFormat = SimpleDateFormat("dd/MM/yyyy").format(pastDateDetailFormat!!)
        detailListStoryBinding.tvDetailDate.text = presentDateDetailFormat
    }


    private  fun detailStoryLocation(detailStory: ListStoryUserPage){
        detailListStoryBinding.tvDetailLocation.text = LocationConvertData.getLocationConvert(
            MapConvertData.mapConvertData(detailStory.lat, detailStory.lon),
            this
        )
    }

    private  fun photoStory(detailStory: ListStoryUserPage){
        Glide.with(this)
            .load(detailStory.photoUrl)
            .placeholder(R.drawable.story)
            .into(detailListStoryBinding.ivDetailPhoto)
    }




    override fun onCreateOptionsMenu(menuItem: Menu): Boolean {
        val menuInflate = menuInflater
        menuInflate.inflate(R.menu.option_menu_item, menuItem)
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
            android.R.id.home -> {
                finish()
                true
            }
            R.id.menu_map -> {
                goToMap()
                true
            }


            else -> true
        }
    }
    private  fun  languageMenu(){
        val language = Intent(Settings.ACTION_LOCALE_SETTINGS)
        startActivity(language)
    }

    private  fun  goToMap(){
        val mapActivity = Intent(this@DetailListStoryActivity, MapUserActivity::class.java)
        startActivity(mapActivity)
    }

    private fun logOutDialog() {
        val alert = AlertDialog.Builder(this)
        val alertLogOut = alert.create()
        alert
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
        val useStoryViewModel =
            ViewModelProvider(this, ViewModelAccountPreferences(preference))[StoryUserViewModel::class.java]
            useStoryViewModel.apply {
                saveUserLoginAuth(false)
                saveTokenAuth("")
                saveUserName("")
            }
        logoutUserFromDetail()

    }

    private  fun logoutUserFromDetail(){
        isEnding = true
        startActivity(Intent(this, LoginUserActivity::class.java))
        finish()
    }

    companion object {
        const val LIST_EXTRA_STORIES= "LIST_EXTRA_STORY"
    }
}