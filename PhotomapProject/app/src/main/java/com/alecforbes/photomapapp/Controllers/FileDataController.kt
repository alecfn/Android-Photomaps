package com.alecforbes.photomapapp.Controllers

import android.content.ContentResolver
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.support.annotation.RequiresApi
import com.alecforbes.photomapapp.Model.ImageData
import java.io.File

/**
 * Created by Alec on 5/8/2018.
 */

class FileDataController (private val contentResolver: ContentResolver){

    val imageUris = ArrayList<Uri>()
    var selectedImages = ArrayList<ImageData>()

    @RequiresApi(Build.VERSION_CODES.N) // FIXME exif stream needs android N
    /**
     * For each image URI, build an input stream object, then an exif interface. This allows access
     * to exif data. The data can then stored as an ImageData object.
     */
    private fun createImageData(imageUris: ArrayList<Uri>){


        imageUris.forEach {

            // Not every object can be created in the object because we have to resolve the current content, so pass those in
            val stream = contentResolver.openInputStream(it)
            val exif = ExifInterface(stream)
            val file = File(it.path)
            // TODO bitmap may need to be byte array not a Bitmap due to parcelable limits

            val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(it))
            // You can't reuse an InputStream in Android, so it has to be declared again

            // TODO any more creation stuff should be done here

            val selectedImage = ImageData(file, bitmap, exif)
            selectedImages.add(selectedImage)
        }

    }

    private fun getSelectedImageUri(){

        //val uri =

    }


    @RequiresApi(Build.VERSION_CODES.N)
    /**
     *
     */
    fun getSelectedImageUris(fileData: Intent){

        //val imageData = imagesIntent.getParcelableExtra<Intent>("imageData")
        //val imageUris= ArrayList<Uri>()

        if (fileData.clipData != null) {

            // Get the number of images selected from the gallery application
            val numberImages = fileData.clipData!!.itemCount

            // Get all of the image ClipData objects to add to an array to send in an intent
            for (i in 0..(numberImages - 1)){
                val uri = fileData.clipData.getItemAt(i).uri

                imageUris.add(uri)
            }

        } else {

            // If the user only selects one image, clipData is unused and will be null
            val uri = fileData.data
            imageUris.add(uri)

        }

        createImageData(imageUris)

    }

    fun updateImageList(){

    }

    fun getImageList(){

    }

}