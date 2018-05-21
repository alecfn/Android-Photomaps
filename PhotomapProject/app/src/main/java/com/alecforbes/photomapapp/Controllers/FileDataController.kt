package com.alecforbes.photomapapp.Controllers

import android.content.ContentResolver
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.ExifInterface
import android.net.Uri
import android.support.v4.app.ActivityCompat.startActivityForResult
import com.alecforbes.photomapapp.Model.ImageData
import java.io.File

/**
 * Created by Alec on 5/8/2018.
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

            // fixme some test stuff https://developer.android.com/guide/topics/providers/document-provider#client
            // fixme https://androidforums.com/threads/permission-denied-android-permission-manage_documents.920600/
            //val i = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            //startActivityForResult(i, CHOOSE_IMAGE);
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.setType("image/*")

            val stream = contentResolver.openInputStream(uri)
            val exif = ExifInterface(stream)
            val file = File(uri.path)
            // TODO bitmap may need to be byte array not a Bitmap due to parcelable limits

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
            for (i in 0..(numberImages - 1)){
                val uri = fileData.clipData.getItemAt(i).uri

                newImageUris.add(uri)
            }

        } else {

            // If the user only selects one image, clipData is unused and will be null
            val uri = fileData.data
            newImageUris.add(uri)

        }

        createImageData()

    }

    /**
     * When creating a map again from saved data, the URI data comes from an ArrayList
     */
    fun getSelectedImageUrisFromArray(fileData: ArrayList<Uri>){

        fileData.forEach {
            //val realUri = contentResolver.openInputStream(it)
            //val test2 = DocumentFile.fromTreeUri(contentResolve, Uri.fromFile(File(it.path.toString())))
            val uriFromPath = Uri.parse(it.toString()) // FIXME uri access problem
            newImageUris.add(uriFromPath)
            val test = File(uriFromPath.path)
            print("")
        }

        createImageData()

    }

}