package com.alecforbes.photomaps.Activities

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment

/**
 * Created by Alec on 4/26/2018.
 */

class CustomPhotomapFragment: SupportMapFragment(), OnMapReadyCallback {

    private lateinit var map: GoogleMap

    override fun onMapReady(p0: GoogleMap?) {
        map = p0 as GoogleMap

        //TODO Populate map with custom views based on image data passed in
    }
}