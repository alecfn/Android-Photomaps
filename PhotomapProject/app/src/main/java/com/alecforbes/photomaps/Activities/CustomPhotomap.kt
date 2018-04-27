package com.alecforbes.photomaps.Activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment.getExternalStorageDirectory
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import com.alecforbes.photomaps.Model.ImageData
import com.alecforbes.photomaps.R
import java.io.File

class CustomPhotomap : AppCompatActivity() {

    var selectedImages = ArrayList<ImageData>()
    // Store the images as objects with all relevant info

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val imagesIntent = intent
        getSelectedImageUris(imagesIntent)

        setContentView(R.layout.acivity_photomap)

        val customMapFragment : CustomPhotomapFragment? = supportFragmentManager.findFragmentById(R.id.photomapFragment) as CustomPhotomapFragment?

        customMapFragment?.getMapAsync(customMapFragment)

    }

    /**
     * Add the images to the map fragment as previews from the retrieved data
     */
    fun addImagePreviews(mapFrag: CustomPhotomapFragment){



    }


    @RequiresApi(Build.VERSION_CODES.N) // FIXME exif stream needs android N
    /**
     * For each image URI, build an input stream object, then an exif interface. This allows access
     * to exif data. The data can then stored as an ImageData object.
     */
    private fun createImageData(imageUris: ArrayList<Uri>){


        imageUris.forEach {

            // For each selected image file URI, create an input stream object, and exif interface

            // Not every object can be created in the object because we have to resolve the current content, so pass those in
            val stream = contentResolver.openInputStream(it)
            val exif = ExifInterface(stream)
            val file = File(it.path)
            val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(it))
            // You can't reuse an InputStream in Android, so it has to be declared again

            //val options = BitmapFactory.Options()
            //options.inPreferredConfig = Bitmap.Config.ARGB_8888
            //val path = it.path
            //val bitmap = BitmapFactory.decodeStream(path, options)

            val selectedImage = ImageData(file, bitmap, exif)
            selectedImages.add(selectedImage)
        }
        print("")

    }



    @RequiresApi(Build.VERSION_CODES.N)
    /**
     *
     */
    private fun getSelectedImageUris(imagesIntent: Intent){

        if (imagesIntent != null) {

            val imageData = imagesIntent.getParcelableExtra<Intent>("imageData")

            // FIXME exception when only selecting one?
            // Get the number of images selected from the gallery application
            val numberImages = imageData.clipData!!.itemCount
            val imageUris= ArrayList<Uri>()

            // Get all of the image ClipData objects to add to an array and send in an intent
            for (i in 0..(numberImages - 1)){
                val uri = imageData.clipData.getItemAt(i).uri

                imageUris.add(uri)
            }

            createImageData(imageUris)
        }

    }


}
