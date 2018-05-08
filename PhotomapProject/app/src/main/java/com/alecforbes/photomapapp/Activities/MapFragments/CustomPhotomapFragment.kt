package com.alecforbes.photomapapp.Activities.MapFragments

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alecforbes.photomapapp.Model.ImageData
import com.alecforbes.photomapapp.R
import com.dekoservidoni.omfm.OneMoreFabMenu
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_photo_selection.*
import kotlinx.android.synthetic.main.activity_photomap.*


/**
 * Created by Alec on 4/26/2018.
 * The base class from which individual map fragments inherit from.
 *
 * The 'open' modifier defines the class as not being final and can be inherited from (by default
 * classes are final in Kotlin)
 */

open class CustomPhotomapFragment: SupportMapFragment(), OnMapReadyCallback {

    private lateinit var photomap: GoogleMap
    private var selectedImages = ArrayList<ImageData>()

    // TODO this could be custom views later
    private var markers = ArrayList<Marker>()


    override fun onActivityCreated(p0: Bundle?) {
        super.onActivityCreated(p0)
        getMapAsync(this)

    }


    override fun onMapReady(p0: GoogleMap?) {
        photomap = p0 as GoogleMap

        //addImagePreviews()

        // fixme bug redrawing when you go back to the select screen and make a new one
        //setMapBounds()


        //TODO Populate photomap with custom views based on image data passed in

    }


    /**
     * Define the necessary objects for the map fragment when the instance is created. This does not
     * call the lifecycle functions like onMapReady, the fragment must be linked to a view for that.
     */
    companion object {
        fun newInstance(): CustomPhotomapFragment {
            val f = CustomPhotomapFragment()
            val args = Bundle()
            //args.putParcelableArrayList("selectedImages", images)
            //f.arguments = args
            return f
        }
    }

    fun setSelectedData(selectedData: ArrayList<ImageData>){
        selectedImages = selectedData
    }

    /**
     * Add the images to the photomap fragment as previews from the retrieved data
     */
    fun addImagePreviews(){


        val testLine = PolylineOptions() // fixme testing

        //selectedImages = arguments.getParcelableArrayList<ImageData>("selectedImages")

        val markerOpts = MarkerOptions()

        selectedImages.forEach {

            markerOpts.position(it.latLong)
            val thumbnail = BitmapFactory.decodeByteArray(it.getImageThumbnail(), 0, it.getImageThumbnail().size)
            val thumbnailDesc = BitmapDescriptorFactory.fromBitmap(thumbnail)
            markerOpts.icon(thumbnailDesc)

            val marker = photomap.addMarker(markerOpts)
            markers.add(marker)

            testLine.add(it.latLong)
        }

        photomap.addPolyline(testLine)

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



}