package com.alecforbes.photomapapp.Controllers

import android.content.ContentValues.TAG
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import java.io.IOException
import java.util.*

/**
 * Created by Alec on 4/26/2018.
 * This class performs geocoding on supplied coordinates and returns the results. It is supplied
 * a latitude and longitude coordinate set which it can then get the address for.
 *
 * Based on Google example https://developer.android.com/reference/android/location/Geocoder
 * and following Stack Overflow:
 * https://stackoverflow.com/questions/15711499/get-latitude-and-longitude-with-geocoder-and-android-google-maps-api-v2
 */
class ImageGeocoder(private val lat: Double, private val long: Double, private val context: Context) {

    private val MAX_RESULTS = 1 // We only need one result

    fun getAddressFromLocation(): String {

        val geocoder = Geocoder(context, Locale.getDefault())

        var addresses: List<Address> = emptyList()
        var addressString = ""

        var error = ""
        try {

            addresses = geocoder.getFromLocation(lat, long, MAX_RESULTS)
        } catch (ioEx: IOException) {
            error = "ImageGeocoder service unavailable"
            Log.e(TAG, error, ioEx)
        } catch (illegalArgEx: IllegalArgumentException) {
            error = "Invalid Lat Long Used"
            Log.e(TAG, "$error when geocoding values.")

        }

        // Handle no addresses returned
        if (addresses.isEmpty()) {

            if (error.isEmpty()) {
                error = "No addresses found"
                Log.e(TAG, error)
                return "Unknown"
            }
        } else {
            // Address was found
            val fetchedAddress = addresses[0]
            addressString = fetchedAddress.getAddressLine(0).toString()
        }

    return addressString
    }
}

