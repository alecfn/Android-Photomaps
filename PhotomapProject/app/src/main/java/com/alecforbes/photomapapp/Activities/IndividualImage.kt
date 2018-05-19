package com.alecforbes.photomapapp.Activities

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Parcelable
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import com.alecforbes.photomapapp.Model.ImageData
import com.alecforbes.photomapapp.R
import kotlinx.android.synthetic.main.individual_image_view.*

/**
 * Created by Alec on 5/19/2018.
 */

class IndividualImage : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.individual_image_view)


        //val imageData = intent.getParcelableExtra<ImageData>("SelectedImage")
        val compressedBitmap = intent.getByteArrayExtra("CompressedImageBitmap")
        val bitmap = BitmapFactory.decodeByteArray(compressedBitmap, 0, compressedBitmap.size)
        val dateTimeTaken = intent.getParcelableExtra<Parcelable>("DateTimeTaken")
        val latLong = intent.getParcelableExtra<Parcelable>("LatLong")

        indvImageView.setImageBitmap(bitmap)
        imageAddressValue.text = "REVERSE GEOCDOE"
        imageTimeTakenValue.text = dateTimeTaken.toString()
    }
}