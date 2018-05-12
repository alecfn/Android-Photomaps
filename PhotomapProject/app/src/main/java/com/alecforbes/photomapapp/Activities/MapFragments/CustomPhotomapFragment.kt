package com.alecforbes.photomapapp.Activities.MapFragments

import android.content.Context
import android.graphics.BitmapFactory
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import com.alecforbes.photomapapp.Model.ImageData
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*


/**
 * Created by Alec on 4/26/2018.
 * The base class from which individual map fragments inherit from.
 *
 * The 'open' modifier defines the class as not being final and can be inherited from (by default
 * classes are final in Kotlin)
 */

open class CustomPhotomapFragment: SupportMapFragment(), OnMapReadyCallback {

    private lateinit var photomap: GoogleMap
    private lateinit var lastLoc: Location
    private var selectedImages = ArrayList<ImageData>()
    private var isPlaceMap = false

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
        } else{
            // Set camera to current location
            //locationMan.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            //val currLatLng = LatLng(locationMan.ge)
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

            markerOpts.position(it.latLong)

            val thumbnail = BitmapFactory.decodeByteArray(it.getImageThumbnail(), 0, it.getImageThumbnail().size)
            val thumbnailDesc = BitmapDescriptorFactory.fromBitmap(thumbnail)
            markerOpts.icon(thumbnailDesc)

            val marker = photomap.addMarker(markerOpts)
            marker.isDraggable  // TODO, make movable and edit file data with new loc?
            markers.add(marker)


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

    fun clearMap(){

    }

    /**
     * Sort the selected images by the time they were taken
     */
    private fun sortByTimeTaken(){

    }



}