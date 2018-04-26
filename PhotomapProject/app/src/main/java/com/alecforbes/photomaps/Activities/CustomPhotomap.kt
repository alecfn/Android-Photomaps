package com.alecforbes.photomaps.Activities

import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import com.alecforbes.photomaps.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import kotlinx.android.synthetic.main.acivity_photomap.*

class CustomPhotomap : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val imagesIntent = intent
        setContentView(R.layout.acivity_photomap)
        var customMapFragment : CustomPhotomapFragment?=null
        
        customMapFragment = supportFragmentManager.findFragmentById(R.id.photomapFragment) as CustomPhotomapFragment?
        customMapFragment?.getMapAsync(customMapFragment)

    }

}
