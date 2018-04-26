package com.alecforbes.photomaps.Activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.alecforbes.photomaps.R

class CustomPhotomap : AppCompatActivity() {

    var imageFilePath = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val imagesIntent = intent
        getImageFilepaths(imagesIntent)

        setContentView(R.layout.acivity_photomap)

        var customMapFragment : CustomPhotomapFragment? = supportFragmentManager.findFragmentById(R.id.photomapFragment) as CustomPhotomapFragment?

        customMapFragment?.getMapAsync(customMapFragment)

    }

    fun getImageFilepaths(imagesIntent: Intent){

        if (imagesIntent != null) {

            var imageData = imagesIntent.getParcelableExtra<Intent>("imageData")
            //imageData.

            var numberImages = imageData.clipData!!.itemCount
            var imageUris = ArrayList<String>()

            // Get all of the image ClipData objects to add to an array and send in an intent
            for (i in 0..(numberImages - 1)){
                var uri = imageData.clipData.getItemAt(i).uri.toString()
                imageUris.add(uri)
            }



            //val selectedImageUris = imagesIntent.getSerializableExtra("imageData")
            print("bleh")
        }
    }


}
