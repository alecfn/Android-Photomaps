package com.alecforbes.myapplication

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class PhotoSelection : AppCompatActivity() {

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
                startActivityForResult(customSelectIntent, 1)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Received the selection from the gallery
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode){}
    }
}
