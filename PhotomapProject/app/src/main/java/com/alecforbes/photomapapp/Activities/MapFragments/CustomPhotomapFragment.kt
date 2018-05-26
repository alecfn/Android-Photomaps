package com.alecforbes.photomapapp.Activities.MapFragments

import android.graphics.BitmapFactory
import android.location.Location
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.util.Log
import android.view.View
import com.alecforbes.photomapapp.Model.ImageData
import com.alecforbes.photomapapp.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


/**
 * Created by Alec on 4/26/2018.
 * The base class from which individual map fragments inherit from.
 *
 * The 'open' modifier defines the class as not being final and can be inherited from (by default
 * classes are final in Kotlin)
 */

open class CustomPhotomapFragment: SupportMapFragment(), OnMapReadyCallback, View.OnClickListener, GoogleMap.OnMarkerClickListener {

    private lateinit var photomap: GoogleMap
    private lateinit var lastLoc: Location
    private var screenSize: Int? = null

    // Store uris as a hashmap to check if added already (Value is unused, just the key)
    private var imageUriHashMap = HashMap<String, String>()

    private var selectedImages = ArrayList<ImageData>()
    //private var sortedImages = ArrayList<ImageData>()
    private var isPlaceMap = false
    private var isSavedMap = false // FIXME: probably refactor this stuff

    // TODO this could be custom views later
    private var markers = ArrayList<Marker>()


    override fun onActivityCreated(p0: Bundle?) {
        super.onActivityCreated(p0)
        getMapAsync(this)

    }


    override fun onMapReady(p0: GoogleMap?) {
        photomap = p0 as GoogleMap

        // todo i think place probably should just be moved to it's own class FIX ME
        if (isPlaceMap) {
            addImagePreviews()

            // fixme bug redrawing when you go back to the select screen and make a new one
            setMapBounds()
        } else if (isSavedMap){
            addImagePreviews()


        } else {

        }


        //TODO Populate photomap with custom views based on image data passed in

    }

    //todo fix descriptions n such

    /**
     * Define the necessary objects for the map fragment when the instance is created. This does not
     * call the lifecycle functions like onMapReady, the fragment must be linked to a view for that.
     *
     *
     * Define placemap objects
     */
    companion object {
        fun newCustomInstance(): CustomPhotomapFragment {
            val fragment = CustomPhotomapFragment()
            return fragment
        }

        fun newPlaceInstance(images: ArrayList<ImageData>): CustomPhotomapFragment{
            val fragment = CustomPhotomapFragment()
            val args = Bundle()
            args.putParcelableArrayList("selectedData", images)
            fragment.arguments = args
            fragment.isPlaceMap = true
            return fragment
        }

        fun newSavedInstance(savedImages: ArrayList<ImageData>): CustomPhotomapFragment{
            val fragment = CustomPhotomapFragment()
            val args = Bundle()
            args.putParcelableArrayList("savedData", savedImages)
            fragment.arguments = args
            fragment.isSavedMap = true

            return fragment
        }
    }



    fun setSelectedData(selectedData: ArrayList<ImageData>){
        selectedImages = selectedData
    }

    fun setSelectedDataFromIntent(){
        selectedImages = arguments.getParcelableArrayList<ImageData>("selectedData")
    }

    /**
     * Add the images to the photomap fragment as previews from the retrieved data
     */
    fun addImagePreviews(){


        //selectedData = arguments.getParcelableArrayList<ImageData>("selectedData")

        val markerOpts = MarkerOptions()

        selectedImages.forEach {

            val imageUri = it.file.absolutePath.toString()

            if (!imageUriHashMap.containsKey(imageUri)) {

                markerOpts.position(it.latLong)

                val thumbnail = BitmapFactory.decodeByteArray(it.getImageThumbnail(), 0, it.getImageThumbnail().size)
                val thumbnailDesc = BitmapDescriptorFactory.fromBitmap(thumbnail)
                markerOpts.icon(thumbnailDesc)

                val marker = photomap.addMarker(markerOpts)
                marker.isDraggable  // TODO, make movable and edit file data with new loc?
                markers.add(marker)

                imageUriHashMap[imageUri] = ""
            }

        }



    }

    /**
     * Set the boundaries of the map to include all of the markers added so they all appear on
     * the screen.
     */
    fun setMapBounds(){

        // TODO custom views

        val latLongBuilder = LatLngBounds.builder()

        markers.forEach{
            latLongBuilder.include(it.position)
        }

        val mapBounds = latLongBuilder.build()
        val pad = 200 // Map pixel padding
        val cameraUpdate = CameraUpdateFactory.newLatLngBounds(mapBounds, pad)


        photomap.moveCamera(cameraUpdate)

    }

    /**
     * Add a polyLine drawn between two images on the map. These will be in succession of when the
     * image was taken (based on how the selectedImages list is sorted).
     */
    fun addPhotoTimeline(){
        val testLine = PolylineOptions() // fixme testing

        markers.forEach {
            val markerLoc = it.position
            testLine.add(markerLoc)
            photomap.addPolyline(testLine)
        }
    }

    fun clearPhotoTimeline(){

    }

    /**
     * Clear all markers and polylines from the map, and invalidate the timeline preview of parent.
     */
    fun clearMap(){
        photomap.clear()
        imageUriHashMap.clear() // Also clear the hashmap, or no new data can be added
    }

    /**
     * Sort the selected images by the time they were taken so they can appear in order in the
     * preview. Unfortunately some newer Java/Kotlin features like .parse for a datetime don't work
     * except in Android O, so the process is a bit more complicated here.
     */
    fun sortByTimeTaken(){

        selectedImages.forEach{ imageData ->

            // Set the unix time stamp on each object by converting timestamp values and use to sort

            val dateTime = imageData.dateTimeTaken.split(" ")

            imageData.datetaken = "0"
            imageData.timeTaken = "0"
            try {
                imageData.datetaken = dateTime[0]
                imageData.timeTaken = dateTime[1]
            }catch (indexEx: IndexOutOfBoundsException){
                // Some images may not have both date and time, only one, just cont.
                Log.e("Bad dateTime","Image missing time or date value, set to default of 0")
            }


            var unixStamp = 0L

            if (imageData.datetaken != "0" && imageData.timeTaken != "0") {
                val dateComponents = imageData.datetaken.split(":")
                val timeComponents = imageData.timeTaken.split(":")

                // FIXME: is the hour correct? keeps coming back as 0
                val cal = Calendar.getInstance()
                cal.set(dateComponents[0].toInt(), dateComponents[1].toInt(), dateComponents[2].toInt(),
                        timeComponents[0].toInt(), timeComponents[1].toInt(), timeComponents[2].toInt())

                unixStamp = cal.timeInMillis / 1000
            }

            imageData.unixTime = unixStamp
        }

        selectedImages.sort()

    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        // tODO

        return true

    }

    override fun onClick(view: View?) {

        // fixme not being called?
        val displayedImageView = parentFragment.activity.findViewById<ConstraintLayout>(R.id.indvImageViewConstraint)

        displayedImageView.invalidate()
        displayedImageView.visibility = View.GONE
    }

}