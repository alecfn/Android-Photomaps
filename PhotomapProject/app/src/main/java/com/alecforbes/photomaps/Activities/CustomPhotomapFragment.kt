package com.alecforbes.photomaps.Activities

import android.graphics.Bitmap
import android.os.Bundle
import com.alecforbes.photomaps.Model.ImageData
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import java.io.ByteArrayOutputStream

/**
 * Created by Alec on 4/26/2018.
 */

class CustomPhotomapFragment: SupportMapFragment(), OnMapReadyCallback {

    private lateinit var customPhotomap: GoogleMap
    private var selectedImages = ArrayList<ImageData>()


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

        //TODO Populate customPhotomap with custom views based on image data passed in
    }

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

            customPhotomap.addMarker(markerOpts)
        }

    }

}