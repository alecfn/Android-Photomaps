package com.alecforbes.photomapapp.Activities.MapFragments.Clustering

import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.google.maps.android.ui.IconGenerator

/**
 * Created by Alec on 5/27/2018.
 */

class ImageClusterItem(private val position: LatLng,
                       private val title: String,
                       private val snippet: String): ClusterItem{

    private lateinit var bitmapDesc: BitmapDescriptor

    override fun getTitle(): String {
        return title
    }

    override fun getPosition(): LatLng {
        return position
    }

    override fun getSnippet(): String {
        return snippet
    }

    fun setBitmapDesc(bitmapDesc: BitmapDescriptor){
        this.bitmapDesc = bitmapDesc
    }


}
