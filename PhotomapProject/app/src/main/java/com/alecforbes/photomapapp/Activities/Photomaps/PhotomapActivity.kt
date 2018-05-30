package com.alecforbes.photomapapp.Activities.Photomaps

import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics

/**
 * Created by Alec on 5/30/2018.
 */

open class PhotomapActivity: AppCompatActivity(){

    var screenSize: Int? = null

    /**
     * Helper function to get the resolution of a screen. If greater than 1080p, the image
     * thumbnails and timeline photos should be larger.
     */
    fun setScreenResolution(){
        val displayMetrics = DisplayMetrics()

        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenHeight = displayMetrics.heightPixels
        val screenWidth = displayMetrics.widthPixels

        screenSize = screenHeight * screenWidth
    }

}