package com.alecforbes.photomapapp.Activities.Photomaps

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.alecforbes.photomapapp.Activities.MapFragments.PhotomapFragment
import com.alecforbes.photomapapp.BuildConfig
import com.alecforbes.photomapapp.Controllers.Database.DatabaseHelper
import com.alecforbes.photomapapp.Controllers.FileDataController
import com.alecforbes.photomapapp.Controllers.ImageGeocoder
import com.alecforbes.photomapapp.Model.ImageData
import com.alecforbes.photomapapp.R
import com.dekoservidoni.omfm.OneMoreFabMenu
import com.google.android.gms.maps.model.Marker
import kotlinx.android.synthetic.main.activity_photomap.*
import kotlinx.android.synthetic.main.individual_image_view.*
import kotlinx.android.synthetic.main.timeline_scroll.*
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

/**
 * This class defines the behaviour of a custom photomap created by the user. A relevant custom
 * map instance of a photomap fragment is created in this class and populated from the selections
 * a user makes. The FAB defines much of the interaction that a user can perform in this activity.
 */
class CustomPhotomap : PhotomapActivity(), OneMoreFabMenu.OptionsClick {

    // Store the images as objects with all relevant info
    private val pickData = 1

    private lateinit var fileDataController: FileDataController
    private lateinit var mapFragment: PhotomapFragment

    // Images stored in the timeline/preview pane
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
            mapFragment = PhotomapFragment.newSavedInstance(fileDataController.selectedData)
            mapFragment.setSelectedData(fileDataController.selectedData)

        } else {
            mapFragment = PhotomapFragment.newCustomInstance()
        }

        // As the map is a fragment, initialise it in a view (but just the constraint as the map fills the view)
        supportFragmentManager.beginTransaction()
                .add(R.id.photomapConstraintLayout, mapFragment)
                .commit()

    }

    /**
     * Ensure some graphics are always on the front of the object.
     */
    override fun onResumeFragments() {
        super.onResumeFragments()

        // Call bring to front on the other elements or the map will draw on top and hide them
        photomapActionButton.bringToFront()
        customTimeline.bringToFront()

    }

    /**
     * Set up the Floating Action Button (FAB) for interactions with the map.
     */
    override fun onOptionClick(optionId: Int?) {

        // Use Kotlin lambdas to set up FAB click responses, do what we can off the main thread
        when(optionId) {
            R.id.add_files_option -> getDataFromSelectedApp()
            R.id.add_timeline_option -> addImagesToPreview()
            R.id.remove_timeline_option -> clearTimelinePreview()
            R.id.save_photomap_option -> saveMap()
            R.id.clear_map_option -> clearViewsAndData()
            R.id.share_map_option -> shareMap()
        }
    }

    /**
     * Get data from the gallery/documents application that the user has selected to use to populate
     * the map with image data.
     *
     * Based on:
     * https://stackoverflow.com/questions/5309190/android-pick-images-from-gallery (Best answer)
     */
    private fun getDataFromSelectedApp(){

        // For whatever reason only some apps let you select multiple images (Like Google Photos)
        val galleryIntent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        galleryIntent.type = "image/*"
        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)

        if (galleryIntent.resolveActivity(packageManager) != null){
            startActivityForResult(Intent.createChooser(galleryIntent, "Select images with:"), pickData)
        }

    }

    /**
     * When a user clicks 'Clear Photomap', clear all of the views, data structures and all relevant
     * information to building a photomap. This essentially resets the state of the map.
     */
    private fun clearViewsAndData(){
        imagePreviewPane.removeAllViews()
        timelineCardView.visibility = View.GONE
        mapFragment.clearMap() // Also clear drawables on the map fragment
        fileDataController.selectedData.clear()
        fileDataController.imageUris.clear()
        previewImageUriHashMap.clear() // Clear uri hashmap so old images can be added again
        mapFragment.setUpClusterer() // Re initialise the clusterer so there are no leftover images
    }

    /**
     * Save the map ImageData objects to an SQLite instance (controlled by the DatabaseHelper)
     */
    private fun saveMap(){

        val saveInputBuilder = AlertDialog.Builder(this)

        // Set up an alert dialog style box for the user to enter a name
        val saveInputText = EditText(this)

        // Toast alert to tell user to add images
        if (fileDataController.imageUris.size == 0) {
            Toast.makeText(this@CustomPhotomap, "There are no images to save!",
                    Toast.LENGTH_LONG).show()
            return
        }

        with(saveInputBuilder) {
            saveInputBuilder.setTitle("Enter a name for your creation!")


            setPositiveButton("Save"){
                dialog, _ -> // Unused variables for buttons can be called _
                val savedMapName = saveInputText.text.toString()
                dialog.dismiss()

                // If there's no images added, don't save
                if (fileDataController.imageUris.size > 0) {
                    // Check if the map exists in the Db, and ask the user if they want to overwrite
                    val mapExists = databaseHelper.checkMapExists(databaseHelper.readableDatabase, savedMapName)

                    if(mapExists){ // Create a new dialogue asking confirmation

                        val overwriteAlert= AlertDialog.Builder(this@CustomPhotomap)

                        with(overwriteAlert){
                            overwriteAlert.setTitle("$savedMapName already exists. Would you like to overwrite it?")

                            setPositiveButton("Overwrite"){
                                _, _ ->
                                databaseHelper.updateMap(savedMapName, fileDataController.imageUris)

                                // Toast to confirm overwrite
                                Toast.makeText(this@CustomPhotomap, "Map $savedMapName successfully updated!",
                                        Toast.LENGTH_LONG).show()
                            }

                            setNegativeButton("Cancel"){
                                dialog, _ ->
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
                dialog, _ ->
                dialog.dismiss()
            }
        }

        val saveDialog = saveInputBuilder.create()
        saveDialog.setView(saveInputText)
        saveDialog.show()

    }


    /**
     * Clear the map timeline preview images and the clear the polylines on the map, the components
     * which make up the timeline feature (but only those elements)
     */
    private fun clearTimelinePreview(){

        timelineCardView.invalidate()
        timelineCardView.visibility = View.GONE

        // Now clear the polylines from the map fragment
        mapFragment.clearTimelinePolylines()
    }

    /**
     * Launch a share intent when the user clicks 'share map' in the FAB.
     * Currently just shares image files on a map.
     * Uses a 'FileProvider' to create Content Uris for the images and make them sharable.
     *
     * https://stackoverflow.com/questions/38200282/android-os-fileuriexposedexception-file-storage-emulated-0-test-txt-exposed
     * https://stackoverflow.com/questions/42516126/fileprovider-illegalargumentexception-failed-to-find-configured-root
     */
    private fun shareMap(){
        val shareIntent = Intent(android.content.Intent.ACTION_SEND_MULTIPLE)

        // Uris in a saved map cannot be shared directly as they are file uris, so create content uris

        val shareUris = ArrayList<Uri>()
        fileDataController.imageUris.forEach { uri ->
            try{ // If the map was saved, need to make content URIs
                shareUris.add(FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", File(uri.path)))
            }catch (exIllegalArg: IllegalArgumentException){ // If unsaved, can just send file URIs
                shareUris.add(uri)
            }

        }

        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, shareUris)
        shareIntent.type = "image/*"

        // Add some text to the share intent
        val shareBody = "Download Photomaps here: <link to play store> and create your own!"
        shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Check out my cool new photomap!")
        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody)

        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(Intent.createChooser(shareIntent, "Share via:"))
    }


    /**
     * Add images that exist on the map to the preview pane at the top of the screen as a horizontal
     * scrollview of images that can be tapped for an individual view, just as images on the map can
     * be.
     */
    private fun addImagesToPreview(){

        // If there's no images, don't make a timeline view
        if(fileDataController.selectedData.size == 0){
            Toast.makeText(applicationContext, "There are no images on the map!", Toast.LENGTH_SHORT).show()
            return
        }

        timelineCardView.visibility = View.VISIBLE

        fileDataController.selectedData.forEach{ imageData ->

            val imageUri = imageData.file.absolutePath.toString()

            // Only add the image to the preview if it isn't already on the map
            if (!previewImageUriHashMap.containsKey(imageUri)) {

                val customImageButton = defineHorizontalScrollViewButton()
                customImageButton.setImageBitmap(
                        BitmapFactory.decodeByteArray(imageData.getImageThumbnail(), 0,
                                imageData.getImageThumbnail().size))

                // Set up the listener for clicking to create a more detailed view

                customImageButton.setOnClickListener {

                    // Get the address of the image from the lat long
                    setImageAddress(imageData)
                    createIndvView(imageData)

                }
                imagePreviewPane.addView(customImageButton)
                previewImageUriHashMap[imageUri] = ""
            }

        }
        // Now draw lines between the images
        mapFragment.addTimelinePolylines()
    }

    /**
     * Set the image address attribute using an ImageGeocoder object. This address is a real street
     * address and is displayed when an image in the map is tapped.
     */
    private fun setImageAddress(imageData: ImageData){

        // Only get the address data if is not already collected
        if (imageData.realAddress == null){
            val lat = imageData.latitude.toDouble()
            val long = imageData.longitude.toDouble()
            val imageGeocoder = ImageGeocoder(lat, long, applicationContext)
            val imageAddress = imageGeocoder.getAddressFromLocation()
            imageData.realAddress = imageAddress
        }

        imageAddressValue.text = imageData.realAddress
    }

    /**
     * Creates a card view with the relevant information collected for that image. Will be displayed
     * when an image is clicked on the map or in the horizontal scroll view of the timeline.
     *
     * Reference to:
     * https://stackoverflow.com/questions/42854152/how-to-convert-hhmmss-to-hhmm-am-pm-in-android
     */
    private fun createIndvView(imageData: ImageData){

        // Fill the included Image View with the data of the image clicked in timeline
        indvImageView.setImageBitmap(imageData.getImageBitmap())
        val datePos = 0
        val timePos = 1

        if (imageData.timeTaken != "0" || imageData.datetaken != "0") { // 0 0 is returned when no timestamp was found

            // Split the date time into the time and data values
            val splitDateTime = imageData.dateTimeTaken.split(" ")

            // There may be some cases where only one value was so found handle that
            if (imageData.datetaken != "0") {
                // Reformat to be more user friendly
                try {
                    val reformattedDate = splitDateTime[datePos].replace(":", "/")
                    dateTakenValue.text = reformattedDate
                } catch (exIllegalArg: IllegalArgumentException){
                    // Just set the time to be the raw string if it fails to format
                    dateTakenValue.text = splitDateTime[datePos]
                }
            } else {
                dateTakenValue.text = "Unknown"
            }

            if (imageData.timeTaken != "0"){
                try {
                    var time = splitDateTime[timePos]
                    val splitTime = time.split(":")
                    val hour = splitTime[1].toInt()

                    // Add PM or AM to time as necessary
                    time = if(hour in 0..11){
                        "$time a.m."
                    }else {
                        "$time p.m."
                    }

                    imageTimeTakenValue.text = time
                }catch (exIllegalArg: IllegalArgumentException){
                    imageTimeTakenValue.text = splitDateTime[timePos]
                }
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
     * When the user clicks a marker, pass that in and get the corresponding ImageData from the
     * lat long values. Similar to what is done in a place map, but uses filedatacontroller instead
     * of firebase.
     */
    fun getImageDataFromMarker(clickedMarker: Marker?){

        val markerLatLong = clickedMarker!!.position

        var clickedImageData: ImageData? = null
        fileDataController.selectedData.forEach { imageData ->
            if (imageData.latLong == markerLatLong){
                clickedImageData = imageData
            }
        }

        // Now create the individual image view from the found data

        setImageAddress(clickedImageData!!)
        createIndvView(clickedImageData!!)
    }


    /**
     * This function will handle the selected images a user adds to a photomap and call the relevant
     * functions to display image data on the map.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == pickData){

            if (data != null) {

                fileDataController.getSelectedImageUrisFromIntent(data)
                mapFragment.setSelectedData(fileDataController.selectedData)
                mapFragment.sortByTimeTaken()
                mapFragment.addImagePreviews()

            }

        }
    }

    /**
     * Destroy the fragment when the user exits
     */
    override fun onDestroy() {
        supportFragmentManager.beginTransaction().remove(mapFragment).commitAllowingStateLoss()
        super.onDestroy()
    }

}
