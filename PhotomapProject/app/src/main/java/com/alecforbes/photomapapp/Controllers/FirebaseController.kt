package com.alecforbes.photomapapp.Controllers

import android.content.ContentResolver
import android.graphics.BitmapFactory
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.support.annotation.RequiresApi
import com.alecforbes.photomapapp.Activities.PlacePhotomap
import com.alecforbes.photomapapp.Model.ImageData
import com.alecforbes.photomapapp.Model.PlacesLinksHashmap
import com.google.firebase.storage.FirebaseStorage
import java.io.File

/**
 * Created by Alec on 4/28/2018.
 */

class FirebaseController(private val content: ContentResolver,
                         private val associatedPlaceMap: PlacePhotomap){

    val firebaseStorage: FirebaseStorage = FirebaseStorage.getInstance()

    var firebaseFiles = ArrayList<File>()
    var includedImages = ArrayList<ImageData>()

    val placesLinksHashmap = PlacesLinksHashmap()
    val imageDataCreator = ImageDataCreator(content, firebaseFiles, includedImages)

    /**
     * Get the images from firebase for the selected location and return the array of URIs
     */
    @RequiresApi(Build.VERSION_CODES.N)
    fun retrieveSelectedPlaceImages(placeName: String) {

        // Handle cases to download the correct data

        // Because we need to access exif information, we have to download the image
        val storageRef = firebaseStorage.reference

        val selectedMapLinks = placesLinksHashmap.getPlaceLinks(placeName)

        selectedMapLinks!!.forEach {
            val storagePathRef = storageRef.child(it)
            val tempFile = File.createTempFile("images", "jpg")

            storagePathRef.getFile(tempFile).addOnSuccessListener {
                firebaseFiles.add(tempFile)

                print("")

            }.addOnCompleteListener {
                includedImages = imageDataCreator.createIncludedImageData()

                //returnImages()
                associatedPlaceMap.onFirebaseComplete(includedImages)
            }

        }

    }

}