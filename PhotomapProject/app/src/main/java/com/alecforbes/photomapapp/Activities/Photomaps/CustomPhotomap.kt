package com.alecforbes.photomapapp.Activities.Photomaps

//import com.google.android.gms.location.FusedLocationProviderClient
//import com.google.android.gms.location.LocationServices
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
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
open class CustomPhotomap : AppCompatActivity(), OneMoreFabMenu.OptionsClick {

    //var selectedData = ArrayList<ImageData>()
    // Store the images as objects with all relevant info

    // TODO permission handling
    private val PICK_DATA = 1

    lateinit var fileDataController: FileDataController
    lateinit var customMapFragment: CustomPhotomapFragment
    private var locationManager: LocationManager? = null
    private var lastLoc: Location? = null

    private var screenSize: Int? = null

    //private val locationListener: LocationListener? = null

    //private val imagePreviewPane = imagePreviewPane
    // Polling values for GPS update intervals

    // Images stored in the preview pane
    private var previewImageUriHashMap = HashMap<String, String>()
    //private lateinit var fusedLocationClient: FusedLocationProviderClient

    private  var databaseHelper = DatabaseHelper(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Custom Photomap"

        this.setScreenResolution()
        fileDataController = FileDataController(contentResolver, screenSize!!)

        setContentView(R.layout.activity_photomap)

        // Set up the FAB on the custom map with options
        photomapActionButton.setOptionsClick(this@CustomPhotomap)

        // Set camera to current location FIXME
/*                requestGPSPermissions()
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?

                try {
                    // We don't need continuous location updates, just the last one to centre camera

                    fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
                    fusedLocationClient.lastLocation
                            .addOnSuccessListener { location : Location? ->
                                // Got last known location
                                lastLoc = location
                            }
                } catch (securityEx: SecurityException){
                    requestGPSPermissions()
                    // todo could set it up so if they deny, it just default to default pos on map
                }*/
        customMapFragment = CustomPhotomapFragment.newCustomInstance()

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
            R.id.remove_timeline_option -> customMapFragment.clearPhotoTimeline()
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
                    databaseHelper.addMap(savedMapName, fileDataController.imageUris)

                } else {
                    // todo do something appropriate
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

    private fun shareMap(){
        // TODO
    }

    private fun requestGPSPermissions(){
        val permissions = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION)
        ActivityCompat.requestPermissions(this, permissions,0)
    }

    /**
     * Add images that exist on the map to the preview pane at the top of the screen
     */
    private fun addImagesToPreview(){

        fileDataController.selectedData.forEach{ imageData ->

            val imageUri = imageData.file.absolutePath.toString()

            // Only add the image to the preview if it isn't already on the map
            if (!previewImageUriHashMap.containsKey(imageUri)) {

                val layout = LinearLayout(applicationContext)

                var PREVIEW_THUMBNAIL_SIZE = 300

                if (screenSize!! > 2073600){  // Greater than 1080p
                    PREVIEW_THUMBNAIL_SIZE = 500
                }

                val layoutParams = ViewGroup.LayoutParams(PREVIEW_THUMBNAIL_SIZE, PREVIEW_THUMBNAIL_SIZE)
                layout.layoutParams = layoutParams
                layout.gravity = Gravity.CENTER_HORIZONTAL

                // Each image should be a button to tap to bring up a larger preview
                val imageButton = ImageButton(applicationContext) // todo button listeners
                imageButton.layoutParams = layoutParams
                imageButton.adjustViewBounds = true
                imageButton.scaleType = ImageView.ScaleType.FIT_XY

                imageButton.setImageBitmap(imageData.getImageBitmap()) //fixme not thumbnail bitmap

                // Set up the listener for clicking to create a more detailed view

                imageButton.setOnClickListener {

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

                imagePreviewPane.addView(imageButton)
                previewImageUriHashMap[imageUri] = ""
            }

        }
        // Now draw lines between the images
        customMapFragment.addPhotoTimeline()
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
            val mapsIntent = Intent(android.content.Intent.ACTION_VIEW, Uri.parse(imageData.realAddress)) // fixme
            mapsIntent.setClassName("com.google.android.apps.maps",
                    "com.google.android.maps.MapsActivity")

            startActivity(mapsIntent)
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
                customMapFragment.setMapBounds()

            }

        }
    }

    /**
     * Helper function to get the resolution of a screen. If greater than 1080p, the image
     * thumbnails and timeline photos should be larger.
     */
    private fun setScreenResolution(){
        val displayMetrics = DisplayMetrics()

        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenHeight = displayMetrics.heightPixels
        val screenWidth = displayMetrics.widthPixels

        screenSize = screenHeight * screenWidth
    }

}
