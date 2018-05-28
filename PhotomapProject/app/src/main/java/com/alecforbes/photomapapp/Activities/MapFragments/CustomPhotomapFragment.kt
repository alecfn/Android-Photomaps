package com.alecforbes.photomapapp.Activities.MapFragments

import android.graphics.BitmapFactory
import android.location.Location
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.util.Log
import android.view.View
import com.alecforbes.photomapapp.Activities.MapFragments.Clustering.ImageClusterItem
import com.alecforbes.photomapapp.Activities.MapFragments.Clustering.ImageClusterRenderer
import com.alecforbes.photomapapp.Model.ImageData
import com.alecforbes.photomapapp.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.clustering.ClusterManager
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


/**
 * Created by Alec on 4/26/2018.
 */

open class CustomPhotomapFragment: SupportMapFragment(), OnMapReadyCallback, View.OnClickListener, GoogleMap.OnMarkerClickListener {

    private lateinit var photomap: GoogleMap
    private lateinit var imageClusterManager: ClusterManager<ImageClusterItem>


    // Store uris as a hashmap to check if added already (Value is unused, just the key)
    private var imageUriHashMap = HashMap<String, String>()

    private var selectedImages = ArrayList<ImageData>()

    private var isPlaceMap = false
    private var isSavedMap = false // FIXME: probably refactor this stuff

    // TODO this could be custom views later
    private var imageMarkers = ArrayList<ImageClusterItem>()
    private var timelinePolys = ArrayList<Polyline>()


    override fun onActivityCreated(p0: Bundle?) {
        super.onActivityCreated(p0)
        getMapAsync(this)

    }


    override fun onMapReady(map: GoogleMap?) {
        photomap = map as GoogleMap
        setUpClusterer()


        // todo i think place probably should just be moved to it's own class FIX ME
        if (isPlaceMap) {
            addImagePreviews()

            // fixme bug redrawing when you go back to the select screen and make a new one
            setMapBounds()
        } else if (isSavedMap){
            addImagePreviews()


        } else {

        }


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

        val markerOpts = MarkerOptions()

        selectedImages.forEach { imageData ->

            val imageUri = imageData.file.absolutePath.toString()

            if (!imageUriHashMap.containsKey(imageUri)) {

                markerOpts.position(imageData.latLong)


                // Construct the Bitmap, and set it to an image cluster item
                val thumbnail = BitmapFactory.decodeByteArray(imageData.getImageThumbnail(), 0, imageData.getImageThumbnail().size)
                val thumbnailDesc = BitmapDescriptorFactory.fromBitmap(thumbnail)

                // Image and snippet values aren't used, but necessary in method constructor
                val imageClusterItem = ImageClusterItem(imageData.latLong, "", "")
                imageClusterItem.setBitmapDesc(thumbnailDesc)

                imageClusterItem.setBitmapDesc(thumbnailDesc)
                imageClusterManager.addItem(imageClusterItem)
                imageClusterManager.cluster()

                imageMarkers.add(imageClusterItem)

                imageUriHashMap[imageUri] = ""
            }

        }

        // After all the images are added set the camera bounds to be inclusive of all images
        if(imageMarkers.size > 0) {
            this.setMapBounds()
        }


    }

    /**
     * Set the boundaries of the map to include all of the markers added so they all appear on
     * the screen.
     */
    private fun setMapBounds(){

        // TODO custom views

        val latLongBuilder = LatLngBounds.builder()

        imageMarkers.forEach{ clusterItem ->
            latLongBuilder.include(clusterItem.position)
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
    fun addTimelinePolylines(){
        val imagePolyLine = PolylineOptions() // fixme testing

        imageMarkers.forEach { clusterItem ->
            val markerLoc = clusterItem.position
            imagePolyLine.add(markerLoc)
            timelinePolys.add(photomap.addPolyline(imagePolyLine))
        }
    }

    /**
     * Remove the polylines used on the map to represent a timeline between images.
     */
    fun clearPhotoTimeline(){

        timelinePolys.forEach{ polyline ->
            polyline.remove()
        }

        timelinePolys.clear()
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

                // The following format is how exif date data fields are stored, format it
                val dateFormat = SimpleDateFormat("yyyy:MM:dd hh:mm:ss", Locale.getDefault())
                val realDate = dateFormat.parse(imageData.dateTimeTaken)

                imageData.realTimeTaken = realDate

                unixStamp = realDate.time / 1000
            } else {
                // If only one of date or time fields is present, the sorting wont be accurate set 0
                unixStamp = 0
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

    private fun setUpClusterer(){
        imageClusterManager = ClusterManager<ImageClusterItem>(this.context, photomap)
        imageClusterManager.renderer = ImageClusterRenderer(this.context, photomap, imageClusterManager)
        photomap.setOnCameraIdleListener(imageClusterManager)

        photomap.setOnMarkerClickListener(imageClusterManager)

        addImagePreviews()
    }

}