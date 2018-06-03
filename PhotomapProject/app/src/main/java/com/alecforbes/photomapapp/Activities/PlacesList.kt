package com.alecforbes.photomapapp.Activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.alecforbes.photomapapp.Activities.Photomaps.PlacePhotomap
import com.alecforbes.photomapapp.R
import kotlinx.android.synthetic.main.activity_places_list.*


class PlacesList : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_places_list)
        title = "World Places"
        setUpPlacesButtons()

    }

    /**
     * Set up the buttons in the card view (Image buttons) to link to different place maps.
     */
    private fun setUpPlacesButtons(){

        val placePhotoMapIntent = Intent(this, PlacePhotomap::class.java)

        // Set all the listeners for each image button
        var selectedLoc: String?
        newYorkButton.setOnClickListener {
            selectedLoc = "New York"
            placePhotoMapIntent.putExtra("SelectedLocation", selectedLoc)
            startActivity(placePhotoMapIntent)
        }

        londonButton.setOnClickListener{
            selectedLoc = "London"
            placePhotoMapIntent.putExtra("SelectedLocation", selectedLoc)
            startActivity(placePhotoMapIntent)
        }

        sydneyButton.setOnClickListener{
            selectedLoc = "Sydney"
            placePhotoMapIntent.putExtra("SelectedLocation", selectedLoc)
            startActivity(placePhotoMapIntent)
        }

        ausBigButton.setOnClickListener{
            selectedLoc = "Australia's Big Things"
            placePhotoMapIntent.putExtra("SelectedLocation", selectedLoc)
            startActivity(placePhotoMapIntent)
        }

        worldButton.setOnClickListener{
            selectedLoc = "World Landmarks"
            placePhotoMapIntent.putExtra("SelectedLocation", selectedLoc)
            startActivity(placePhotoMapIntent)
        }


    }

}
