package com.example.storyapp.UI.Activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.Model.ListStoryUserPage
import com.example.storyapp.Preference.StoryUserPreference
import com.example.storyapp.R
import com.example.storyapp.UI.AddListStoryActivity
import com.example.storyapp.UI.LoginUserActivity
import com.example.storyapp.UI.ViewModel.MapUserViewModel
import com.example.storyapp.Preference.ListStoryRepositoryPrefernces
import com.example.storyapp.UI.ViewModel.StoryUserViewModel
import com.example.storyapp.Preference.ViewModelAccountPreferences
import com.example.storyapp.databinding.ActivityMapUserBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MapUserActivity : AppCompatActivity(), OnMapReadyCallback {
    private val mapUserViewModel: MapUserViewModel by viewModels {
        ListStoryRepositoryPrefernces(this)
    }

    private lateinit var gMap: GoogleMap
    private var isEnding = false
    private lateinit var mapBinding: ActivityMapUserBinding
    private  var _storiesWithLocation = MutableLiveData<List<ListStoryUserPage>>()
    private var storiesWithLocation: LiveData<List<ListStoryUserPage>> =  _storiesWithLocation
    private val Context.data: DataStore<Preferences> by preferencesDataStore(name = "map")
    private  lateinit var focusedPrivateClient: FusedLocationProviderClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mapBinding= ActivityMapUserBinding.inflate(layoutInflater)
        setContentView(mapBinding.root)
        observe()
        fragmentMap()
        addToStory()
        focusedLocation()
    }

    private  fun focusedLocation(){
        focusedPrivateClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private  fun observe(){
        val userStoryPreference = StoryUserPreference.getAccountUser(data)
        val userStoryViewModel =
            ViewModelProvider(this, ViewModelAccountPreferences(userStoryPreference))[StoryUserViewModel::class.java]

        userStoryViewModel.getTokenAuth().observe(this) {
            mapUserViewModel.getMapUser(it)
        }



        mapUserViewModel.loadingIsActive.observe(this) {
            loadingIsActive(it)
        }
        mapUserViewModel.message.observe(this){

        }
        mapUserViewModel.listStoryUser.observe(this){
           getMyMap()
        }
    }


    private  fun fragmentMap(){
        val mapUserFragment = supportFragmentManager
            .findFragmentById(R.id.mapping) as SupportMapFragment
        mapUserFragment.getMapAsync(this)
    }



    override fun onMapReady(map: GoogleMap) {
        gMap = map
        styleMyMap()
        floatingAction()
        getMyMap()
        positionMap()
        mapSetting()
    }

    private  fun mapSetting(){
        gMap.uiSettings.isZoomControlsEnabled = true
        gMap.uiSettings.isIndoorLevelPickerEnabled = true
        gMap.uiSettings.isCompassEnabled = true
        gMap.uiSettings.isMapToolbarEnabled = true
    }

    private fun loadingIsActive(loadingIsActive: Boolean) {
        mapBinding.loadingProcessing.visibility = if (loadingIsActive){
            View.VISIBLE
        } else{
            View.GONE
        }
    }

    private  fun addToStory(){
        mapBinding.addItemStory.setOnClickListener {
            val intent = Intent(this, AddListStoryActivity::class.java)
            intent.putExtra(AddListStoryActivity.LATITUDE, LATITUDE.toFloat())
            intent.putExtra(AddListStoryActivity.LONGITUDE, LONGITUDE.toFloat())
            startActivity(intent)
        }
    }





    private  val requestMapPermissionLaunch =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        )  { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: true -> {
                    getMyMap()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: true -> {
                    getMyMap()
                }
            }
        }

    private fun  checkMapLocation(permission: String) : Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }


    private  fun positionMap(){
        val myLocation = LatLng(-6.23, 106.76)
       storiesWithLocation.observe(this){
          for(item in storiesWithLocation.value?.indices!!){
              val mapUser = LatLng(storiesWithLocation.value?.get(item)?.lat!!,
                  storiesWithLocation.value?.get(item)?.lon!!)
              gMap.addMarker(MarkerOptions().position(mapUser).title(getString(R.string.this_story_uploaded_by)
                   + storiesWithLocation.value?.get(item)?.name))
          }
          gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 5f))
      }
    }



    private fun floatingAction() {
       mapBinding.fabDefaultMap.setOnClickListener {
           floatingActionNormalMap()
       }
        mapBinding.fabAreaMap.setOnClickListener {
            floatingActionTerrainMap()

        }
        mapBinding.fabRealMap.setOnClickListener {
          floatingActionHybridMap()
        }
    }

    private  fun toastDefaultMap(){
        AlertDialog.Builder(this).apply {
            setMessage(getString(R.string.clicked_default_map))
            setPositiveButton(getString(R.string.continue_)) { _, _ ->
            }
            create()
            show()
        }
    }

    private  fun toastAreaMap(){
        AlertDialog.Builder(this).apply {
            setMessage(getString(R.string.clicked_Area_map))
            setPositiveButton(getString(R.string.continue_)) { _, _ ->

            }
            create()
            show()
        }

    }

    private  fun  toastRealMap(){
        AlertDialog.Builder(this).apply {
            setMessage(getString(R.string.clicked_satellite_map))
            setPositiveButton(getString(R.string.continue_)) { _, _ ->

            }
            create()
            show()
        }
    }

    private  fun floatingActionHybridMap(){
        gMap.mapType = GoogleMap.MAP_TYPE_HYBRID
        clickedFloatButtonMapStyle(mapBinding.fabRealMap)
    }

    private  fun floatingActionTerrainMap(){
        gMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
        clickedFloatButtonMapStyle(mapBinding.fabAreaMap)
    }

    private  fun floatingActionNormalMap(){
        gMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        clickedFloatButtonMapStyle(mapBinding.fabDefaultMap)
    }


    private fun selectFloatingMap(floatMap: FloatingActionButton, isClicked: Boolean) {
        var floatButtonMap: Drawable = floatMap.background
        floatButtonMap= DrawableCompat.wrap(floatButtonMap)
        if (isClicked) {
            DrawableCompat.setTint(floatButtonMap, resources.getColor(R.color.red, theme))
        } else {
            DrawableCompat.setTint(floatButtonMap, resources.getColor(R.color.purple, theme))
        }
       floatMap.background = floatButtonMap

    }




    private  fun showMyLocationMarker(location: Location){
        LATITUDE = location.latitude
        LONGITUDE = location.longitude

        val startActivity = LatLng(LATITUDE, LONGITUDE)
        gMap.addMarker(
            MarkerOptions()
                .position(startActivity)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .draggable(true)
                .title(getString(R.string.this_is_your_location))
        )
    }

    private  fun getMyMap(){
        if (checkMapLocation(Manifest.permission.ACCESS_FINE_LOCATION)
            && checkMapLocation(Manifest.permission.ACCESS_COARSE_LOCATION)
        ){
            focusedPrivateClient.lastLocation.addOnSuccessListener { location: Location? ->
               gMap.isMyLocationEnabled = true
                if (location != null) {
                    showMyLocationMarker(location)
                    yourLocationIsSuccessfullyToFound()
                } else {
                    myLocationNotFound()
                }
            }
        } else {
            requestMapPermissionLaunch.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private  fun myLocationNotFound(){
        Toast.makeText(this@MapUserActivity,
            getString(R.string.no_have_location),
            Toast.LENGTH_SHORT).show()
    }

    private  fun  yourLocationIsSuccessfullyToFound(){
        Toast.makeText(this@MapUserActivity,
            getString(R.string.your_location_found),
                    Toast.LENGTH_SHORT).show()
    }






    private fun styleMyMap() {
        try {
            successStyleMap()
            parsingStyledFailed()
        } catch (exception: Resources.NotFoundException) {
            Log.e(LOCATION_USER,  getString(R.string.cant_Styling_parsing_failed) ,exception)
        }
    }

    private  fun  parsingStyledFailed(){
        Log.e(LOCATION_USER,  getString(R.string.cant_Styling_parsing_failed))
    }

    private  fun successStyleMap(){
        gMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.location_style_raw))
    }

    private fun clickedFloatButtonMapStyle(clicked: FloatingActionButton) {
        when (clicked) {
            mapBinding.fabDefaultMap -> {
             floatButtonDefaultMap()
              toastDefaultMap()
            }
            mapBinding.fabAreaMap-> {
                floatButtonAreaMap()
                toastAreaMap()
            }
            mapBinding.fabRealMap-> {
              floatButtonRealMap()
               toastRealMap()
            }
        }
    }

    private  fun floatButtonDefaultMap(){
        selectFloatingMap(mapBinding.fabDefaultMap, true)
        selectFloatingMap(mapBinding.fabAreaMap, false)
        selectFloatingMap(mapBinding.fabRealMap, false)
    }


    private  fun floatButtonAreaMap(){
        selectFloatingMap(mapBinding.fabAreaMap, true)
        selectFloatingMap( mapBinding.fabDefaultMap, false)
        selectFloatingMap( mapBinding.fabRealMap, false)
    }

    private  fun floatButtonRealMap(){
        selectFloatingMap( mapBinding.fabRealMap, true)
        selectFloatingMap( mapBinding.fabAreaMap, false)
        selectFloatingMap( mapBinding.fabDefaultMap, false)
    }



    override fun onCreateOptionsMenu(menuItem: Menu): Boolean {
        val menuInflate = menuInflater
        menuInflate.inflate(R.menu.option_menu_item, menuItem)
        return super.onCreateOptionsMenu(menuItem)
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        return  when(menuItem.itemId) {
            R.id.language_menu -> {
                val intent = Intent(Settings.ACTION_LOCALE_SETTINGS)
                startActivity(intent)
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
                toMap()
                true
            }
            else -> true
        }
    }

    private  fun toMap(){
        val mapActivity = Intent(this@MapUserActivity, MapUserActivity::class.java)
        startActivity(mapActivity)
    }

    private fun logOutDialog() {
        val toast = AlertDialog.Builder(this)
        val logOutAlert = toast.create()
        toast
            .setTitle(getString(R.string.check_logOut))
            .setMessage(getString(R.string.are_you_sure_to_logout))
            .setPositiveButton(getString(R.string.no)) { _, _ ->
                logOutAlert.cancel()
            }
            .setNegativeButton(getString(R.string.yes)) { _, _ ->
                logoutUser()
            }
            .show()
    }


    private fun logoutUser() {
        val preference = StoryUserPreference.getAccountUser(data)
        val storyUserViewModel =
            ViewModelProvider(this, ViewModelAccountPreferences(preference))[StoryUserViewModel::class.java]
            storyUserViewModel.apply {
                saveUserLoginAuth(false)
                saveTokenAuth("")
                saveUserName("")
            }
        userWentToLogout()
    }

    private  fun userWentToLogout(){
        isEnding = true
        startActivity(Intent(this, LoginUserActivity::class.java))
        finish()
    }



    companion object {
        const val LOCATION_USER = "LOCATION"
        var LATITUDE= 0.0
        var LONGITUDE = 0.0

    }
}