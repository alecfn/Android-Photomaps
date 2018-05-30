package com.alecforbes.photomapapp.Activities.Photomaps

import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout

/**
 * Created by Alec on 5/30/2018.
 * Superclass (denoted by the 'open' modifier in Kotlin) for common methods in both custom and
 * place photomap activities. These classes inherit from this class.
 */

open class PhotomapActivity: AppCompatActivity(){

    var screenSize: Int? = null
    var THUMBNAIL_SIZE = 300
    val PIXELS_1080 = 2073600

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

        // If the screen is greater than 1080p, increase the default thumbnail size to 500

        if (screenSize!! > PIXELS_1080){  // Greater than 1080p
            THUMBNAIL_SIZE = 500 // 500, usually screens larger than 1080 are UHD
        }

    }

    /**
     * Defines the necessary layout parameters to put images in a horizontal scrollview.
     *
     * This is displayed at the top of the screen.
     */
    fun defineHorizontalScrollViewButton(): ImageButton {

        val layout = LinearLayout(applicationContext)

        val layoutParams = ViewGroup.LayoutParams(THUMBNAIL_SIZE, THUMBNAIL_SIZE)
        layout.layoutParams = layoutParams
        layout.gravity = Gravity.CENTER_HORIZONTAL

        // Each image should be a button to tap to bring up a larger preview
        val imageButton = ImageButton(applicationContext)
        imageButton.layoutParams = layoutParams
        imageButton.adjustViewBounds = true
        imageButton.scaleType = ImageView.ScaleType.FIT_XY

        return imageButton

    }

}