package com.alecforbes.photomapapp.Activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.alecforbes.photomapapp.Activities.Photomaps.CustomPhotomap
import com.alecforbes.photomapapp.Controllers.Database.DatabaseHelper
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

            try{

                val savedMapAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, ArrayList(savedMaps.keys))
                savedMapListView.adapter = savedMapAdapter

                savedMapListView.onItemClickListener = AdapterView.OnItemClickListener {
                    adapterView, view, i, l ->

                    // todo make this look pretty, maybe colour and different layout
                    val selectedSavedMap = savedMapListView.getItemAtPosition(i) as String
                    val savedMapIntent = Intent(this, CustomPhotomap::class.java)
                    savedMapIntent.putExtra("SavedMapName", selectedSavedMap)
                    val selectedMapImages = savedMaps[selectedSavedMap]

                    // Data returned from the DB will be of 'URIString' data types, recast to string
                    val selectedImageUriStrings = ArrayList<String>()
                    selectedMapImages!!.forEach { uri ->
                        selectedImageUriStrings.add(uri.toString())
                    }

                    // Now store the image URIs in the intent to make the ImageData objects later
                    savedMapIntent.putStringArrayListExtra("SavedImageUris", selectedImageUriStrings)
                    savedMapIntent.putExtra("IsSavedMap", true)

                    startActivity(savedMapIntent)

                }
                // Long click listener to delete saved maps

                savedMapListView.setOnItemLongClickListener{ adapterView, view, i, l ->

                    val selectedSavedMap = savedMapListView.getItemAtPosition(i) as String

                    // Confirmation dialog to ask if user definitely wants to delete the map
                    val deleteAlert = AlertDialog.Builder(this)

                    with(deleteAlert){
                        deleteAlert.setTitle("Are you sure you want to delete the map $selectedSavedMap?")

                        setPositiveButton("Delete"){
                            dialog, deleteButton ->

                            // Calls to database helper to delete the map
                            databaseHelper.deleteMap(selectedSavedMap)
                            // Update the list view
                            savedMapAdapter.remove(savedMapAdapter.getItem(i))
                            savedMapAdapter.notifyDataSetChanged()

                        }

                        setNegativeButton("Cancel"){
                            dialog, negButton ->
                            dialog.dismiss()
                        }
                    }

                    deleteAlert.create()
                    deleteAlert.show()

                    return@setOnItemLongClickListener true
                }

            }catch (ex: Exception){
                // todo put in a placeholder or something
            }

        }

    }
}
