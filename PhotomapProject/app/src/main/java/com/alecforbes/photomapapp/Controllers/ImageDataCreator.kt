package com.alecforbes.photomapapp.Controllers

import android.content.ContentResolver
import android.graphics.BitmapFactory
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.support.annotation.RequiresApi
import com.alecforbes.photomapapp.Model.ImageData
import java.io.File
import java.nio.file.Files

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
    @RequiresApi(Build.VERSION_CODES.N) // Todo api levels

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