package com.alecforbes.photomapapp.Activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.alecforbes.photomapapp.Controllers.Database.DatabaseHelper
import com.alecforbes.photomapapp.R

class SavedMaps : AppCompatActivity() {

    private var databaseHelper = DatabaseHelper(this)

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
        val savedMaps = databaseHelper.getSavedMaps()
        val test = databaseHelper.getSavedMapUris(savedMaps[0])
        print("")
    }

    /**
     * Get all the map names saved in the database and the first image to use as a preview
     */
    private fun populateSavedView(){

    }


}
