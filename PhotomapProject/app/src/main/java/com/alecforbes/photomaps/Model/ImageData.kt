package com.alecforbes.photomaps.Model

import android.media.ExifInterface

/**
 * Created by Alec on 4/26/2018.
 */

class ImageData constructor(exifInterface: ExifInterface) {
    // TODO time taken probably isnt a string

    // TODO any more exifInterface

    //var inputStream
    var latitude = ""
    var longitude = ""
    var timeTaken = ""
    var thumbnailBitmap = ""
    var imageBitmap = ""
    var exif = exifInterface

    init {
        getAllImageData()
    }

    //val exifInterface = ExifInterface(imagePath)

    fun getAllImageData(){
        getLatLong()
        getImageBitmap()

    }

    fun getLatLong(){
        latitude = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE)
        longitude = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE)

    }

    fun getImageBitmap(){

    }

    fun getImageThumbnail(){

    }

    fun getTimeTaken(){

    }


}
