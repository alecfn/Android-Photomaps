package com.alecforbes.photomaps.Model

import android.graphics.Bitmap
import android.media.ExifInterface

/**
 * Created by Alec on 4/26/2018.
 */

class ImageData constructor(imagePath: String) {
    // TODO time taken probably isnt a string

    // TODO any more exif

    var latitude = ""
    var longitude = ""
    var timeTaken = ""
    var thumbnailBitmap = ""
    var imageBitmap = ""

    val exifInterface = ExifInterface(imagePath)

    fun getAllImageData(){
        getLatLong()
        getImageBitmap()

    }

    fun getLatLong(){
        latitude = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE)
        longitude = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE)

    }

    fun getImageBitmap(){

    }

    fun getImageThumbnail(){

    }

    fun getTimeTaken(){

    }


}
