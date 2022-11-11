package com.example.storyapp.Helper


import com.google.android.gms.maps.model.LatLng


class MapConvertData {
    companion object {
        fun mapConvertData(latitude: Double?, longitude: Double?): LatLng? {
            return if (latitude != null && longitude != null) {
                LatLng(latitude, longitude)

            } else{
                return null
            }
        }

    }
}