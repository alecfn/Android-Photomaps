package com.alecforbes.photomaps.Model

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ExifInterface
import java.io.File
import java.io.InputStream

/**
 * Created by Alec on 4/26/2018.
 */

class ImageData constructor(file: File, bitmap: Bitmap, exifInterface: ExifInterface) {
    // TODO time taken probably isnt a string

    // TODO any more exifInterface

    //var inputStream = stream
    var file = file
    var exif = exifInterface
    var bitmap: Bitmap? = null
    var latitude = ""
    var longitude = ""
    var timeTaken = ""
    var thumbnailBitmap = ""

    init {
        setAllImageData()
    }


    fun setAllImageData(){
        setLatLong()
    }

    fun setLatLong(){
        latitude = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE)
        longitude = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE)

    }

    fun getImageBitmap(): Bitmap? {
        return bitmap
    }

    fun getImageThumbnail(){

    }

    fun getTimeTaken(){

    }


}
