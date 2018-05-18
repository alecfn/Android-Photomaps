package com.alecforbes.photomapapp.Activities

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.alecforbes.photomapapp.Controllers.Database.DatabaseHelper
import com.alecforbes.photomapapp.R

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
            savedMaps.put(it,savedMapUris)
            print("")
        }
        //val test = databaseHelper.getSavedMapUris(savedMaps[0])
        //print("")
    }

    /**
     * Get all the map names saved in the database and the first image to use as a preview
     */
    private fun populateSavedView(){

    }


}
