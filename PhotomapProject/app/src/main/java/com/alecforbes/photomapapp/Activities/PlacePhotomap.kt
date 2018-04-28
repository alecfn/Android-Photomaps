package com.alecforbes.photomapapp.Activities

import android.graphics.BitmapFactory
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import com.alecforbes.photomapapp.Model.ImageData
import com.alecforbes.photomapapp.Model.PlacesLinksHashmap
import com.alecforbes.photomapapp.R
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import java.io.File

/**
 * A place photomap inherits methods from the Custom photomap, as some functionality is not
 * available in a place photomap
 */
class PlacePhotomap : AppCompatActivity() {

    val firebaseStorage: FirebaseStorage = FirebaseStorage.getInstance()

    var firebaseFiles = ArrayList<File>()
    var includedImages = ArrayList<ImageData>()

    val placesLinksHashmap = PlacesLinksHashmap()

    @RequiresApi(Build.VERSION_CODES.N)// TODO api level
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // FIXME should either call super or not inherit here
        setContentView(R.layout.activity_place_photomap)

        val placesIntent = intent
        val selectedLoc = placesIntent.getStringExtra("SelectedLocation")
        title = "$selectedLoc Photomap"

        retrieveSelectedPlaceImages(selectedLoc)

        // The URIs for images in a place photomap come from firebase downloads, not an intent

        //createIncludedImageData()

    }

    /**
     * Only once firebase has successfully retrieved images should the map fragment be created
     */
    private fun onFirebaseComplete(){

        val customMapFragment = PhotomapFragment.newInstance(includedImages)

        // As the map is a fragment, initialise it in a view (but just the constraint as the map fills the view)
        supportFragmentManager.beginTransaction()
                .add(R.id.placePhotomapConstraint, customMapFragment)
                .commit()
    }

    /**
     * Get the images from firebase for the selected location and return the array of URIs
     */
    @RequiresApi(Build.VERSION_CODES.N) // todo api level
    private fun retrieveSelectedPlaceImages(placeName: String) {
        // TODO probably move to own class, needs to actually do everything based on selection
        // Handle cases to download the correct data

        // Because we need to access exif information, we have to download the image
        val storageRef = firebaseStorage.reference

        val selectedMapLinks = placesLinksHashmap.getPlaceLinks(placeName)

        selectedMapLinks!!.forEach {
            val storagePathRef = storageRef.child(it)
            val tempFile = File.createTempFile("images", "jpg")

            storagePathRef.getFile(tempFile).addOnSuccessListener {
                firebaseFiles.add(tempFile)
                //var test = BitmapFactory.decodeFile(tempFile.absolutePath)
                print("")

            }.addOnCompleteListener {
                createIncludedImageData()
                onFirebaseComplete()
            }

        }

    }

    /**
     * Place map Image data objects need to be created differently from a CustomPhotomap, as they
     * are not selected via intents
     */
    @RequiresApi(Build.VERSION_CODES.N) // Todo api levels
    // FIXME still think this method is too similar to the one in CustomPhotomap, could be good to
    // todo put that in another class or something for both
    private fun createIncludedImageData(){

        firebaseFiles.forEach {

            val stream = contentResolver.openInputStream(Uri.fromFile(it))
            val exif = ExifInterface(stream)
            val file = File(it.path)

            val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(Uri.fromFile(it)))

            val selectedImage = ImageData(file, bitmap, exif)
            includedImages.add(selectedImage)
        }
        print("")
    }



        // TODO  add a progress bar
        // TODO 1mb limit at the moment, increase if needed
//        val MAX_SIZE = (1024 * 1024).toLong()
//
//        // Use get bytes as the images will only be stored in memory, rather than on the device
//        httpsRef.getBytes(MAX_SIZE)
//                .addOnSuccessListener { bytes ->
//                    imageBytes.add(bytes)
//                    print("")
//                }
//                .addOnFailureListener { exception ->
//                    //TODO
//                }
//        print("")

}
