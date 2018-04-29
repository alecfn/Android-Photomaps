package com.alecforbes.photomapapp.Model

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.media.ExifInterface
import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import java.io.ByteArrayOutputStream
import java.io.File

/**
 * Created by Alec on 4/26/2018.
 */

@SuppressLint("ParcelCreator") // Known unnecessary warning with Kotlin when using Parcelize
@Parcelize
data class ImageData(private val file: File,
                     private var bitmap: Bitmap,
                     private val exifInterface: @RawValue ExifInterface,
                     private var latitude: Float=0F,
                     private var longitude: Float=0F,
                     var latLong: LatLng= LatLng(0.0, 0.0),
                     private var thumbnailData: ByteArray= byteArrayOf()): Parcelable {
    // TODO time taken probably isnt a string

    // TODO any more exifInterface

    private var timeTaken = ""

    init {
        setAllImageData()
    }


    fun setAllImageData(){
        setLatLong()
        setImageThumbnail()
    }

    fun setLatLong(){

        val latLongArr = FloatArray(2)
        exifInterface.getLatLong(latLongArr)

        latitude = latLongArr[0]
        longitude = latLongArr[1]

        latLong = LatLng(latitude.toDouble(), longitude.toDouble())

    }

    fun convertCoordToDegree(){

    }

    fun setTimeTaken(){

    }

    fun getImageBitmap(): Bitmap {
        return bitmap
    }

    fun setImageThumbnail(){

        try {

            val THUMBNAIL_SIZE = 180 // Controls the size of displayed thumbnails on map fragments
            val thumbnail = Bitmap.createScaledBitmap(bitmap, THUMBNAIL_SIZE, THUMBNAIL_SIZE, false)

            val outputStream = ByteArrayOutputStream()
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            thumbnailData = outputStream.toByteArray()
        } catch (e: Exception){

        }
    }

    fun getImageThumbnail(): ByteArray {
        return thumbnailData
    }

    fun getTimeTaken(){

    }


}
