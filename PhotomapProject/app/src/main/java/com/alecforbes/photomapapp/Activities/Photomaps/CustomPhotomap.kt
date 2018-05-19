package com.alecforbes.photomapapp.Activities.Photomaps

//import com.google.android.gms.location.FusedLocationProviderClient
//import com.google.android.gms.location.LocationServices
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.alecforbes.photomapapp.Activities.MapFragments.CustomPhotomapFragment
import com.alecforbes.photomapapp.Controllers.Database.DatabaseHelper
import com.alecforbes.photomapapp.Controllers.FileDataController
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

        fileDataController = FileDataController(contentResolver)

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
                .add(R.id.photomapFrameLayout, customMapFragment)
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
        val mapLayout = findViewById<FrameLayout>(R.id.photomapFrameLayout)
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
                val layoutParams = ViewGroup.LayoutParams(200, 200)
                layout.layoutParams = layoutParams
                layout.gravity = Gravity.CENTER

                // Each image should be a button to tap to bring up a larger preview
                val imageButton = ImageButton(applicationContext) // todo button listeners
                imageButton.layoutParams = layoutParams
                imageButton.scaleType = ImageView.ScaleType.CENTER_CROP
                imageButton.setImageBitmap(imageData.getImageBitmap()) //fixme not thumbnail bitmap

                // Set up the listener for clicking to create a more detailed view

                imageButton.setOnClickListener {

                    // Get the address of the image from the lat long
                    // TODO maybe store it, and only get if it's not already stored in the imagedata

                    // Only get the address data if is not already collected
                    if (imageData.realAddress == null){

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

    fun createIndvView(imageData: ImageData){

        // Fill the included Image View with the data of the image clicked in timeline
        indvImageView.setImageBitmap(imageData.getImageBitmap())

        imageAddressValue.text = "REVERSE GEOCDOE"

        if (imageData.dateTimeTaken != "0 0") { // 0 0 is returned when no timestamp was found

            // Split the date time into the time and data values
            val splitDateTime = imageData.dateTimeTaken.split(" ")
            dateTakenValue.text = splitDateTime[0]
            imageTimeTakenValue.text = splitDateTime[1]
        }else{
            imageTimeTakenValue.text = "Unknown"
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

}
