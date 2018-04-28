package com.alecforbes.photomapapp.Activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.alecforbes.photomapapp.R
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = "Photomaps"

        setupButtonListeners()

        // TODO probably move to own class
        val firebaseStorage: FirebaseStorage= FirebaseStorage.getInstance()

        //val testFile = File.createTempFile("downloadtestimage", "jpg")
        val storageRef = firebaseStorage.reference
        val pathRef = storageRef.child("images/testfiledownload.jpg")
        val httpsRef = firebaseStorage.getReferenceFromUrl("https://firebasestorage.googleapis.com/v0/b/photomaps-fit3027.appspot.com/o/PlacesTestData%2FSydney%2Fharbourbridge.jpg?alt=media&token=56479978-584f-4fc9-b58d-20928e1ffd73")

        print("")

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
            val placesIntent = Intent(this, PlacesList::class.java)
            startActivity(placesIntent)
        }

        savedMapButton.setOnClickListener {

        }

    }

    private fun requestPermissions(){
        // TODO: just doing this in main for now, may move to more appropriate locations

      //  val gpsPermission = ContextCompat.checkSelfPermission(this, Manifest.permission)

    }
}
