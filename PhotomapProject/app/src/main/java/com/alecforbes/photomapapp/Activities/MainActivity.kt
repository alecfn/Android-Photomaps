package com.alecforbes.photomapapp.Activities

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import com.alecforbes.photomapapp.Activities.Photomaps.CustomPhotomap
import com.alecforbes.photomapapp.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val READ_EXTERNAL_REQUEST_CODE = 101
    private val WRITE_EXTERNAL_REQUEST_CODE = 102

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = "Photomaps"

        setupButtonListeners()
        setupPermissions()

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

    /**
     * Request the relevant needed permissions for creating a photomap at run time.
     *
     * There are read/write external storage
     */
    private fun setupPermissions(){
        // TODO: just doing this in main for now, may move to more appropriate locations

        val readPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
        val writePermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

        val permissionGranted = PackageManager.PERMISSION_GRANTED

        if (readPermission != permissionGranted){
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    READ_EXTERNAL_REQUEST_CODE)
        }

        if (writePermission != permissionGranted){
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    WRITE_EXTERNAL_REQUEST_CODE)
        }

    }

    /**
     * Handle responses to permission requests, the app needs access to internal files so if
     * this is not done the user cannot create maps so if not granted ask again.
     *
     * However if the user does not give write it just means maps cannot be saved, we can work
     * without that.
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val permissionDenied = PackageManager.PERMISSION_DENIED

        // Handle denials
        when(requestCode) {
            READ_EXTERNAL_REQUEST_CODE -> {
            if ((grantResults.isNotEmpty() && grantResults[0] == permissionDenied)){
                    // We need read permission to perform basic functions
                }
            }
            WRITE_EXTERNAL_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == permissionDenied)){
                    // Maps can't be saved without write, but that's all
                    //todo
                }

            }
        }

        }
}
