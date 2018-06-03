package com.alecforbes.photomapapp.Activities.Photomaps

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.*
import com.alecforbes.photomapapp.Activities.MapFragments.CustomPhotomapFragment
import com.alecforbes.photomapapp.Controllers.Database.DatabaseHelper
import com.alecforbes.photomapapp.Controllers.FileDataController
import com.alecforbes.photomapapp.Controllers.ImageGeocoder
import com.alecforbes.photomapapp.Model.ImageData
import com.alecforbes.photomapapp.R
import com.dekoservidoni.omfm.OneMoreFabMenu
import kotlinx.android.synthetic.main.activity_photomap.*
import kotlinx.android.synthetic.main.individual_image_view.*
import kotlinx.android.synthetic.main.timeline_scroll.*

// FIXME Open keyword means this class can be inherited from, needed?
class CustomPhotomap : PhotomapActivity(), OneMoreFabMenu.OptionsClick {

    // Store the images as objects with all relevant info

    // TODO permission handling
    private val PICK_DATA = 1

    lateinit var fileDataController: FileDataController
    lateinit var customMapFragment: CustomPhotomapFragment

    // Images stored in the preview pane
    private var previewImageUriHashMap = HashMap<String, String>()

    private  var databaseHelper = DatabaseHelper(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Custom Photomap"

        setScreenResolution()
        fileDataController = FileDataController(contentResolver, screenSize!!)

        setContentView(R.layout.activity_photomap)

        // Set up the FAB on the custom map with options
        photomapActionButton.setOptionsClick(this@CustomPhotomap)


        // Get the arguments from the intent to check if this is a saved map or a new one
        val isSavedMap = intent.getBooleanExtra("IsSavedMap", false)

        if(isSavedMap) {
            // Image data cannot be parceled, so that is built here (Kotlin parcelize issue)
            val savedImageUris = intent.getStringArrayListExtra("SavedImageUris")
            fileDataController.getSelectedImageUrisFromArray(savedImageUris)
            customMapFragment = CustomPhotomapFragment.newSavedInstance(fileDataController.selectedData)
            customMapFragment.setSelectedData(fileDataController.selectedData)

        } else {
            customMapFragment = CustomPhotomapFragment.newCustomInstance()
        }

        // As the map is a fragment, initialise it in a view (but just the constraint as the map fills the view)
        supportFragmentManager.beginTransaction()
                .add(R.id.photomapConstraintLayout, customMapFragment)
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
            R.id.add_files_option -> getDataFromGallery()
            R.id.add_timeline_option -> addImagesToPreview()
            R.id.remove_timeline_option -> clearTimelinePreview()
            R.id.save_photomap_option -> saveMap()
            R.id.clear_map_option -> clearViewsAndData()
            R.id.share_map_option -> shareMap()
        }
    }

    private fun getDataFromGallery(){

        val customSelectIntent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        customSelectIntent.type = "image/*"
        customSelectIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        customSelectIntent.action = Intent.ACTION_GET_CONTENT

        if (customSelectIntent.resolveActivity(packageManager) != null){
            startActivityForResult(Intent.createChooser(customSelectIntent, "Select photos for photomap"), PICK_DATA)
        }

    }

    private fun clearViewsAndData(){
        val mapLayout = findViewById<ConstraintLayout>(R.id.photomapConstraintLayout)
        imagePreviewPane.removeAllViews()
        timelineCardView.visibility = View.GONE
        mapLayout.removeView(imagePreviewPane)
        mapLayout.removeView(findViewById(R.id.customTimeline))
        customMapFragment.clearMap() // Also clear drawables on the map fragment
        fileDataController.selectedData.clear()
        fileDataController.imageUris.clear()
        //mapLayout.removeView(imagePreviewPane)
        previewImageUriHashMap.clear() // Clear uri hashmap so old images can be added again
    }

    /**
     * Save the map ImageData objects to an SQLite instance (controlled by the DatabaseHelper)
     */
    private fun saveMap(){

        val saveInputBuilder = AlertDialog.Builder(this)

        // Set up an alert dialog style box for the user to enter a name
        val saveInputText = EditText(this)
        // fixme padding

        // Toast alert to tell user to add images
        if (fileDataController.imageUris.size == 0) {
            Toast.makeText(this@CustomPhotomap, "There are no images to save!",
                    Toast.LENGTH_LONG).show()
            return
        }

        with(saveInputBuilder) {
            saveInputBuilder.setTitle("Enter a name for your creation!")


            setPositiveButton("Save"){
                dialog, posButton ->
                val savedMapName = saveInputText.text.toString()
                dialog.dismiss()

                // If there's no images added, don't save
                if (fileDataController.imageUris.size > 0) {
                    // Check if the map exists in the Db, and ask the user if they want to overwrite
                    val mapExists = databaseHelper.checkMapExists(databaseHelper.readableDatabase, savedMapName)

                    if(mapExists){ // Create a new dialogue asking confirmation

                        val overwriteAlert= AlertDialog.Builder(this@CustomPhotomap)

                        with(overwriteAlert){
                            overwriteAlert.setTitle("A map with the name $savedMapName" +
                                    " already exists. Would you like to overwrite it?")

                            setPositiveButton("Overwrite"){
                                dialog, overwriteButton ->
                                databaseHelper.updateMap(savedMapName, fileDataController.imageUris)

                                // Toast to confirm overwrite
                                Toast.makeText(this@CustomPhotomap, "Map $savedMapName successfully updated!",
                                        Toast.LENGTH_LONG).show()
                            }

                            setNegativeButton("Cancel"){
                                dialog, negButton ->
                                dialog.dismiss()
                            }

                            val overwriteDialog = overwriteAlert.create()
                            overwriteDialog.show()

                        }

                    }else {

                        databaseHelper.addMap(savedMapName, fileDataController.imageUris)


                        // Toast to confirm save
                        Toast.makeText(this@CustomPhotomap, "Map $savedMapName saved successfully!",
                                Toast.LENGTH_LONG).show()
                    }

                } else {
                    Toast.makeText(this@CustomPhotomap, "No new images to save!",
                            Toast.LENGTH_LONG).show()
                }
            }

            setNegativeButton("Cancel"){
                dialog, negButton ->
                dialog.dismiss()
            }
        }

        val saveDialog = saveInputBuilder.create()
        saveDialog.setView(saveInputText)
        saveDialog.show()

    }


    /**
     * Clear the map timeline preview images and the clear the polylines on the map.
     */
    private fun clearTimelinePreview(){

        timelineCardView.invalidate()
        timelineCardView.visibility = View.GONE

        // Now clear the polylines from the map fragment
        customMapFragment.clearTimelinePolylines()
    }

    private fun shareMap(){
        // TODO
    }


    /**
     * Add images that exist on the map to the preview pane at the top of the screen
     */
    private fun addImagesToPreview(){

        timelineCardView.visibility = View.VISIBLE

        fileDataController.selectedData.forEach{ imageData ->

            val imageUri = imageData.file.absolutePath.toString()

            // Only add the image to the preview if it isn't already on the map
            if (!previewImageUriHashMap.containsKey(imageUri)) {

                val customImageButton = defineHorizontalScrollViewButton()

                customImageButton.setImageBitmap(imageData.getImageBitmap()) //fixme not thumbnail bitmap

                // Set up the listener for clicking to create a more detailed view

                customImageButton.setOnClickListener {

                    // Get the address of the image from the lat long
                    // TODO maybe store it, and only get if it's not already stored in the imagedata

                    // Only get the address data if is not already collected
                    if (imageData.realAddress == null){
                        val lat = imageData.latitude.toDouble()
                        val long = imageData.longitude.toDouble()
                        val imageGeocoder = ImageGeocoder(lat, long, applicationContext)
                        val imageAddress = imageGeocoder.getAddressFromLocation()
                        imageData.realAddress = imageAddress
                    }

                    imageAddressValue.text = imageData.realAddress

                    createIndvView(imageData)

                }

                imagePreviewPane.addView(customImageButton)
                previewImageUriHashMap[imageUri] = ""
            }

        }
        // Now draw lines between the images
        customMapFragment.addTimelinePolylines()
    }

    private fun createIndvView(imageData: ImageData){

        // Fill the included Image View with the data of the image clicked in timeline
        indvImageView.setImageBitmap(imageData.getImageBitmap())

        // FIXME this logic is a bit weird, could just get this earlier and assign
        if (imageData.timeTaken != "0" || imageData.datetaken != "0") { // 0 0 is returned when no timestamp was found

            // Split the date time into the time and data values
            val splitDateTime = imageData.dateTimeTaken.split(" ")

            // There may be some cases where only one value was so found handle that
            if (imageData.datetaken != "0") {
                dateTakenValue.text = splitDateTime[0]
            } else {
                dateTakenValue.text = "Unknown"
            }

            if (imageData.timeTaken != "0"){
                imageTimeTakenValue.text = splitDateTime[1]
            } else {
                imageTimeTakenValue.text = "Unknown"
            }

        }else{
            dateTakenValue.text = "Unknown"
            imageTimeTakenValue.text = "Unknown"
        }

        // Set the view in maps button to take the user to the Google Maps app from the listed addr
        viewInMapsButton.setOnClickListener {
            startMapsFromAddress(imageData.realAddress!!)
        }

        indvCloseButton.setOnClickListener {
            photomapIndvImageView.visibility = View.GONE
            photomapIndvImageView.invalidate()
        }

        photomapIndvImageView.visibility = View.VISIBLE
        photomapIndvImageView.bringToFront()
    }


    /**
     * This function will handle the selected images a user adds to a photomap
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_DATA){

            if (data != null) {

                fileDataController.getSelectedImageUrisFromIntent(data)
                customMapFragment.setSelectedData(fileDataController.selectedData)
                customMapFragment.addImagePreviews()
                customMapFragment.sortByTimeTaken()

            }

        }
    }

}
