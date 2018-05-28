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

    override fun onMarkerClick(marker: Marker?): Boolean {
        marker!!.showInfoWindow()
        return super.onMarkerClick(marker)
    }

    override fun onCameraIdle() {
        super.onCameraIdle()
        cameraIdleListener?.onCameraIdle()
    }

    fun setRenderer(){

    }

   // override fun getMarkerCollection(): MarkerManager.Collection? {

       // return
   // }

}