package com.alecforbes.photomapapp.Activities.Photomaps

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.alecforbes.photomapapp.Activities.MapFragments.CustomPhotomapFragment
import com.alecforbes.photomapapp.Controllers.FirebaseController
import com.alecforbes.photomapapp.Model.ImageData
import com.alecforbes.photomapapp.R

/**
 * A place photomap inherits methods from the Custom photomap, as some functionality is not
 * available in a place photomap
 */
class PlacePhotomap : AppCompatActivity() {

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
     * Only once firebase has successfully retrieved images should the map fragment be created.
     * This is called from the FirebaseController class.
     */
    fun onFirebaseComplete(includedImages: ArrayList<ImageData>){

        // fixme the new instance needs to be fixed for placesmaps now
        val placeMapFragment = CustomPhotomapFragment.newPlaceInstance(includedImages)

        //val placeMapFragment = CustomPhotomapFragment.newCustomInstance(includedImages)

        placeMapFragment.setSelectedDataFromIntent()

        // As the map is a fragment, initialise it in a view (but just the constraint as the map fills the view)
        supportFragmentManager.beginTransaction()
                .add(R.id.placePhotomapConstraint, placeMapFragment)
                .commit()

        //placeMapFragment.addImagePreviews()
        //placeMapFragment.setMapBounds()
    }


}
