package com.alecforbes.photomaps.Activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.alecforbes.photomaps.Controllers.ImageController
import com.alecforbes.photomaps.Model.ImageData
import com.alecforbes.photomaps.R

class CustomPhotomap : AppCompatActivity() {

    var selectedImages = ArrayList<ImageData>()
   // var imageController = ImageController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val imagesIntent = intent
        getImageFilepaths(imagesIntent)

        setContentView(R.layout.acivity_photomap)

        var customMapFragment : CustomPhotomapFragment? = supportFragmentManager.findFragmentById(R.id.photomapFragment) as CustomPhotomapFragment?

        customMapFragment?.getMapAsync(customMapFragment)

    }

    private fun createImageData(imageUris: ArrayList<String>){

        imageUris.forEach {
           // val selectedImage = imageController
            var uri = it
            var selectedImage = ImageData(uri)
            selectedImages.add(selectedImage)
        }
        print("")

    }


    private fun getImageFilepaths(imagesIntent: Intent){

        if (imagesIntent != null) {

            val imageData = imagesIntent.getParcelableExtra<Intent>("imageData")

            val numberImages = imageData.clipData!!.itemCount
            val imageUris = ArrayList<String>()

            // Get all of the image ClipData objects to add to an array and send in an intent
            for (i in 0..(numberImages - 1)){
                val uri = imageData.clipData.getItemAt(i).uri.toString()
                imageUris.add(uri)
            }

            createImageData(imageUris)
        }

    }


}
