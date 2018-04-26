package com.alecforbes.photomaps.Activities

import android.content.ClipData
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import com.alecforbes.photomaps.R

class PhotoSelection : AppCompatActivity() {

    // TODO permission handling
    private val PICK_IMAGES = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_selection)

        setupSelectionListeners()
    }

    private fun setupSelectionListeners(){

        val selectAllButton = findViewById<Button>(R.id.selectAllButton)
        val customSelectButton = findViewById<Button>(R.id.customSelectionButton)

        customSelectButton.setOnClickListener {

            val customSelectIntent = Intent(Intent.ACTION_PICK)
            customSelectIntent.type = "image/*"
            customSelectIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            customSelectIntent.action = Intent.ACTION_GET_CONTENT

            if (customSelectIntent.resolveActivity(packageManager) != null){
                startActivityForResult(Intent.createChooser(customSelectIntent, "Select photos for photomap"), PICK_IMAGES)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Received the selection from the gallery
        super.onActivityResult(requestCode, resultCode, data)
        val customPhotomapIntent = Intent(this, CustomPhotomap::class.java)
        //var imageUris = ArrayList<ClipData.Item>()

        //var numberImages = data?.clipData!!.itemCount

        if (requestCode == PICK_IMAGES){

            // Get all of the image ClipData objects to add to an array and send in an intent
            //for (i in 0..(numberImages - 1)){
            //    var uri = data.clipData.getItemAt(i)
            //    imageUris.add(uri)
            //}

            customPhotomapIntent.putExtra("imageData", data)

            startActivity(customPhotomapIntent)
        }
    }
}