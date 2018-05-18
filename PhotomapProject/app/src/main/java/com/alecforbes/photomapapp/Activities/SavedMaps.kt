package com.alecforbes.photomapapp.Activities

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.widget.CardView
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.alecforbes.photomapapp.Controllers.Database.DatabaseHelper
import com.alecforbes.photomapapp.Model.ImageData
import com.alecforbes.photomapapp.R
import kotlinx.android.synthetic.main.activity_saved_maps.*
import java.io.InputStream

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

        // Duplicate the card layout and populate with the retrieved information

        savedMaps.forEach {


            val savedName = TextView(this)
            val savedPreview = ImageView(this)
            val viewButton = Button(this)

            savedName.text = it.key
            viewButton.text = getString(R.string.view)

            savedCardLinearLayout.addView(savedName)
            savedCardLinearLayout.addView(savedPreview)
            savedCardLinearLayout.addView(viewButton)

            // Now create a bitmap from the first uri to act as the image preview for the saved map

            // Only system apps can use MANAGE_DOCUMENTS permissions, so get the byte stream
            val previewUriStream = contentResolver.openInputStream(it.value[0])

            val byteData = previewUriStream.readBytes()

            previewUriStream.close()

            val previewBitmap = BitmapFactory.decodeByteArray(byteData, 0, byteData.size)

            savedPreview.setImageBitmap(previewBitmap)


        }




    }


}
