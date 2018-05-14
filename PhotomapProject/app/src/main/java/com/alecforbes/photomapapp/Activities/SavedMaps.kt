package com.alecforbes.photomapapp.Activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.alecforbes.photomapapp.R

class SavedMaps : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_maps)

        title = "Your Saved Photomaps"

        getSavedMapsFromDB()

        populateSavedView()
    }

    private fun getSavedMapsFromDB(){

    }

    private fun populateSavedView(){

    }


}
