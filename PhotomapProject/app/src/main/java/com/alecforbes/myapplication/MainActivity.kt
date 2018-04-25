package com.alecforbes.myapplication

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupButtonListeners()
    }

    private fun setupButtonListeners(){

        val createmMapButton = findViewById<ImageButton>(R.id.createPhotomapButton)
        val placesMapButton = findViewById<ImageButton>(R.id.placesPhotomapButton)
        val savedMapButton = findViewById<ImageButton>(R.id.savedPhotomapsButton)

        createmMapButton.setOnClickListener {
            // Send the map creation intent
            val createIntent = Intent(this, PhotoSelection::class.java)
            startActivity(createIntent)

        }

        placesMapButton.setOnClickListener {

        }

        savedMapButton.setOnClickListener {

        }

    }
}
