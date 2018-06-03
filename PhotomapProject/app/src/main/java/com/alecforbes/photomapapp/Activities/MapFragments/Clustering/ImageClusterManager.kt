package com.alecforbes.photomapapp.Activities.MapFragments.Clustering

import android.content.Context
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.clustering.ClusterManager

/**
 * Created by Alec on 5/27/2018.
 */

class ImageClusterManager(context: Context,
                          map: GoogleMap,
                          private var cameraIdleListener: GoogleMap.OnCameraIdleListener?):
        ClusterManager<ImageClusterItem>(context, map){

    var isPlaceMap: Boolean = false // The type of map, place, custom etc. to know how to perform logic

    override fun onMarkerClick(marker: Marker?): Boolean {
        marker!!.showInfoWindow()
        // todo When the marker is clicked once, make the image larger

        // When a place map, information should be displayed when the user taps a marker
        if(isPlaceMap){

           // indvPlaceViewConstraint


        }

        return super.onMarkerClick(marker)
    }

    override fun onCameraIdle() {
        super.onCameraIdle()
        cameraIdleListener?.onCameraIdle()
    }

    fun setRenderer(){

    }

    private fun resizeMarker(marker: Marker?){

    }

   // override fun getMarkerCollection(): MarkerManager.Collection? {

       // return
   // }

}