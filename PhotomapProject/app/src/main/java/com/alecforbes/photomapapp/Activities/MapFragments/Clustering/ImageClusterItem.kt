package com.alecforbes.photomapapp.Activities.MapFragments.Clustering

import android.graphics.Bitmap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

/**
 * Created by Alec on 5/27/2018.
 */

class ImageClusterItem(private val position: LatLng,
                       private val title: String,
                       private val snippet: String): ClusterItem{

    private lateinit var bitmapDesc: BitmapDescriptor
    private lateinit var thumbnailBitmap: Bitmap

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

    fun setThumbnailBitmap(thumbnailBitmap: Bitmap){
        this.thumbnailBitmap = thumbnailBitmap
    }

    // In this class we need a getter due to the method JVM signature not liking public vals
    fun getBitmapDesc(): BitmapDescriptor {
        return this.bitmapDesc
    }

    fun getThumbnailBitmap(): Bitmap{
        return this.thumbnailBitmap
    }


}
