package com.alecforbes.photomapapp.Model

import android.content.ContentResolver
import android.graphics.BitmapFactory
import android.media.ExifInterface
import android.net.Uri
import java.io.File

/**
 * Created by Alec on 5/5/2018.
 */

class  ImageDataCreator( private val content: ContentResolver,
                         private val files: ArrayList<File>,
                         private val includedImages: ArrayList<ImageData>){


    /**
     * Place map Image data objects need to be created differently from a CustomPhotomap, as they
     * are not selected via intents
     */

    fun createIncludedImageData(): ArrayList<ImageData> {

        files.forEach {

            val stream = content.openInputStream(Uri.fromFile(it))
            val exif = ExifInterface(stream)
            val file = File(it.path)

            val bitmap = BitmapFactory.decodeStream(content.openInputStream(Uri.fromFile(it)))

            val selectedImage = ImageData(file, bitmap, exif)
            includedImages.add(selectedImage)
        }
        return includedImages
    }
}