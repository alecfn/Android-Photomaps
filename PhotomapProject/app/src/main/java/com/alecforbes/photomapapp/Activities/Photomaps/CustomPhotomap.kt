package com.alecforbes.photomapapp.Activities.Photomaps

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import com.alecforbes.photomapapp.Activities.MapFragments.CustomPhotomapFragment
import com.alecforbes.photomapapp.Controllers.FileDataController
import com.alecforbes.photomapapp.R
import com.dekoservidoni.omfm.OneMoreFabMenu
import kotlinx.android.synthetic.main.activity_photomap.*
import kotlinx.android.synthetic.main.timeline_scroll.*

// FIXME Open keyword means this class can be inherited from, needed?
open class CustomPhotomap : AppCompatActivity(), OneMoreFabMenu.OptionsClick {

    //var selectedData = ArrayList<ImageData>()
    // Store the images as objects with all relevant info

    // TODO permission handling
    private val PICK_DATA = 1

    lateinit var fileDataController: FileDataController
    lateinit var customMapFragment: CustomPhotomapFragment

    //private val imagePreviewPane = imagePreviewPane


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Custom Photomap"

        fileDataController = FileDataController(contentResolver)

        setContentView(R.layout.activity_photomap)

        // Set up the FAB on the custom map with options
        photomapActionButton.setOptionsClick(this@CustomPhotomap)

        customMapFragment = CustomPhotomapFragment.newCustomInstance()

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

    /**
     * Add images that exist on the map to the preview pane at the top of the screen
     */
    private fun addImagesToPreview(){

        fileDataController.selectedData.forEach{

            val layout = LinearLayout(applicationContext)
            val layoutParams = ViewGroup.LayoutParams(200, 200)
            layout.layoutParams = layoutParams
            layout.gravity = Gravity.CENTER

            // Each image should be a button to tap to bring up a larger preview
            val imageView = ImageButton(applicationContext) // todo button listeners
            imageView.layoutParams = layoutParams
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            imageView.setImageBitmap(it.getImageBitmap()) //fixme not thumbnail bitmap
            // todo organise by date taken, dont let duplicates appear

            imagePreviewPane.addView(imageView)

        }
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

            if (data != null) {

                fileDataController.getSelectedImageUris(data)
                customMapFragment.setSelectedData(fileDataController.selectedData)
                customMapFragment.addImagePreviews()
                customMapFragment.setMapBounds()

                // Create the preview pane from selected images
                this.addImagesToPreview()

            }

        }
    }

}
