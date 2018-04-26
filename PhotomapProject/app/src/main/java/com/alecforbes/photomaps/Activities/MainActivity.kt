package com.alecforbes.photomaps.Activities

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import com.alecforbes.photomaps.Manifest
import com.alecforbes.photomaps.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupButtonListeners()
    }

    private fun setupButtonListeners(){

        // Get the buttons with Kotlin extensions, so we don't have to use findById
        val createmMapButton = createPhotomapButton
        val placesMapButton = placesPhotomapButton
        val savedMapButton = savedPhotomapsButton

        createmMapButton.setOnClickListener {
            // Send the map creation intent
            val createIntent = Intent(this, PhotoSelection::class.java)
            startActivity(createIntent)

        }

        placesMapButton.setOnClickListener {

        }

        savedMapButton.setOnClickListener {

        }

    }

    private fun requestPermissions(){
        // TODO: just doing this in main for now, may move to more appropriate locations

      //  val gpsPermission = ContextCompat.checkSelfPermission(this, Manifest.permission)

    }
}
