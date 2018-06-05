package com.alecforbes.photomapapp.Controllers

import android.content.ContentResolver
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.ExifInterface
import android.net.Uri
import com.alecforbes.photomapapp.Model.ImageData
import java.io.File

/**
 * Created by Alec on 5/8/2018.
 * This class handles all retrieval of data from the device. The retrieved data is then stored
 * as ImageData objects which stop all the relevant data that is useful to the application.
 */

class FileDataController (private val contentResolver: ContentResolver,
                          private val screenSize: Int){

    val imageUris = ArrayList<Uri>() // All selected image URI values
    private val newImageUris = ArrayList<Uri>() // Only newly selected URI values
    var selectedData = ArrayList<ImageData>()


    /**
     * For each image URI, build an input stream object, then an exif interface. This allows access
     * to exif data. The data can then stored as an ImageData object.
     */
    private fun createImageData(){


        newImageUris.forEach { uri ->

            val stream = contentResolver.openInputStream(uri)
            val exif = ExifInterface(stream)
            val file = File(uri.path)

            val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(uri))
            // You can't reuse an InputStream in Android, so it has to be declared again

            // TODO any more creation stuff should be done here

            val selectedImage = ImageData(file, bitmap, exif, screenSize = screenSize)
            imageUris.add(uri)
            selectedData.add(selectedImage)
        }

        // Clearing the array means image data isn't created again in the above loop
        newImageUris.clear()

    }


    /**
     * Get the URI of images selected in the gallery application.
     *
     * As the data being looped through comes from the selection intent, only those images are
     * looped through to get URI data, not images that are already selected.
     */
    fun getSelectedImageUrisFromIntent(fileData: Intent){


        if (fileData.clipData != null) {

            // Get the number of images selected from the gallery application
            val numberImages = fileData.clipData!!.itemCount

            // Get all of the image ClipData objects to add to an array to send in an intent
            for (i in 0..(numberImages - 1)) {
                val uri = fileData.clipData.getItemAt(i).uri

                if(!checkFileExistsInMap(uri)) {
                    newImageUris.add(uri)
                }
            }

        } else {

            // If the user only selects one image, clipData is unused and will be null
            val uri = fileData.data
            if(!checkFileExistsInMap(uri)) {
                newImageUris.add(uri)
            }

        }

        createImageData()

    }

    /**
     * When creating a map again from saved data, the URI data comes from an ArrayList
     */
    fun getSelectedImageUrisFromArray(fileData: ArrayList<String>){

        fileData.forEach {

            newImageUris.add(Uri.parse(it))
        }

        createImageData()

    }

    /**
     * When getting data from an intent, make sure that the new data does not already exist using
     * the URI
     */
    private fun checkFileExistsInMap(newuri: Uri): Boolean {

        // For saved maps, file uris will be different because the files are saved in data
        // This is more of a work around due to the problems with saving, it's not ideal

        // Use a regex to see if new uris have the same image names to get around saved map problem
        val imageNameRegex = "(image%[0-9]*[A-Z]*[0-9]*)".toRegex()
        val imageName = imageNameRegex.find(newuri.toString())?.groups?.get(0)?.value

        // Saved images will be appended by "_copy"
        val copyRegex = "($imageName.*_copy)".toRegex()

        if (imageName != null){
            imageUris.forEach { storedUri ->
                if(copyRegex.containsMatchIn(storedUri.toString())){
                    return true
                }
            }
        }

        // Non saved maps can just be checked with a contains
        if(imageUris.contains(newuri) ){
            return true
        }

        return false
    }

}