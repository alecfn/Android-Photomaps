package com.alecforbes.photomaps.Model

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Alec on 4/26/2018.
 */

class ImageData constructor(latitude: Long, longitude: Long, imagePath: String) : Parcelable {

    // Set up image variables, these should be immutable so declare as vals
    val latitude = latitude
    val longitude = longitude
    val imagePath = imagePath

    constructor(parcel: Parcel) : this(
            TODO("latitude"),
            TODO("longitude"),
            TODO("imagePath")) {
    }

    // TODO any more exif
    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ImageData> {
        override fun createFromParcel(parcel: Parcel): ImageData {
            return ImageData(parcel)
        }

        override fun newArray(size: Int): Array<ImageData?> {
            return arrayOfNulls(size)
        }
    }


}
