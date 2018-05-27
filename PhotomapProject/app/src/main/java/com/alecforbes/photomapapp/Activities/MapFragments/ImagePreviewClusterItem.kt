package com.alecforbes.photomapapp.Activities.MapFragments

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

/**
 * Created by Alec on 5/27/2018.
 */

class ImagePreviewClusterItem(private val position: LatLng,
                              private val title: String,
                              private val snippet: String): ClusterItem{

    override fun getTitle(): String {
        return title
    }

    override fun getPosition(): LatLng {
        return position
    }

    override fun getSnippet(): String {
        return snippet
    }

}
