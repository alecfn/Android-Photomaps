package com.alecforbes.photomapapp.Activities.MapFragments.Clustering

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.support.annotation.Dimension
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
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
 *
 * Based on the Gooogle MapUtils clustering example:
 * https://github.com/googlemaps/android-maps-utils/
 */

class ImageClusterRenderer(private val context: Context?, map: GoogleMap?,
                           clusterManager: ClusterManager<ImageClusterItem>?,
                           private var dimension: Int) :
        DefaultClusterRenderer<ImageClusterItem>(context, map, clusterManager) {

    private val iconGenerator = IconGenerator(context)
    private val clusterIconGenerator = IconGenerator(context)
    private var photomapImage: ImageView
    private var clusterImageView: ImageView
    private var clusterNumberText: TextView

    init {

        // Definitions for an image cluster
        val multiImage = View.inflate(context, R.layout.multi_image, null)
        clusterIconGenerator.setContentView(multiImage)
        clusterImageView = multiImage.findViewById(R.id.clusterImage)
        clusterImageView.maxHeight = dimension
        clusterImageView.maxWidth = dimension
        clusterNumberText = multiImage.findViewById(R.id.clusterNumberText)

        // Definitions for a single image
        photomapImage = ImageView(context)
        photomapImage.layoutParams = ViewGroup.LayoutParams(dimension, dimension)
        val padding = 10 // Pixel padding
        photomapImage.setPadding(padding, padding, padding, padding)
        iconGenerator.setContentView(photomapImage)
    }


    override fun onBeforeClusterRendered(cluster: Cluster<ImageClusterItem>?, markerOptions: MarkerOptions?) {
        super.onBeforeClusterRendered(cluster, markerOptions)


        // fixme this would be better run on its own thread
        // 2 or more images should cluster into a custom display
        val imageClusterDrawables = ArrayList<Drawable>(Math.min(2, cluster!!.size))
        for(clusterImageItem in cluster.items){

            if (imageClusterDrawables.size == 2){
                break
            }

            val imageDrawable = BitmapDrawable(context!!.resources, clusterImageItem.getThumbnailBitmap())
            imageDrawable.setBounds(0, 0, dimension, dimension)
            imageClusterDrawables.add(imageDrawable)

        }
        // Uses MultiDrawable class from Google Map Utils
        val multiImageDrawable = MultiDrawable(imageClusterDrawables)
        multiImageDrawable.setBounds(0, 0, dimension, dimension)

        clusterImageView.setImageDrawable(multiImageDrawable)
        // Set options for text view showing number of images in cluster
        clusterNumberText.text = cluster.size.toString()
        clusterNumberText.textSize = 30f
        clusterNumberText.setTypeface(clusterNumberText.typeface, Typeface.BOLD)
        clusterNumberText.setTextColor(Color.parseColor("#ffffff"))  //
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

    override fun setOnClusterItemClickListener(listener: ClusterManager.OnClusterItemClickListener<ImageClusterItem>?) {
        super.setOnClusterItemClickListener(listener)

       // Toast.makeText(context, "blub", Toast.LENGTH_SHORT).show()
    }

}