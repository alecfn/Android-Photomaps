package com.alecforbes.photomapapp.Activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ArrayAdapter
import com.alecforbes.photomapapp.R
import kotlinx.android.synthetic.main.activity_places_list.*


class PlacesList : AppCompatActivity() {

    val availablePlaces = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_places_list)
        title = "Places"
        //actionBar.setDisplayHomeAsUpEnabled(true)

        //placesListView.setOnClickListener { parent, vi }

        setUpPlacesItems()
    }

    /**
     * Set up the options in the list on this screen to be of the available place maps hosted on
     * the firebase server.
     */
    private fun setUpPlacesItems(){
        // TODO this is just manually added, could be a better way? Iterate through the sample files on firebase?

        availablePlaces.add("Sydney")

        // TODO do want to add image thumnail previews so will probably have to be a custom item later
        val placeListAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, availablePlaces)
        placesListView.adapter = placeListAdapter


    }
}
