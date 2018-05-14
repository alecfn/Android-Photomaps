package com.alecforbes.photomapapp.Activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.alecforbes.photomapapp.Activities.Photomaps.CustomPhotomap
import com.alecforbes.photomapapp.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        // FIXME this layout should be a proper card view
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = "Photomaps"

        setupButtonListeners()

    }

    private fun setupButtonListeners(){

        // Get the buttons with Kotlin extensions, so we don't have to use findById
        val createmMapButton = createPhotomapButton
        val placesMapButton = createPlacesButton
        val savedMapButton = savedPhotomapButton

        createmMapButton.setOnClickListener {
            // Send the map creation intent
            val createIntent = Intent(this, CustomPhotomap::class.java)
            startActivity(createIntent)

        }

        placesMapButton.setOnClickListener {
            val placesIntent = Intent(this, PlacesList::class.java)
            startActivity(placesIntent)
        }

        savedMapButton.setOnClickListener {
            val savedPlacesIntent = Intent(this, SavedMaps::class.java)
            startActivity(savedPlacesIntent)
        }

    }

    private fun requestPermissions(){
        // TODO: just doing this in main for now, may move to more appropriate locations

      //  val gpsPermission = ContextCompat.checkSelfPermission(this, Manifest.permission)

    }
}
