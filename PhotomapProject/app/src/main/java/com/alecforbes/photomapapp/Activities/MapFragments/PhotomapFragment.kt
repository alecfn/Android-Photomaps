package com.alecforbes.photomapapp.Activities.MapFragments

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.util.Log
import android.view.View
import com.alecforbes.photomapapp.Activities.MapFragments.Clustering.ImageClusterItem
import com.alecforbes.photomapapp.Activities.MapFragments.Clustering.ImageClusterManager
import com.alecforbes.photomapapp.Activities.MapFragments.Clustering.ImageClusterRenderer
import com.alecforbes.photomapapp.Activities.Photomaps.CustomPhotomap
import com.alecforbes.photomapapp.Activities.Photomaps.PhotomapActivity
import com.alecforbes.photomapapp.Activities.Photomaps.PlacePhotomap
import com.alecforbes.photomapapp.Model.ImageData
import com.alecforbes.photomapapp.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.clustering.ClusterManager.OnClusterItemClickListener
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


/**
 * Created by Alec on 4/26/2018.
 */

open class PhotomapFragment : SupportMapFragment(), OnMapReadyCallback, View.OnClickListener, GoogleMap.OnMarkerClickListener, OnClusterItemClickListener<ImageClusterItem> {


    private lateinit var photomap: GoogleMap
    private lateinit var imageClusterManager: ImageClusterManager


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
            // fixme this call likes to break alot.. maybe a threading issue?
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
        fun newCustomInstance(): PhotomapFragment {
            val fragment = PhotomapFragment()
            return fragment
        }

        fun newPlaceInstance(images: ArrayList<ImageData>): PhotomapFragment{
            val fragment = PhotomapFragment()
            val args = Bundle()
            args.putParcelableArrayList("selectedData", images)

            fragment.arguments = args
            fragment.isPlaceMap = true
            return fragment
        }

        fun newSavedInstance(savedImages: ArrayList<ImageData>): PhotomapFragment{
            val fragment = PhotomapFragment()
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
                var thumbnail: Bitmap? = null
                if (!isPlaceMap) {
                    thumbnail = BitmapFactory.decodeByteArray(imageData.getImageThumbnail(), 0, imageData.getImageThumbnail().size)
                } else if (isPlaceMap){
                    // Downloaded place image thumbnail icons need to be generated from the uri
                    val thumbnailData = BitmapFactory.decodeFile(imageUri)
                    //if (activity.screenSize)
                    thumbnail = Bitmap.createScaledBitmap(thumbnailData, 250, 250, false)
                }

                val thumbnailDesc = BitmapDescriptorFactory.fromBitmap(thumbnail)

                // Image and snippet values aren't used, but necessary in method constructor
                val imageClusterItem = ImageClusterItem(imageData.latLong, "", "")

                imageClusterItem.setThumbnailBitmap(thumbnail!!)

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

        try {
            photomap.moveCamera(cameraUpdate)
        }catch (ex: Exception){
            // fixme, this breaks when a saved map is loaded
        }



    }

    /**
     * Add a polyLine drawn between two images on the map. These will be in succession of when the
     * image was taken (based on how the selectedImages list is sorted).
     */
    fun addTimelinePolylines(){
        val imagePolyLine = PolylineOptions()
        // Set some style options for the lines joining images
                .color(Color.BLUE)
                .startCap(RoundCap())
                .endCap(RoundCap())
                .width(10F)
                .geodesic(true)


        imageMarkers.forEach { clusterItem ->
            val markerLoc = clusterItem.position


            imagePolyLine.add(markerLoc)
            timelinePolys.add(photomap.addPolyline(imagePolyLine))
        }
    }

    /**
     * Remove the polylines used on the map to represent a timeline between images.
     */
    fun clearTimelinePolylines(){

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
        imageMarkers.clear() // Clear the image markers stored
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
                val dateTimeFormat = SimpleDateFormat("yyyy:MM:dd hh:mm:ss", Locale.getDefault())
                val realDateTime = dateTimeFormat.parse(imageData.dateTimeTaken)

                imageData.realTimeTaken = realDateTime

                unixStamp = realDateTime.time / 1000
            } else if (imageData.datetaken != "0") {
                // If there's just a date, we can still get a unix time
                val dateFormat = SimpleDateFormat("yyyy:MM:dd", Locale.getDefault())
                val realDate = dateFormat.parse(imageData.datetaken)

                imageData.realTimeTaken = realDate
                unixStamp = realDate.time / 1000
            } else{
                // If there's neither a date of time, just set to 0
                unixStamp = 0
            }

            imageData.unixTime = unixStamp
        }

        selectedImages.sort()

    }

    override fun onMarkerClick(marker: Marker?): Boolean {

        if (isPlaceMap){
            val parent = activity as PlacePhotomap
            parent.createIndvFirebaseImageView(marker)

        }else { // Is a custom map
            val parent = activity as CustomPhotomap
            // Get the image from the marker LatLong passed
            parent.getImageDataFromMarker(marker)
        }

        return true

    }

    override fun onClick(view: View?) {

        // fixme not being called?
        val displayedImageView = parentFragment.activity.findViewById<ConstraintLayout>(R.id.indvImageViewConstraint)

        displayedImageView.invalidate()
        displayedImageView.visibility = View.GONE
    }

    private fun setUpClusterer(){
        val cameraIdleListenter = GoogleMap.OnCameraIdleListener {  }
        imageClusterManager = ImageClusterManager(this.context, photomap, cameraIdleListenter)

        // Set the map type to know how to perform additional rendering
        val parent: PhotomapActivity?
        // Get the parent activity to give the cluster the thumbnail size
        if(isPlaceMap){
            imageClusterManager.isPlaceMap = true
            parent = activity as PlacePhotomap
        } else {
            parent = activity as CustomPhotomap
        }

        val imageClusterRenderer = ImageClusterRenderer(this.context, photomap, imageClusterManager,
                parent.THUMBNAIL_SIZE)
        imageClusterManager.renderer = imageClusterRenderer
        photomap.setOnCameraIdleListener(imageClusterManager)

        photomap.setOnMarkerClickListener(this)

        addImagePreviews()
    }

    override fun onClusterItemClick(imageClusterItem: ImageClusterItem?): Boolean {
        // todo may not need this anymore as onmarker does it
        return true
    }

}