package com.alecforbes.myapplication

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_custom_photomap.*

class CustomPhotomap : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val imagesIntent = intent
        setContentView(R.layout.activity_custom_photomap)


    }

}
