package com.alecforbes.photomaps.Activities

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Parcelable
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.alecforbes.photomaps.Controllers.ImageController
import com.alecforbes.photomaps.Model.ImageData
import com.alecforbes.photomaps.R
import java.io.File
import java.io.InputStream

class CustomPhotomap : AppCompatActivity() {

    var selectedImages = ArrayList<ImageData>()
   // var imageController = ImageController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val imagesIntent = intent
        getImageFilepaths(imagesIntent)

        setContentView(R.layout.acivity_photomap)

        var customMapFragment : CustomPhotomapFragment? = supportFragmentManager.findFragmentById(R.id.photomapFragment) as CustomPhotomapFragment?

        customMapFragment?.getMapAsync(customMapFragment)

    }

    private fun createImageData(imageFiles: ArrayList<File>){

        imageFiles.forEach {
           // val selectedImage = imageController
            var imageFile = it
            // FIXME k this method doesnt work at all, look into the whole inputstream thing
            var selectedImage = ImageData(Environment.getExternalStorageDirectory().absolutePath + imageFile.absoluteFile)
            selectedImages.add(selectedImage)
        }
        print("")

    }


    private fun getImageFilepaths(imagesIntent: Intent){

        if (imagesIntent != null) {

            val imageData = imagesIntent.getParcelableExtra<Intent>("imageData")

            // FIXME exception when only selecting one?
            val numberImages = imageData.clipData!!.itemCount
            val imageFiles= ArrayList<File>()

            // Get all of the image ClipData objects to add to an array and send in an intent
            for (i in 0..(numberImages - 1)){
                val uri = imageData.clipData.getItemAt(i).uri.path
                val imageFile = File(uri)


                //val uriStream = contentResolver.openInputStream(uri)
                //val byteArr
                //val bytes = uriStream.available()
                //uriStream.read(bytes,0, bytes)
               // uriStream.read(bytes, 0, bytes.length)

                imageFiles.add(imageFile)
                print("")
            }

            createImageData(imageFiles)
        }

    }
//
//    private fun getPathFromContentURI(context: Context, uri: Uri){
//
//        try {
//            var proj = {MediaStore.Images.Media.DATA}
//            var cursor = context.contentResolver.query(uri, proj, null, null, null)
//            var colIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
//            cursor.moveToFirst()
//            return cursor.getString(colIndex)
//        } finally {
//
//        }
//
//
//    }


}
