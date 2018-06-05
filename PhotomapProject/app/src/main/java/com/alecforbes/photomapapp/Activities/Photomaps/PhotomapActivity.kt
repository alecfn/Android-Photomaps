package com.alecforbes.photomapapp.Activities.Photomaps

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import java.net.URLEncoder

/**
 * Created by Alec on 5/30/2018.
 * Superclass (denoted by the 'open' modifier in Kotlin) for common methods in both custom and
 * place photomap activities. These classes inherit from this class. Mainly defines helper functions
 * for getting things like the size of the screen.
 */

open class PhotomapActivity: AppCompatActivity(){

    var screenSize: Int? = null
    var THUMBNAIL_SIZE = 300
    private val PIXELS_1080 = 2073600

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
        imageButton.setPadding(0,0,0,0) // Pad to edges of image button
        imageButton.scaleType = ImageView.ScaleType.FIT_XY

        return imageButton

    }

    /**
     * Send the address of the image to the Google Maps application.
     *
     * With reference to:
     * https://stackoverflow.com/questions/9987551/how-to-open-google-maps-using-address
     */
    fun startMapsFromAddress(realAddress: String){
        val mapsIntent = Intent(Intent.ACTION_VIEW, Uri.parse(String.format("geo:0,0?q=%s",URLEncoder.encode(realAddress)))) // fixme
        mapsIntent.setClassName("com.google.android.apps.maps",
                "com.google.android.maps.MapsActivity")

        startActivity(mapsIntent)
    }

}