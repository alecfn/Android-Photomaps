package com.alecforbes.photomapapp.Activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.alecforbes.photomapapp.R
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_places_list.*


class PlacesList : AppCompatActivity() {

    val availablePlaces = ArrayList<String>()
    // TODO probably move to own class

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_places_list)
        title = "Places"
        //actionBar.setDisplayHomeAsUpEnabled(true)

        //placesListView.setOnClickListener { parent, vi }

        setUpPlacesListView()

    }

    /**
     * Set up the options in the list on this screen to be of the available place maps hosted on
     * the firebase server.
     */
    private fun setUpPlacesListView(){
        // TODO this is just manually added, could be a better way? Iterate through the sample files on firebase?

        availablePlaces.add("Sydney")

        // TODO do want to add image thumnail previews so will probably have to be a custom item later
        val placeListAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, availablePlaces)
        placesListView.adapter = placeListAdapter

        placesListView.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->

            val selectedLoc = placesListView.getItemAtPosition(i) as String
            val placePhotoMapIntent = Intent(this, PlacePhotomap::class.java)
            placePhotoMapIntent.putExtra("SelectedLocation", selectedLoc)
            startActivity(placePhotoMapIntent)
        }


    }

}
