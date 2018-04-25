package com.alecforbes.photomaps

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class CustomPhotomap : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val imagesIntent = intent
        setContentView(R.layout.activity_custom_photomap)


    }

}
