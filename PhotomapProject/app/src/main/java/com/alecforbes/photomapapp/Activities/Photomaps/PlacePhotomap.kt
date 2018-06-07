package com.alecforbes.photomapapp.Activities.Photomaps

import android.content.Intent
import android.net.Uri
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
import kotlinx.android.synthetic.main.place_individual_image_view.view.*
import org.jetbrains.anko.doAsyncResult

/**
 * This class defines a place map activity, and creates the relevant Photomap fragment instance.
 * It also generates the card views with individual information about each image on a place map
 * when a market is clicked.
 */
class PlacePhotomap : PhotomapActivity() {

    lateinit var firebaseController: FirebaseController

    var imageInfoView: ViewGroup? = null
    var placeMapFragment: PhotomapFragment? = null
    var selectedLoc: String? = null

    private val wikiLinkPos = 1 // Position of Wikipedia link in the associatedLinks list of ImageData


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

        // Now create the new view from the image data
        val fragmentViewGroup = placeMapFragment!!.view as ViewGroup
        imageInfoView = View.inflate(applicationContext, R.layout.place_individual_image_view, fragmentViewGroup) as ViewGroup

        // Populate the address field with the geocoder as on a custom map
        if (clickedImageData!!.realAddress == null) {
            // Only call the Geocoder if address hasn't been found before
            val imageGeocoder = ImageGeocoder(clickedImageData!!.latitude.toDouble(), clickedImageData!!.longitude.toDouble(), applicationContext)
            val imageAddress = imageGeocoder.getAddressFromLocation()
            clickedImageData!!.realAddress = imageAddress
        }

        // Set read more listener to open relevant Wikipedia page
        imageInfoView!!.readMoreButton.setOnClickListener{
            // https://stackoverflow.com/questions/3004515/sending-an-intent-to-browser-to-open-specific-url
            val wikiUrl = clickedImageData!!.getAssociatedLinks()!![wikiLinkPos]
            val wikiIntent = Intent(Intent.ACTION_VIEW)
            wikiIntent.data = Uri.parse(wikiUrl)
            startActivity(wikiIntent)
        }

        imageInfoView!!.placeAddressValue.text = clickedImageData!!.realAddress
        imageInfoView!!.placeIndvImageView.setImageBitmap(clickedImageData!!.getImageBitmap())

        // Populate the description with the first paragraph on the landmark from Wikipedia

        getWikipediaDesc(clickedImageData!!.getAssociatedLinks()!![wikiLinkPos])

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

    /**
     * Call the WikipediaRetriever object and get the first paragraph in the webpage to use
     * as a description of a place landmark.
     */
    private fun getWikipediaDesc(wikiUrl: String){
        val wikiRetriever = WikipediaRetriever()

        // Anko library allows running asynchronous tasks easily like so
        var descriptionPara: String?
        doAsyncResult {
            descriptionPara = wikiRetriever.getFirstParagraphFromWikipedia(wikiUrl)

            // Reduce the length of the paragraph to up 250 characters so the view doesn't clutter
            val reducedDesc = descriptionPara!!.substring(0, Math.min(descriptionPara!!.length, 250)) +
                    "..."
            imageInfoView!!.placeDescValue.text = reducedDesc

        }

    }

    override fun onDestroy() {
        supportFragmentManager.beginTransaction().remove(placeMapFragment).commitAllowingStateLoss()
        super.onDestroy()
    }

}
