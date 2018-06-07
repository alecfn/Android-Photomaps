package com.alecforbes.photomapapp.Model

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ExifInterface
import android.media.ExifInterface.*
import android.os.Parcelable
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*

/**
 * Created by Alec on 4/26/2018.
 * This ImageData class contains all of the information relevant to the generation of data in a map.
 * Most information is pulled from the ExifInterface, which retrieves data from the image tags.
 * Other information relevant to the generation of images is stored in instances of this class.
 *
 *
 * Exif tags to access reference from Google:
 * https://developer.android.com/reference/android/support/media/ExifInterface
 */

@SuppressLint("ParcelCreator") // Known unnecessary warning with Kotlin when using Parcelize
@Parcelize
data class ImageData(val file: File,
                     private var bitmap: Bitmap,
                     @IgnoredOnParcel private var exifInterface: @RawValue ExifInterface?,
                     var latitude: Float=0F,
                     var longitude: Float=0F,
                     var dateTimeTaken: String="",
                     var datetaken: String ="",
                     var timeTaken: String="",
                     var realTimeTaken: Date?= null, // Calendar time
                     var latLong: LatLng= LatLng(0.0, 0.0),
                     var unixTime: Long = 0L,
                     var realAddress: String? = null,
                     var imageOrientation: Int? = 0,
                     private var thumbnailData: ByteArray= byteArrayOf(),
                     private var screenSize: Int? = null): Parcelable, Comparable<ImageData> {

    @IgnoredOnParcel private var associatedLinks: List<String>? = null // Only used by place images

    // FIXME this class is a little all over the place with how things are done, clean up
    /**
     * Initialise all the relevant data in this class when the class is created.
     */
    init {
        // Set all the exif data we want to get from the exif interface
        setLatLong()
        dateTimeTaken = setDateTimeTaken()
        setImageOrientation()
        rotateBitmaps()
        setImageThumbnail()
        // Nullify the exif interface, it wont parcel and will crash, and we don't need it after
        exifInterface = null
    }

    /**
     * Set the latitude and longitude of the object by retrieving from the exif tag.
     */
    private fun setLatLong(){

        val latLongArr = FloatArray(2)
        exifInterface!!.getLatLong(latLongArr)

        latitude = latLongArr[0]
        longitude = latLongArr[1]

        latLong = LatLng(latitude.toDouble(), longitude.toDouble())

    }

    /**
     * Images contain multiple possible fields which may contain a datatime value. Try to get the
     * best one first, if it can't be retrieved, try the next and set the data. If none are set,
     * just set the value to 0 0.
     */
    private fun setDateTimeTaken(): String {

        // Number of potential date stamps stored in an image, so try to get the best first
        val tagsList = listOf(TAG_GPS_DATESTAMP, TAG_DATETIME, TAG_DATETIME_DIGITIZED)


        tagsList.forEach {
            val dateStamp = exifInterface!!.getAttribute(it)

            if (dateStamp != null){
                return dateStamp
            }

        }
        return "0 0" // If we never got a datetime, just return 0

    }

    /**
     * Images can be taken at different orientations, horizontal, vertical etc. We can use these
     * to determine how a bitmap should be oriented.
     */
    private fun setImageOrientation(){
        imageOrientation = exifInterface!!.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1)
    }

    /**
     * If images are rotated, the image thumbnails stored need to also be rotated so they display
     * correctly on the map later.
     *
     * Case statements based on code at:
     * https://stackoverflow.com/questions/20478765/how-to-get-the-correct-orientation-of-the-image-selected-from-the-default-image
     */
    private fun rotateBitmaps(){

        val rotationMatrix = Matrix()

        // Only when images are rotated (not ORIENTATION_NORMAL) should bitmaps be rotated
        when(imageOrientation){
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> {
                rotationMatrix.setScale(-1f, 1f)
            }

            ExifInterface.ORIENTATION_FLIP_VERTICAL -> {
                rotationMatrix.setRotate(180f)
                rotationMatrix.postScale(-1f, 1f)
            }
            ExifInterface.ORIENTATION_TRANSPOSE -> {
                rotationMatrix.setRotate(90f)
                rotationMatrix.setScale(-1f, 1f)
            }
            ExifInterface.ORIENTATION_TRANSVERSE -> {
                rotationMatrix.setRotate(-90f)
                rotationMatrix.postScale(-1f, 1f)
            }
            ExifInterface.ORIENTATION_ROTATE_90 -> {
                rotationMatrix.setRotate(90f)
            }
            ExifInterface.ORIENTATION_ROTATE_180 -> {
                rotationMatrix.setRotate(180f)
                rotationMatrix.postScale(-1f, 1f)
            }
            ExifInterface.ORIENTATION_ROTATE_270 -> {
                rotationMatrix.setRotate(-90f)
            }
        }
        // Set the bitmap to the newly rotated image
        try {
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, rotationMatrix, true)
        }catch (memoryEx: OutOfMemoryError){
            // Memory errors can occur here (but shouldn't), just in case, log memory errors
            Log.e("Exif Rotate Memory", "Ran out of memory while rotating bitmaps.")
        }

    }

    /**
     * Return the full bitmap of the image (not a scaled thumbnail)
     */
    fun getImageBitmap(): Bitmap {
        return bitmap
    }

    /**
     * Create a scaled image bitmap of a size which makes sense based on the screen resolution of
     * the device. Note: Not intended to scale for less than 1080p (as most screens in Android
     * are at least this size).
     */
    private fun setImageThumbnail(){

        try {

            var THUMBNAIL_SIZE = 250 // Controls the size of displayed thumbnails on map fragments

            if (screenSize!! > 2073600){  // Greater than 1080p
                THUMBNAIL_SIZE = 350
            }

            val thumbnail = Bitmap.createScaledBitmap(bitmap, THUMBNAIL_SIZE, THUMBNAIL_SIZE, false)

            val outputStream = ByteArrayOutputStream()
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            thumbnailData = outputStream.toByteArray()
        } catch (e: Exception){
            // If failed to create a scaled bitmap, handle so it does not crash
            Log.e("Failed to create Thumbnail", "Failed to create a thumbnail from the" +
                    " supplied image. Perhaps the image is too large?")

        }
    }

    /**
     * Some fields need getters due to JVM requirements, so getters for those are set here.
     */
    fun getImageThumbnail(): ByteArray {
        return thumbnailData
    }

    fun setAssociatedLinks(links: List<String>){
        associatedLinks = links
    }

    fun getAssociatedLinks(): List<String>? {
        return associatedLinks
    }

    /**
     * As images need to be sorted for the timelines, the comparable interface needs to be
     * implemented to compare based on the value of the unix time stamp.
     */
    override fun compareTo(other: ImageData): Int {

        val compareTime = other.unixTime.toInt()
        // return this.unixTime.toInt() - compareTime  // Oldest to newest taken photo

        return compareTime - this.unixTime.toInt() // Newest to oldest

    }

}
