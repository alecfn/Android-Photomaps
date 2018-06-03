package com.alecforbes.photomapapp.Activities.Photomaps

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.alecforbes.photomapapp.Activities.MapFragments.CustomPhotomapFragment
import com.alecforbes.photomapapp.Controllers.FirebaseController
import com.alecforbes.photomapapp.Model.ImageData
import com.alecforbes.photomapapp.R
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.model.Marker
import kotlinx.android.synthetic.main.activity_photomap.*
import kotlinx.android.synthetic.main.activity_place_photomap.*
import kotlinx.android.synthetic.main.timeline_scroll.*
import kotlinx.android.synthetic.main.place_individual_image_view.*
import kotlinx.android.synthetic.main.place_individual_image_view.view.*

/**
 * A place photomap inherits methods from the Custom photomap, as some functionality is not
 * available in a place photomap
 */
class PlacePhotomap : PhotomapActivity() {

    lateinit var firebaseController: FirebaseController

    var imageInfoView: ViewGroup? = null
    var placeMapFragment: CustomPhotomapFragment? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_photomap)

        setScreenResolution()

        val placesIntent = intent
        val selectedLoc = placesIntent.getStringExtra("SelectedLocation")
        title = "$selectedLoc Photomap"

        // The URIs for images in a place photomap come from firebase downloads, not an intent
        firebaseController = FirebaseController(contentResolver, this)
        firebaseController.retrieveSelectedPlaceImages(selectedLoc)

    }

    /**
     * Only once firebase has successfully retrieved images should the map fragment be created.
     * This is called from the FirebaseController class.
     */
    fun onFirebaseComplete(includedImages: ArrayList<ImageData>){

        // fixme the new instance needs to be fixed for placesmaps now
        placeMapFragment = CustomPhotomapFragment.newPlaceInstance(includedImages)

        //val placeMapFragment = CustomPhotomapFragment.newCustomInstance(includedImages)

        placeMapFragment!!.setSelectedDataFromIntent()

        // Create a horizontal scroll view for the place images, similar to the custom map

        createFirebaseImageScrollView()

        // As the map is a fragment, initialise it in a view (but just the constraint as the map fills the view)
        supportFragmentManager.beginTransaction()
                .add(R.id.placePhotomapConstraint, placeMapFragment)
                .commit()

        //placeMapFragment.addImagePreviews()
        //placeMapFragment.setMapBounds()
    }

    /**
     * Create a scroll view for the images on the map, similar to the one used on custom maps.
     *
     * This uses the same layout resource as the custom map but populates with firebase images.
     */
    private fun createFirebaseImageScrollView(){
        placeScrollview.visibility = View.VISIBLE

        firebaseController.includedImages.forEach{ imageData ->

            val placeImageButton = defineHorizontalScrollViewButton()

            placeImageButton.setImageBitmap(imageData.getImageBitmap())

            placeImageButton.setOnClickListener {

            }

            imagePreviewPane.addView(placeImageButton)

        }
    }

    /**
     * Create individual images when images are clicked on a place map. This is not the same
     * as what is created on the custom maps, as the information for the images is different.
     *
     */
    fun createIndvFirebaseImageView(clickedMarker: Marker?){

        val markerLatLong = clickedMarker!!.position
        // From the lat long of the marker, get the relevant image file to populate the view
        // todo, could maybe override the marker class and add some kind of id field?
        var clickedImageData: ImageData
        firebaseController.includedImages.forEach{ imageData ->

            if (imageData.latLong == markerLatLong){
                clickedImageData = imageData
            }
        }

        // Now create the new view from the image data
        val fragmentViewGroup = placeMapFragment!!.view as ViewGroup
        imageInfoView = View.inflate(applicationContext, R.layout.place_individual_image_view, fragmentViewGroup) as ViewGroup
        imageInfoView!!.placeAddressValue.text = "blub"
        imageInfoView!!.elevation = 6f
        imageInfoView!!.visibility = View.VISIBLE
        onResumeFragments()

    }

    override fun onResumeFragments() {
        super.onResumeFragments()

        //placeScrollview.bringToFront()
        if (imageInfoView != null) {
            imageInfoView!!.bringToFront()
        }

    }


}
