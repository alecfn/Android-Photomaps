package com.alecforbes.photomapapp.Activities

import android.content.Intent
import android.graphics.BitmapFactory
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import com.alecforbes.photomapapp.Model.ImageData
import com.alecforbes.photomapapp.R
import java.io.File

// Open keyword means this class can be inherited from
open class CustomPhotomap : AppCompatActivity() {

    var selectedImages = ArrayList<ImageData>()
    // Store the images as objects with all relevant info

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Custom Photomap"

        val imagesIntent = intent
        getSelectedImageUris(imagesIntent)

        setContentView(R.layout.acivity_photomap)

        // Create bundle to send images to fragment
        val images = Bundle()
        images.putParcelableArrayList("selectedImages", selectedImages)
        val customMapFragment = CustomPhotomapFragment.newInstance(selectedImages)

        // As the map is a fragment, initialise it in a view (but just the constraint as the map fills the view)
        supportFragmentManager.beginTransaction()
                .add(R.id.photomapConstraint, customMapFragment)
                .commit()

    }


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
        print("")

    }



    @RequiresApi(Build.VERSION_CODES.N)
    /**
     *
     */
    private fun getSelectedImageUris(imagesIntent: Intent){

        val imageData = imagesIntent.getParcelableExtra<Intent>("imageData")
        val imageUris= ArrayList<Uri>()

        if (imagesIntent != null && imageData.clipData != null) {

            // Get the number of images selected from the gallery application
            val numberImages = imageData.clipData!!.itemCount

            // Get all of the image ClipData objects to add to an array to send in an intent
            for (i in 0..(numberImages - 1)){
                val uri = imageData.clipData.getItemAt(i).uri

                imageUris.add(uri)
            }

        } else {

            // If the user only selects one image, clipData is unused and will be null
            val uri = imageData.data
            imageUris.add(uri)

        }

        createImageData(imageUris)

    }


}
