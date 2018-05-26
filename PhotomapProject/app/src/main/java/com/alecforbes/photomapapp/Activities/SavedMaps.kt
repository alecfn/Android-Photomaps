package com.alecforbes.photomapapp.Activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.alecforbes.photomapapp.Activities.Photomaps.CustomPhotomap
import com.alecforbes.photomapapp.Controllers.Database.DatabaseHelper
import com.alecforbes.photomapapp.Controllers.FileDataController
import com.alecforbes.photomapapp.R
import kotlinx.android.synthetic.main.activity_saved_maps.*

class SavedMaps : AppCompatActivity() {

    private var databaseHelper = DatabaseHelper(this)
    private var savedMaps = HashMap<String, ArrayList<Uri>>() // This stores the map name and uris

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_maps)

        title = "Your Saved Photomaps"

        getSavedMapsFromDB()

        populateSavedView()
    }

    /**
     * Get the URIs stored in the database for the selected map and rebuild the ImageData to make
     * the maps again
     */
    private fun getSavedMapsFromDB(){
        val savedMapNames = databaseHelper.getSavedMaps()


        savedMapNames.forEach {
            val savedMapUris = databaseHelper.getSavedMapUris(it)
            savedMaps[it] = savedMapUris
        }

    }

    /**
     * Get all the map names saved in the database and the first image to use as a preview
     */
    private fun populateSavedView(){


        savedMaps.forEach {


            // Now create a bitmap from the first uri to act as the image preview for the saved map

            // Only system apps can use MANAGE_DOCUMENTS permissions, so get the byte stream
            //val absPath = it.value[0].path
            //val previewUriStream = contentResolver.openInputStream(it.value[0])

            try{

                val savedMapAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, ArrayList(savedMaps.keys))
                savedMapListView.adapter = savedMapAdapter

                savedMapListView.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->

                    // todo make this look pretty, maybe colour and different layout
                    val selectedSavedMap = savedMapListView.getItemAtPosition(i) as String
                    val savedMapIntent = Intent(this, CustomPhotomap::class.java)
                    savedMapIntent.putExtra("SavedMapName", selectedSavedMap)
                    //savedMapIntent.putExtra("SelectedMapImages", savedMaps[selectedSavedMap])
                    val selectedMapImages = savedMaps[selectedSavedMap]

                    // Data returned from the DB will be of 'URIString' data types, recast to string
                    val selectedImageUriStrings = ArrayList<String>()
                    selectedMapImages!!.forEach { uri ->
                        selectedImageUriStrings.add(uri.toString())
                    }

                    // Now create the image data objects fixme dont do this here cant be parceled
                    savedMapIntent.putStringArrayListExtra("SavedImageUris", selectedImageUriStrings)
                    savedMapIntent.putExtra("IsSavedMap", true)
                    //val savedFileData = FileDataController(contentResolver, screenSize = 0)  // todo screen size here?
                    //savedFileData.getSelectedImageUrisFromArray(selectedMapImages!!)
                    //savedMapIntent.putExtra("SavedMapImages", savedFileData.selectedData)

                    //val selectedImages = ArrayList<ImageData>()


                    startActivity(savedMapIntent)

                }

            }catch (ex: Exception){
                // todo put in a placeholder or something
            }

            //val byteData = previewUriStream.readBytes()

            //previewUriStream.close()

            //val previewBitmap = BitmapFactory.decodeByteArray(byteData, 0, byteData.size)

            //savedPreview.setImageBitmap(previewBitmap)



        }




    }


}
