package com.alecforbes.photomaps.Model

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ExifInterface
import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import java.io.File
import java.io.InputStream
import kotlin.properties.Delegates

/**
 * Created by Alec on 4/26/2018.
 */

@SuppressLint("ParcelCreator") // Known unnecessary warning with Kotlin when using Parcelize
@Parcelize
data class ImageData(private val file: File,
                     private var bitmap: Bitmap ,
                     private val exifInterface: @RawValue ExifInterface,
                     private var latitude: Float=0F,
                     private var longitude: Float=0F,
                     var latLong: LatLng= LatLng(0.0, 0.0)): Parcelable {
    // TODO time taken probably isnt a string

    // TODO any more exifInterface

    //var inputStream = stream
    //var file = file
    //var exif = exifInterface
   // var bitmap: Bitmap? = null
    //private var latitude: Float by Delegates.notNull()
    //private var longitude: Float by Delegates.notNull()
    //private var latLong: LatLng by Delegates.notNull()
    private var timeTaken = ""
    private var thumbnailBitmap = ""

    init {
        setAllImageData()
    }


    fun setAllImageData(){
        setLatLong()
    }

    fun setLatLong(){

        val latLongArr = FloatArray(2)
        exifInterface.getLatLong(latLongArr)

        latitude = latLongArr[0]
        longitude = latLongArr[1]

        latLong = LatLng(latitude.toDouble(), longitude.toDouble())


        //latitude = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE)
        //longitude = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE)
        //latLong = LatLng(latitude.toDouble(), longitude.toDouble())

    }

    fun convertCoordToDegree(){

    }

    fun setTimeTaken(){

    }

    fun getImageBitmap(): Bitmap {
        return bitmap
    }

    fun getImageThumbnail(){

    }

    fun getTimeTaken(){

    }


}
