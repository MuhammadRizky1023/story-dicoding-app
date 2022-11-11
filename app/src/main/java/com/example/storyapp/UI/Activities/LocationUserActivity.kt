package com.example.storyapp.UI.Activities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.Helper.LocationConvertData
import com.example.storyapp.Preference.StoryUserPreference
import com.example.storyapp.R
import com.example.storyapp.UI.LoginUserActivity
import com.example.storyapp.UI.ViewModel.StoryUserViewModel
import com.example.storyapp.Preference.ViewModelAccountPreferences
import com.example.storyapp.databinding.ActivityLocationUserBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import java.lang.StringBuilder

class LocationUserActivity :  AppCompatActivity(), OnMapReadyCallback {

    private lateinit var gMap: GoogleMap
    private lateinit var locationBinding: ActivityLocationUserBinding
    private var isEnding = false
    private val Context.data: DataStore<Preferences> by preferencesDataStore(name = "location")
    private  lateinit var focusLocationUser: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationBinding = ActivityLocationUserBinding.inflate(layoutInflater)
        setContentView(locationBinding.root)
        defaultLocationIsActive(false)
       clickedPlaceIsActive(false)
        onClickButton()
        locationUserFragment()
        preferencesUser()
        focusLocationUser()
    }


    private  fun focusLocationUser(){
        focusLocationUser= LocationServices.getFusedLocationProviderClient(this)
    }

    private  fun locationUserFragment(){
        val locationFragment = supportFragmentManager
            .findFragmentById(R.id.location) as SupportMapFragment
        locationFragment.getMapAsync(this)
    }

    private  fun preferencesUser(){
        val preferences = StoryUserPreference.getAccountUser(data)
        val userStoryPreferences = ViewModelProvider(this,
            ViewModelAccountPreferences(preferences))[StoryUserViewModel::class.java]


        userStoryPreferences.getUserName().observe(this){
            locationBinding.tvName.text = StringBuilder(getString(R.string.select_location)).append(" ").append(it)
        }
    }

    private fun onClickButton(){
        locationBinding.btnDefaultLocation.setOnClickListener {
            alertLocation(myDefaultLocation)
        }

        locationBinding.btnClickedPlace.setOnClickListener {
            alertLocation(clickedThisLocation)
        }
    }


    override fun onMapReady(map: GoogleMap) {
        gMap = map
        getDefaultLocationUser()
        setDefaultLocationUser()
        setMarkerDefaultLocationUser()
        settingLocation()
    }

    private  fun settingLocation(){
        gMap.uiSettings.isZoomControlsEnabled = true
        gMap.uiSettings.isIndoorLevelPickerEnabled = true
        gMap.uiSettings.isCompassEnabled = true
        gMap.uiSettings.isMapToolbarEnabled = true
    }

    private  fun setMarkerDefaultLocationUser(){
        gMap.setOnMapClickListener {
            clickedThisLocation= it
            val chooseMarkerLocationUser = MarkerOptions()
            chooseMarkerLocationUser.position(it)

            chooseMarkerLocationUser.title(LocationConvertData.getLocationConvert(it, this))
            gMap.clear()
            val location = CameraUpdateFactory.newLatLngZoom(
                it, 25f
            )
            gMap.animateCamera(location)
            gMap.addMarker(chooseMarkerLocationUser)
            clickedPlaceIsEnabled()
        }
    }

    private  fun  clickedPlaceIsEnabled(){
        clickedPlaceIsActive(true)
    }



    private fun alertLocation(location: LatLng?) {
        val longitude = LocationConvertData.getLocationConvert(location, this)
        val toast = AlertDialog.Builder(this)
        val locationAlert = toast.create()
        toast
            .setTitle(getString(R.string.information))
            .setMessage(longitude)
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                successAccessLocationUser(location)
            }
            .setNegativeButton(getString(R.string.no)) { _, _ ->
                locationAlert.cancel()
            }
            .show()
    }

    private fun successAccessLocationUser(location: LatLng?) {
        val locationActivity= Intent()
        if (location != null) {
            locationActivity.putExtra(LATITUDE, location.latitude)
            locationActivity.putExtra(LONGITUDE, location.longitude)
        }
        setResult(Activity.RESULT_OK, locationActivity)
        finish()
    }

    private fun myDefaultLocation() = LatLng(-6.23, 106.76)


    private fun accessThisPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this.applicationContext,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private val launchingRequestLocationUserPermission =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { result: Boolean ->
            if (result) {
                setDefaultLocationUser()
            }
        }

    private fun getDefaultLocationUser() {
            if (ContextCompat.checkSelfPermission(
                    this.applicationContext,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                myLocationIsEnabled()
            } else {
                accessesFoundLocationUser()
            }
    }

    private  fun  myLocationIsEnabled(){
        gMap.isMyLocationEnabled = true
    }

    private  fun accessesFoundLocationUser(){
       launchingRequestLocationUserPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun defaultLocationIsActive(isEnabled: Boolean) {
        locationBinding.btnDefaultLocation.isEnabled = isEnabled
    }

    private fun clickedPlaceIsActive(isEnabled: Boolean) {
        locationBinding.btnClickedPlace.isEnabled = isEnabled
    }


    override fun onCreateOptionsMenu(itemMenu: Menu): Boolean {
        val itemMenuInflater= menuInflater
        itemMenuInflater.inflate(R.menu.option_menu_item, itemMenu)
        return super.onCreateOptionsMenu(itemMenu)
    }


    override fun onOptionsItemSelected(itemMenu: MenuItem): Boolean {
        return  when(itemMenu.itemId) {
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
            android.R.id.home -> {
                finish()
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
        val mapActivity = Intent(this@LocationUserActivity, MapUserActivity::class.java)
        startActivity(mapActivity)
    }

    private fun logOutDialog() {
        val toast = AlertDialog.Builder(this)
        val logout = toast.create()
        toast
            .setTitle(getString(R.string.check_logOut))
            .setMessage(getString(R.string.are_you_sure_to_logout))
            .setPositiveButton(getString(R.string.no)) { _, _ ->
                logout.cancel()
            }
            .setNegativeButton(getString(R.string.yes)) { _, _ ->
                logoutUser()
            }
            .show()
    }


    private fun logoutUser() {
        val preferences = StoryUserPreference.getAccountUser(data)
        val storyUserViewModel =
            ViewModelProvider(this, ViewModelAccountPreferences(preferences))[StoryUserViewModel::class.java]
        storyUserViewModel.apply {
            saveUserLoginAuth(false)
            saveTokenAuth("")
            saveUserName("")
        }
        goToLogOut()
    }

    private  fun  goToLogOut(){
        isEnding = true
        startActivity(Intent(this, LoginUserActivity::class.java))
        finish()
    }

    private  fun getMyLocationNow(){
        val resultLocation: Task<Location> =
            LocationServices.getFusedLocationProviderClient(this).lastLocation
        resultLocation.addOnSuccessListener {
            if (it != null) {
                myDefaultLocation = LatLng(
                    it.latitude,
                    it.longitude
                )
                thisMyDefaultLocation()
                gMap.addMarker(
                    MarkerOptions().position(
                        LatLng(
                            it.latitude,
                            it.longitude
                        )
                    ).title(getString(R.string.my_location_now))
                )
                gMap.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(
                            it.latitude,
                            it.longitude
                        ), DEFAULT_ZOOM_MAP
                    )
                )
            } else {
                noHaveLocationUser()
                moveThisLocation()
                isMyLocationIsEnabled()
            }
        }
    }


    private  fun  thisMyDefaultLocation(){
        defaultLocationIsActive(true)
    }




    private  fun noHaveLocationUser(){
        clickedPlaceIsActive(false)
        Toast.makeText(
            this,
            getString(R.string.no_have_location_data),
            Toast.LENGTH_SHORT
        ).show()
    }

    private  fun  isMyLocationIsEnabled(){
        gMap.isMyLocationEnabled = false
    }

    private  fun  moveThisLocation(){
        gMap.moveCamera(
            CameraUpdateFactory
                .newLatLngZoom(myDefaultLocation(), DEFAULT_ZOOM_MAP)
        )
    }


    private fun setDefaultLocationUser() {
            try {
                if (accessThisPermission()) {
                    getMyLocationNow()

                } else {
                   locationUserPermission()
                }
            } catch (e: SecurityException) {
                Log.e(getString(R.string.error_message), e.message, e)
            }
    }

    private  fun locationUserPermission(){
       launchingRequestLocationUserPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }



    companion object {
        var myDefaultLocation: LatLng? = null
        var clickedThisLocation: LatLng? = null
        const val DEFAULT_ZOOM_MAP = 25.0f
        var LATITUDE = "Latitude"
       var LONGITUDE = "Longitude"
    }
}