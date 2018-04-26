package com.alecforbes.photomaps.Activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
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

            //val selectedImageUris = imagesIntent.getSerializableExtra("imageData")
            //print("bleh")
        }
    }


}
