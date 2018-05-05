package com.alecforbes.photomapapp.Activities

import android.graphics.BitmapFactory
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import com.alecforbes.photomapapp.Controllers.FirebaseController
import com.alecforbes.photomapapp.Model.ImageData
import com.alecforbes.photomapapp.Model.PlacesLinksHashmap
import com.alecforbes.photomapapp.R
import com.google.firebase.storage.FirebaseStorage
import java.io.File

/**
 * A place photomap inherits methods from the Custom photomap, as some functionality is not
 * available in a place photomap
 */
class PlacePhotomap : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.N)// TODO api level
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_photomap)

        val placesIntent = intent
        val selectedLoc = placesIntent.getStringExtra("SelectedLocation")
        title = "$selectedLoc Photomap"

        // The URIs for images in a place photomap come from firebase downloads, not an intent
        val firebaseController = FirebaseController(contentResolver, this)
        firebaseController.retrieveSelectedPlaceImages(selectedLoc)

    }

    /**
     * Only once firebase has successfully retrieved images should the map fragment be created
     */
    fun onFirebaseComplete(includedImages: ArrayList<ImageData>){

        val customMapFragment = PhotomapFragment.newInstance(includedImages)

        // As the map is a fragment, initialise it in a view (but just the constraint as the map fills the view)
        supportFragmentManager.beginTransaction()
                .add(R.id.placePhotomapConstraint, customMapFragment)
                .commit()
    }


}
