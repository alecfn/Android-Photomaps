package com.alecforbes.photomapapp.Activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.alecforbes.photomapapp.Activities.Photomaps.CustomPhotomap
import com.alecforbes.photomapapp.Controllers.Database.DatabaseHelper
import com.alecforbes.photomapapp.R
import kotlinx.android.synthetic.main.activity_saved_maps.*

/**
 * This activity defines the list of saved maps based on the entries contained in the database.
 * If no entries are present, a text view will be displayed letting the user know there are no
 * entries.
 */
class SavedMaps : AppCompatActivity() {

    private var databaseHelper = DatabaseHelper(this)
    private var savedMaps = HashMap<String, ArrayList<Uri>>() // This stores the map name and uris

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_maps)

        title = "Your Saved Photomaps"

        getSavedMapsFromDB()

        // If there's no maps yet, show a text view in the centre of the screen and return
        if(savedMaps.size == 0){
            noMapsText.visibility = View.VISIBLE
            return
        }

    }

    /**
     * Ensure the list is always updated when the saved list is opened so all saved maps display.
     */
    override fun onResume() {
        getSavedMapsFromDB()
        populateSavedView()
        super.onResume()
    }

    /**
     * Get the URIs stored in the database for the selected map and rebuild the ImageData to make
     * the maps again from the stored data.
     */
    private fun getSavedMapsFromDB(){
        val savedMapNames = databaseHelper.getSavedMaps()


        savedMapNames.forEach { key ->
            val savedMapUris = databaseHelper.getSavedMapUris(key)
            savedMaps[key] = savedMapUris
        }

    }

    /**
     * Get all the map names saved in the database and add to a simple list adapter. When the user
     * long clicks an item in the list, bring up a dialog asking for confirmation that they wish
     * to delete the map.
     */
    private fun populateSavedView(){

        savedMaps.forEach {

            try{

                val savedMapAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, ArrayList(savedMaps.keys))
                savedMapListView.adapter = savedMapAdapter

                savedMapListView.onItemClickListener = AdapterView.OnItemClickListener {
                    _, _, i, _ ->

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

                savedMapListView.setOnItemLongClickListener{ _, _, i, _ ->

                    val selectedSavedMap = savedMapListView.getItemAtPosition(i) as String

                    // Confirmation dialog to ask if user definitely wants to delete the map
                    val deleteAlert = AlertDialog.Builder(this)

                    with(deleteAlert){
                        deleteAlert.setTitle("Are you sure you want to delete the map $selectedSavedMap?")

                        setPositiveButton("Delete"){
                            _, _ ->

                            // Calls to database helper to delete the map
                            databaseHelper.deleteMap(selectedSavedMap)
                            // Update the list view
                            savedMapAdapter.remove(savedMapAdapter.getItem(i))
                            savedMapAdapter.notifyDataSetChanged()

                        }

                        setNegativeButton("Cancel"){
                            dialog, _ ->
                            dialog.dismiss()
                        }
                    }

                    deleteAlert.create()
                    deleteAlert.show()

                    return@setOnItemLongClickListener true
                }

            }catch (ex: Exception){
                // This should not be reached, but just in case handle exceptions
                Log.e("Error Loading Maps", "Saved maps were not correctly loaded.")
            }

        }

    }
}
