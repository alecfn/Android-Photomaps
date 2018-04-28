package com.alecforbes.photomapapp.Activities

import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.alecforbes.photomapapp.Model.ImageData
import com.alecforbes.photomapapp.R
import com.google.firebase.storage.FirebaseStorage

/**
 * A place photomap inherits methods from the Custom photomap, as some functionality is not
 * available in a place photomap
 */
class PlacePhotomap : AppCompatActivity() {

    val firebaseStorage: FirebaseStorage = FirebaseStorage.getInstance()
    var imageBytes = ArrayList<ByteArray>()
    var firebaseImageUris = ArrayList<Uri>()
    var includedImages = ArrayList<ImageData>()

    //@TargetApi(Build.VERSION_CODES.N) // TODO api level
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // FIXME should either call super or not inherit here
        setContentView(R.layout.activity_place_photomap)

        val placesIntent = intent
        val selectedLoc = placesIntent.getStringExtra("SelectedLocation")
        title = "$selectedLoc Photomap"

        retrieveSelectedPlaceImages(selectedLoc)

        // The URIs for images in a place photomap come from firebase downloads, not an intent



        val customMapFragment = PhotomapFragment.newInstance(includedImages)

        // As the map is a fragment, initialise it in a view (but just the constraint as the map fills the view)
        supportFragmentManager.beginTransaction()
                .add(R.id.placePhotomapConstraint, customMapFragment)
                .commit()

    }

    /**
     * Get the images from firebase for the selected location and return the array of URIs
     */
    private fun retrieveSelectedPlaceImages(placeName: String){
        // TODO probably move to own class
        // Handle cases to download the correct data
        //val testFile = File.createTempFile("downloadtestimage", "jpg")
        val storageRef = firebaseStorage.reference
        val storagePathRef = storageRef.child("PlacesTestData/Sydney/harbourbridge.jpg")
        //val httpsRef = firebaseStorage.getReferenceFromUrl("https://firebasestorage.googleapis.com/v0/b/photomaps-fit3027.appspot.com/o/PlacesTestData%2FSydney%2Fharbourbridge.jpg?alt=media&token=56479978-584f-4fc9-b58d-20928e1ffd73")

        storagePathRef.downloadUrl.addOnSuccessListener {
            uri ->
            firebaseImageUris.add(uri)
        }

        // TODO  add a progress bar
        // TODO 1mb limit at the moment, increase if needed
//        val MAX_SIZE = (1024 * 1024).toLong()
//
//        // Use get bytes as the images will only be stored in memory, rather than on the device
//        httpsRef.getBytes(MAX_SIZE)
//                .addOnSuccessListener { bytes ->
//                    imageBytes.add(bytes)
//                    print("")
//                }
//                .addOnFailureListener { exception ->
//                    //TODO
//                }
//        print("")


    }
}
