package com.alecforbes.photomapapp.Activities.Photomaps

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import com.alecforbes.photomapapp.Activities.MapFragments.CustomPhotomapFragment
import com.alecforbes.photomapapp.Controllers.FileDataController
import com.alecforbes.photomapapp.R
import com.dekoservidoni.omfm.OneMoreFabMenu
import kotlinx.android.synthetic.main.activity_photomap.*

// FIXME Open keyword means this class can be inherited from, needed?
open class CustomPhotomap : AppCompatActivity(), OneMoreFabMenu.OptionsClick {

    //var selectedData = ArrayList<ImageData>()
    // Store the images as objects with all relevant info

    // TODO permission handling
    private val PICK_DATA = 1

    lateinit var fileDataController: FileDataController
    lateinit var customMapFragment: CustomPhotomapFragment


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Custom Photomap"

        //val timelineScroll = LayoutInflater.from(applicationContext).inflate(R.layout.timeline_scroll, parent, false)

        //val imagesIntent = intent
        //getSelectedImageUris(imagesIntent)

        fileDataController = FileDataController(contentResolver)

        setContentView(R.layout.activity_photomap)

        // Set up the FAB on the custom map with options
        photomapActionButton.setOptionsClick(this@CustomPhotomap)

        // Create bundle to send images to fragment
        //val images = Bundle()
        //images.putParcelableArrayList("selectedData", selectedData)
        customMapFragment = CustomPhotomapFragment.newInstance()

        // As the map is a fragment, initialise it in a view (but just the constraint as the map fills the view)
        supportFragmentManager.beginTransaction()
                .add(R.id.photomapConstraint, customMapFragment)
                .commit()

    }

    override fun onResumeFragments() {
        super.onResumeFragments()

        // Call bring to front on the other elements or the map will draw on top and hide them
        photomapActionButton.bringToFront()
        customTimeline.bringToFront()
    }

    /**
     * Set up the Floating Action Button (FAB) for interactions with the map
     */
    override fun onOptionClick(optionId: Int?) {

        // Use Kotlin lambdas to set up FAB click responses
        when(optionId) {
            R.id.main_photomap_option -> test()
            R.id.add_files_option -> getDataFromGallery()
            R.id.add_timeline_option -> test()
            R.id.remove_timeline_option -> test()
            R.id.clear_map_option -> test()
            R.id.share_map_option -> test()
        }
    }

    private fun test(){
        print("")
    }

    private fun getDataFromGallery(){

        val customSelectIntent = Intent(Intent.ACTION_PICK)
        customSelectIntent.type = "image/*"
        customSelectIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        customSelectIntent.action = Intent.ACTION_GET_CONTENT

        if (customSelectIntent.resolveActivity(packageManager) != null){
            startActivityForResult(Intent.createChooser(customSelectIntent, "Select photos for photomap"), PICK_DATA)
        }

    }


    /**
     * This function will handle the selected images a user adds to a photomap
     */
    @RequiresApi(Build.VERSION_CODES.N) //todo versions
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_DATA){

            //addDataIntent.putExtra("imageData", data)
            if (data != null) {

                fileDataController.getSelectedImageUris(data)
                customMapFragment.setSelectedData(fileDataController.selectedData)
                customMapFragment.addImagePreviews()
                customMapFragment.setMapBounds()

            }

        }
    }

}
