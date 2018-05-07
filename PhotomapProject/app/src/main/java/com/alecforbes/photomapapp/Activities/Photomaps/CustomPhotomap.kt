package com.alecforbes.photomapapp.Activities.Photomaps

import android.content.Intent
import android.graphics.BitmapFactory
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import com.alecforbes.photomapapp.Activities.MapFragments.CustomPhotomapFragment
import com.alecforbes.photomapapp.Model.ImageData
import com.alecforbes.photomapapp.R
import com.dekoservidoni.omfm.OneMoreFabMenu
import kotlinx.android.synthetic.main.activity_photomap.*
import kotlinx.android.synthetic.main.timeline_scroll.*
import java.io.File

// FIXME Open keyword means this class can be inherited from, needed?
open class CustomPhotomap : AppCompatActivity(), OneMoreFabMenu.OptionsClick {

    var selectedImages = ArrayList<ImageData>()
    // Store the images as objects with all relevant info


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Custom Photomap"

        //val timelineScroll = LayoutInflater.from(applicationContext).inflate(R.layout.timeline_scroll, parent, false)

        val imagesIntent = intent
        getSelectedImageUris(imagesIntent)

        setContentView(R.layout.activity_photomap)

        setupOptionsButton()

        // Create bundle to send images to fragment
        val images = Bundle()
        images.putParcelableArrayList("selectedImages", selectedImages)
        val customMapFragment = CustomPhotomapFragment.newInstance(selectedImages)

        // As the map is a fragment, initialise it in a view (but just the constraint as the map fills the view)
        supportFragmentManager.beginTransaction()
                .add(R.id.photomapConstraint, customMapFragment)
                .commit()




        print("")
        //val test = Intent(this, TimelineScroll::class.java)
        //startActivity(test)

        //test.bringToFront()

    }

    //override fun onRefres

    override fun onOptionClick(optionId: Int?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        var test = ""

        when(optionId) {
            R.id.main_photomap_option -> test = "Clicky"
            R.id.add_files_option -> test = "nyes"
        }
    }

    /**
     * Set up the Floating Action Button (FAB) for interactions with the map
     */
    private fun setupOptionsButton(){
       //val optionsFab = photomapActionButton
       // photomapActionButton.setOnClickListener {
            // Bring up menu of options

       // }
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
