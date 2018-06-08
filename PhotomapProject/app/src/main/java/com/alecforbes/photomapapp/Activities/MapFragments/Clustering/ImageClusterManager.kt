package com.alecforbes.photomapapp.Activities.MapFragments.Clustering

import android.content.Context
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.clustering.ClusterManager

/**
 * Created by Alec on 5/27/2018.
 * The cluster manager defines the behaviour and listening classes for the image clustering.
 *
 * Based on the Gooogle MapUtils clustering example:
 * https://github.com/googlemaps/android-maps-utils/
 */

class ImageClusterManager(context: Context,
                          map: GoogleMap,
                          private var cameraIdleListener: GoogleMap.OnCameraIdleListener?):
        ClusterManager<ImageClusterItem>(context, map){

    /**
     * Handle when the user clicks on a marker in a cluster.
     */
    override fun onMarkerClick(marker: Marker?): Boolean {
        marker!!.showInfoWindow()
        return super.onMarkerClick(marker)
    }

    /**
     * Set the camera listener to know how to move the clusters in relation to user navigation in
     * the map.
     */
    override fun onCameraIdle() {
        super.onCameraIdle()
        cameraIdleListener?.onCameraIdle()
    }

}