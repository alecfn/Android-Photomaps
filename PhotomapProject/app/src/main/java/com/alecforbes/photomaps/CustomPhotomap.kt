package com.alecforbes.photomaps

import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.OnMapReadyCallback

class CustomPhotomap : FragmentActivity(), OnMapReadyCallback {

    override fun onMapReady(p0: GoogleMap?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val imagesIntent = intent
        setContentView(R.layout.acivity_photomap)
        
        val customMapFragment = supportFragmentManager.findFragmentById(R.id.photomapFragment) as MapFragment
        customMapFragment.getMapAsync(customMapFragment)




    }

}
