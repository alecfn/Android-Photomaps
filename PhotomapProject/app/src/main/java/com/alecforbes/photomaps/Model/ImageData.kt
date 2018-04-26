package com.alecforbes.photomaps.Model

import android.graphics.Bitmap
import android.media.ExifInterface
import android.net.Uri
import java.io.InputStream

/**
 * Created by Alec on 4/26/2018.
 */

class ImageData constructor(exif: ExifInterface) {
    // TODO time taken probably isnt a string

    // TODO any more exif

    var latitude = ""
    var longitude = ""
    var timeTaken = ""
    var thumbnailBitmap = ""
    var imageBitmap = ""
    var exif = exif

    //val exifInterface = ExifInterface(imagePath)

    fun getAllImageData(){
        getLatLong()
        getImageBitmap()

    }

    fun createExifInterface(){

        try{

        }finally {

        }
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
