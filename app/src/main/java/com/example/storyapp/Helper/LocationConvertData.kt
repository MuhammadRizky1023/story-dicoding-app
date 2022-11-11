package com.example.storyapp.Helper


import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import com.example.storyapp.R
import com.example.storyapp.UI.Activities.LocationUserActivity
import com.example.storyapp.UI.AddListStoryActivity
import com.example.storyapp.UI.DetailListStoryActivity

import com.google.android.gms.maps.model.LatLng
import java.lang.Exception
import java.lang.StringBuilder

class LocationConvertData {
    companion object {
        const val  MAP = "MAP"
        fun getLocationConvert(
            locationData: LatLng?,
            context: Context
        ): String {
            var locationUser = context.resources.getString(R.string.no_have_location)
            try {
                if (locationData != null) {
                    val location: Address?
                    val geocoder = Geocoder(context)
                    val locationList: List<Address> =
                        geocoder.getFromLocation(locationData.latitude, locationData.longitude, 1)
                    location =
                        if (locationList.isNotEmpty()){
                        locationList[0]
                    } else{
                       null
                    }

                    if (location !== null) {
                        val locality = location.locality
                        val adminArea = location.adminArea
                        val countryName = location.countryName

                        locationUser  = location.getAddressLine(0)
                            ?: if (locality !== null && adminArea !== null) {
                                StringBuilder(locality).append(", $adminArea").append(", $countryName")
                                    .toString()
                            } else if (adminArea!== null && countryName !== null) {
                                StringBuilder(adminArea).append(", $countryName").toString()
                            } else{
                                countryName ?: context.resources.getString(R.string.location_name_unknown)
                            }
                    }
                }
            } catch (e: Exception) {
                Log.d(MAP, "ERROR: $e")
            }


            return locationUser
        }
    }
}