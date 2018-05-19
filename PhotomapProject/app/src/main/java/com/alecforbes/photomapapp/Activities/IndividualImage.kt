package com.alecforbes.photomapapp.Activities

import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import com.alecforbes.photomapapp.R
import kotlinx.android.synthetic.main.individual_image_view.*
import java.io.File

class IndividualImage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.individual_image_view)


        //val imageData = intent.getParcelableExtra<ImageData>("SelectedImage")
        //val compressedBitmap = intent.getByteArrayExtra("CompressedImageBitmap")
        //val bitmap = BitmapFactory.decodeByteArray(compressedBitmap, 0, compressedBitmap.size)
        val file = intent.getParcelableExtra<Parcelable>("ImageFile")
        val dateTimeTaken = intent.getParcelableExtra<Parcelable>("DateTimeTaken")
        val latLong = intent.getParcelableExtra<Parcelable>("LatLong")

       // indvImageView.setImageBitmap(bitmap)
        imageAddressValue.text = "REVERSE GEOCDOE"

        if (dateTimeTaken != null) {
            imageTimeTakenValue.text = dateTimeTaken.toString()
        }else{
            imageTimeTakenValue.text = "Unknown"
        }
    }
}


