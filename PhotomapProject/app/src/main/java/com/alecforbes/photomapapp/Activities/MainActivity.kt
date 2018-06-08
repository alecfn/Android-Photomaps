package com.alecforbes.photomapapp.Activities

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.alecforbes.photomapapp.Activities.Photomaps.CustomPhotomap
import com.alecforbes.photomapapp.R
import kotlinx.android.synthetic.main.activity_main.*

/**
 * The main activity for the program. Provides navigation to the custom photomap screen, place
 * screen and saved maps. Also handles initial permission requests.
 */
class MainActivity : AppCompatActivity() {

    private val readExternalRequestCode = 101
    private val writeExternalRequestCode = 102

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
     * There are read/write external storage, though in most modern Android versions this is just
     * 'storage' permission. As long as one is given, the app should work fine.
     */
    private fun setupPermissions(){

        val readPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
        val writePermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

        val permissionGranted = PackageManager.PERMISSION_GRANTED

        if (readPermission != permissionGranted){
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    readExternalRequestCode)
        }

        if (writePermission != permissionGranted){
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    writeExternalRequestCode)
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
            readExternalRequestCode -> {
            if ((grantResults.isNotEmpty() && grantResults[0] == permissionDenied)){
                    // We need read permission to perform basic functions, ask again
                    setupPermissions()
                }
            }
            writeExternalRequestCode -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == permissionDenied)){
                    // Maps can't be saved without write, but that's all, let the user know
                    val noWriteDialog = AlertDialog.Builder(this)

                    with(noWriteDialog){
                        noWriteDialog.setTitle("Without write permission, maps will not be able " +
                                "to be saved, but you can still create them.")

                        setPositiveButton("Ok"){
                            _, _ ->
                            // Do nothing, just accept
                        }
                    }

                    noWriteDialog.create()
                    noWriteDialog.show()
                }

            }
        }

        }
}
