package com.alecforbes.photomapapp.Model

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.media.ExifInterface
import android.media.ExifInterface.*
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
data class ImageData(val file: File,
                     private var bitmap: Bitmap,
                     private val exifInterface: @RawValue ExifInterface,
                     private var latitude: Float=0F,
                     private var longitude: Float=0F,
                     var dateTimeTaken: String="",
                     var timeTaken: String="",
                     var latLong: LatLng= LatLng(0.0, 0.0),
                     var unixTime: Long = 0L,
                     private var thumbnailData: ByteArray= byteArrayOf()): Parcelable, Comparable<ImageData> {

    // TODO time taken probably isnt a string

    // TODO any more exifInterface

    init {
        setAllImageData()
    }

    private fun setAllImageData(){
        setLatLong()
        dateTimeTaken = setDateTimeTaken()
        setImageThumbnail()

    }

    private fun setLatLong(){

        val latLongArr = FloatArray(2)
        exifInterface.getLatLong(latLongArr)

        latitude = latLongArr[0]
        longitude = latLongArr[1]

        latLong = LatLng(latitude.toDouble(), longitude.toDouble())

    }

    private fun setDateTimeTaken(): String {

        // Number of potential date stamps stored in an image, so try to get the best first
        val tagsList = listOf(TAG_GPS_DATESTAMP, TAG_DATETIME, TAG_DATETIME_DIGITIZED)

        //try {

            tagsList.forEach {
                val dateStamp = exifInterface.getAttribute(it)

                if (dateStamp != null){
                    return dateStamp
                }

            }
        return "0 0" // If we never got a datetime, just return 0

        //}catch (readEx: Exception){
            // TODO
        //}
    }

    fun getImageBitmap(): Bitmap {
        return bitmap
    }

    private fun setImageThumbnail(){

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

    /**
     * As images need to be sorted for the timelines, the comparable interface needs to be
     * implemented to compare based on the value of the unix time stamp.
     */
    override fun compareTo(other: ImageData): Int {

        val compareTime = other.unixTime.toInt()
        return this.unixTime.toInt() - compareTime

    }



}
