package com.alecforbes.photomapapp.Activities

import android.os.Bundle
import com.alecforbes.photomapapp.Model.ImageData
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

/**
 * Created by Alec on 4/26/2018.
 */

class CustomPhotomapFragment: SupportMapFragment(), OnMapReadyCallback {

    private lateinit var customPhotomap: GoogleMap
    private var selectedImages = ArrayList<ImageData>()

    // TODO this should be custom views later
    private var markers = ArrayList<Marker>()


    override fun onCreate(p0: Bundle?) {
        super.onCreate(p0)

    }

    override fun onActivityCreated(p0: Bundle?) {
        super.onActivityCreated(p0)
        getMapAsync(this)
    }

    override fun onMapReady(p0: GoogleMap?) {
        customPhotomap = p0 as GoogleMap

        addImagePreviews()
        setMapBounds()

        //TODO Populate customPhotomap with custom views based on image data passed in
    }


    /**
     * Define the necessary objects for the map fragment when the instance is created. This does not
     * call the lifecycle functions like onMapReady, the fragment must be linked to a view for that.
     */
    companion object {
        fun newInstance(images: ArrayList<ImageData>): CustomPhotomapFragment {
            val f = CustomPhotomapFragment()
            val args = Bundle()
            args.putParcelableArrayList("selectedImages", images)
            f.arguments = args
            return f
        }
    }

    /**
     * Add the images to the customPhotomap fragment as previews from the retrieved data
     */
    fun addImagePreviews(){

        selectedImages = arguments.getParcelableArrayList<ImageData>("selectedImages")

        val markerOpts = MarkerOptions()

        selectedImages.forEach {

            markerOpts.position(it.latLong)

            val marker = customPhotomap.addMarker(markerOpts)
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
        val pad = 10 // Map pixel padding
        val cameraUpdate = CameraUpdateFactory.newLatLngBounds(mapBounds, pad)

        customPhotomap.moveCamera(cameraUpdate)
    }

}