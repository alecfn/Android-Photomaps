package com.alecforbes.photomapapp.Controllers

import android.content.ContentResolver
import android.util.Log
import com.alecforbes.photomapapp.Activities.Photomaps.PlacePhotomap
import com.alecforbes.photomapapp.Model.ImageData
import com.alecforbes.photomapapp.Model.ImageDataCreator
import com.alecforbes.photomapapp.Model.PlacesLinksHashmap
import com.google.firebase.storage.FirebaseStorage
import java.io.File

/**
 * Created by Alec on 4/28/2018.
 *
 * This class communicates with the firebase db and downloads images for an associated PlaceMap
 * object. Links for each are stored in the PlacesLinksHashmap class, and the firebase instance
 * is stored in this class.
 */
class FirebaseController(content: ContentResolver,
                         private val associatedPlaceMap: PlacePhotomap){

    private val firebaseStorage: FirebaseStorage = FirebaseStorage.getInstance()
    private var firebaseFiles = ArrayList<File>()
    var includedImages = ArrayList<ImageData>()

    private val placesLinksHashmap = PlacesLinksHashmap()
    private val imageDataCreator = ImageDataCreator(content, firebaseFiles, includedImages)

    private val imagelinkpos = 0 // Position of firebase image link in list

    /**
     * Get the images from firebase for the selected location and return the array of URIs
     */
    fun retrieveSelectedPlaceImages(placeName: String) {

        // Handle cases to download the correct data

        // Because we need to access exif information, we have to download the image
        val storageRef = firebaseStorage.reference

        val selectedMapLinks = placesLinksHashmap.getPlaceLinks(placeName)

        selectedMapLinks!!.forEach { linksList ->
            val storagePathRef = storageRef.child(linksList[imagelinkpos])
            val tempFile = File.createTempFile("images", "jpg")

            storagePathRef.getFile(tempFile).addOnSuccessListener {
                firebaseFiles.add(tempFile)


            }.addOnCompleteListener {
                // Update the map as every firebase operation completes, and update the map
                try {
                    includedImages = imageDataCreator.createIncludedImageData()
                    // Set the last inserted image links to be the firebase and wikipedia references
                    includedImages.last().setAssociatedLinks(linksList)
                    associatedPlaceMap.onFirebaseComplete(includedImages)
                }catch(exIllegal: IllegalStateException){
                    // If going quickly between place views this may fail, so handle the exception
                    Log.w("Firebase illegal state", "Illegal state while downloading images.")
                }
            }

        }

    }

}