package com.alecforbes.photomapapp.Activities.MapFragments.Clustering

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.alecforbes.photomapapp.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
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

    private val context = context
    private val iconGenerator = IconGenerator(context)
    private val clusterIconGenerator = IconGenerator(context)
    private lateinit var photomapBitmap: BitmapDescriptor
    private lateinit var photomapImage: ImageView
    private lateinit var clusterImageView: ImageView
    private var dimension = 0

    init {
        val multiImage = View.inflate(context, R.layout.multi_image, null)
        iconGenerator.setContentView(multiImage)
        clusterImageView = multiImage.findViewById(R.id.clusterImage)

        photomapImage = ImageView(context)
        dimension = 300 //todo make this thumbnail size
        clusterImageView.layoutParams = ViewGroup.LayoutParams(dimension, dimension)
        val padding = 10 // 10 Pixel padding
        photomapImage.setPadding(padding, padding, padding, padding)
        iconGenerator.setContentView(photomapImage)
    }


    override fun onBeforeClusterRendered(cluster: Cluster<ImageClusterItem>?, markerOptions: MarkerOptions?) {
        super.onBeforeClusterRendered(cluster, markerOptions)

        val imageClusterDrawables = ArrayList<Drawable>(Math.min(3, cluster!!.size))
        for(clusterImageItem in cluster.items){

            if (imageClusterDrawables.size == 3){
                break
            }

            val imageDrawable = BitmapDrawable(context!!.resources, clusterImageItem.getThumbnailBitmap())
            imageDrawable.setBounds(0, 0, dimension, dimension)
            imageClusterDrawables.add(imageDrawable)

        }
        // Uses MultiDrawable class from Google Map Utils
        val multiImageDrawable = MultiDrawable(imageClusterDrawables)

        multiImageDrawable.setBounds(0, 0, dimension, dimension)
        val multiIcon = clusterIconGenerator.makeIcon(cluster.size.toString())
        markerOptions!!.icon(BitmapDescriptorFactory.fromBitmap(multiIcon))


    }

    override fun shouldRenderAsCluster(cluster: Cluster<ImageClusterItem>?): Boolean {
        return cluster!!.size > 1
    }

    override fun onBeforeClusterItemRendered(image: ImageClusterItem, markerOptions: MarkerOptions?) {
        super.onBeforeClusterItemRendered(image, markerOptions)

        // Set the icon to the image Bitmap thumbnail
        photomapImage.setImageBitmap(image.getThumbnailBitmap())
        val icon = iconGenerator.makeIcon()
        markerOptions!!.icon(BitmapDescriptorFactory.fromBitmap(icon))

    }

}