package com.alecforbes.photomapapp.Activities.MapFragments.Clustering

import android.content.Context
import android.widget.ImageView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.google.maps.android.ui.IconGenerator

/**
 * Created by Alec on 5/27/2018.
 */

class ImageClusterRenderer(context: Context?, map: GoogleMap?,
                           clusterManager: ClusterManager<ImageClusterItem>?) :
        DefaultClusterRenderer<ImageClusterItem>(context, map, clusterManager) {

    private val iconGenerator = IconGenerator(context)
    private val clusterIcontGenerate = IconGenerator(context)
    private lateinit var photomapBitmap: BitmapDescriptor
    private lateinit var photomapImage: ImageView
    private lateinit var clusterImageView: ImageView
    private var dimension = 0


    override fun onBeforeClusterRendered(cluster: Cluster<ImageClusterItem>?, markerOptions: MarkerOptions?) {
        super.onBeforeClusterRendered(cluster, markerOptions)

        // Make the icon image the photo that was selected
        markerOptions!!.icon
       // markerOptions!!.icon(this.photomapBitmap)
    }

    override fun shouldRenderAsCluster(cluster: Cluster<ImageClusterItem>?): Boolean {
        return cluster!!.size > 1
    }

    fun setImage(photo: BitmapDescriptor){
        this.photomapBitmap = photo
    }

}