package com.alecforbes.photomapapp.Activities.Photomaps

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.alecforbes.photomapapp.Activities.MapFragments.PhotomapFragment
import com.alecforbes.photomapapp.Controllers.FirebaseController
import com.alecforbes.photomapapp.Controllers.ImageGeocoder
import com.alecforbes.photomapapp.Controllers.WikipediaRetriever
import com.alecforbes.photomapapp.Model.ImageData
import com.alecforbes.photomapapp.R
import com.google.android.gms.maps.model.Marker
import kotlinx.android.synthetic.main.activity_place_photomap.*
import kotlinx.android.synthetic.main.individual_image_view.*
import kotlinx.android.synthetic.main.place_individual_image_view.view.*
import org.jetbrains.anko.doAsyncResult

/**
 * A place photomap inherits methods from the Custom photomap, as some functionality is not
 * available in a place photomap
 */
class PlacePhotomap : PhotomapActivity() {

    lateinit var firebaseController: FirebaseController

    var imageInfoView: ViewGroup? = null
    var placeMapFragment: PhotomapFragment? = null
    var selectedLoc: String? = null

    val WIKI_LINK_POS = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_photomap)

        setScreenResolution()

        val placesIntent = intent
        selectedLoc = placesIntent.getStringExtra("SelectedLocation")
        title = "$selectedLoc Photomap"

        // The URIs for images in a place photomap come from firebase downloads, not an intent
        firebaseController = FirebaseController(contentResolver, this)
        firebaseController.retrieveSelectedPlaceImages(selectedLoc!!)

    }

    /**
     * Only once firebase has successfully retrieved images should the map fragment be created.
     * This is called from the FirebaseController class.
     */
    fun onFirebaseComplete(includedImages: ArrayList<ImageData>){

        placeMapFragment = PhotomapFragment.newPlaceInstance(includedImages)

        placeMapFragment!!.setSelectedDataFromIntent()
        // Create a horizontal scroll view for the place images, similar to the custom map

        // As the map is a fragment, initialise it in a view (but just the constraint as the map fills the view)
        supportFragmentManager.beginTransaction()
                .add(R.id.placePhotomapConstraint, placeMapFragment)
                .commit()

    }

    /**
     * Create individual images when images are clicked on a place map. This is not the same
     * as what is created on the custom maps, as the information for the images is different.
     *
     */
    fun createIndvFirebaseImageView(clickedMarker: Marker?){

        val markerLatLong = clickedMarker!!.position

        // From the lat long of the marker, get the relevant image file to populate the view
        var clickedImageData: ImageData? = null
        firebaseController.includedImages.forEach{ imageData ->

            if (imageData.latLong == markerLatLong){
                clickedImageData = imageData
            }
        }

        // fixme threading would be good here
        // Now create the new view from the image data
        val fragmentViewGroup = placeMapFragment!!.view as ViewGroup
        imageInfoView = View.inflate(applicationContext, R.layout.place_individual_image_view, fragmentViewGroup) as ViewGroup

        // Populate the address field with the geocoder as on a custom map
        // fixme needs to be own thread, very slow
        if (clickedImageData!!.realAddress == null) {
            // Only call the Geocoder if address hasn't been found before
            val imageGeocoder = ImageGeocoder(clickedImageData!!.latitude.toDouble(), clickedImageData!!.longitude.toDouble(), applicationContext)
            val imageAddress = imageGeocoder.getAddressFromLocation()
            clickedImageData!!.realAddress = imageAddress
        }

        imageInfoView!!.placeAddressValue.text = clickedImageData!!.realAddress
        imageInfoView!!.placeIndvImageView.setImageBitmap(clickedImageData!!.getImageBitmap())

        // Populate the description with the first paragraph on the landmark from Wikipedia todo

        getWikipediaDesc(clickedImageData!!.getAssociatedLinks()!![WIKI_LINK_POS])

        // Set button listeners
        imageInfoView!!.placeCloseButton.setOnClickListener{
            imageInfoView!!.visibility = View.GONE
            imageInfoView!!.invalidate()
        }

        imageInfoView!!.placeViewInMapsButton.setOnClickListener {
            startMapsFromAddress(clickedImageData!!.realAddress!!)
        }

        imageInfoView!!.elevation = 6f
        imageInfoView!!.bringToFront()
        imageInfoView!!.visibility = View.VISIBLE

    }

    private fun getWikipediaDesc(wikiUrl: String){
        val wikiRetriever = WikipediaRetriever()

        // Anko library allows running asynchronous tasks easily like so
        var descriptionPara: String? = null
        doAsyncResult {
            descriptionPara = wikiRetriever.getFirstParagraphFromWikipedia(wikiUrl)
            imageInfoView!!.placeDescValue.text = descriptionPara.toString()

        }

        // Due to limit screen size, limit the paragraph to be no more than 250 characters

        // todo could also add a 'read more' button that opens the wikipage
    }


}
