package com.alecforbes.photomapapp.Model

import android.content.ContentResolver
import android.graphics.BitmapFactory
import android.media.ExifInterface
import android.net.Uri
import java.io.File

/**
 * Created by Alec on 5/5/2018.
 * This class creates the ImageData objects which are used by the application. The necessary
 * parameters are created in this class based on the supplied files, as we do not want all of the
 * necessary data structure parcelable as they are in the ImageData class.
 */

class  ImageDataCreator( private val content: ContentResolver,
                         private val files: ArrayList<File>,
                         private val includedImages: ArrayList<ImageData>){

    /**
     * Place map Image data objects need to be created differently from a CustomPhotomap, as they
     * are not selected via intents
     */

    fun createIncludedImageData(): ArrayList<ImageData> {

        files.forEach { file ->

            val stream = content.openInputStream(Uri.fromFile(file))
            val exif = ExifInterface(stream)
            val newFile = File(file.path)

            val fileExists = checkFileExists(newFile.absolutePath)

            if (!fileExists) {
                val bitmap = BitmapFactory.decodeStream(content.openInputStream(Uri.fromFile(newFile)))

                val selectedImage = ImageData(newFile, bitmap, exif)

                includedImages.add(selectedImage)
            }
        }
        return includedImages
    }

    /**
     * Due to firebase being asynchronous, multiple copies may be created. Check that the file is
     * not already in the list.
     */
    private fun checkFileExists(filePath: String): Boolean {

            includedImages.forEach { imageData ->
                if(filePath == imageData.file.absolutePath){
                    return true
                }
            }
        return false
    }

}