package com.alecforbes.photomapapp.Controllers

import android.app.IntentService
import android.content.ContentValues.TAG
import android.content.Intent
import android.location.Address
import android.os.Bundle
import android.os.ResultReceiver
import android.util.Log
import java.io.IOException
import java.util.*

/**
 * Created by Alec on 4/26/2018.
 * This class performs geocoding on supplied coordinates and returns the results
 */
class Geocoder(geocoder: Geocoder, default: Locale) : IntentService() {

    private var receiver: ResultReceiver? = null
    private lateinit var resultReceiver: AddressResultReceiver

    object Constants {
        const val SUCCESS = 0
        const val FAILURE = 1
        const val PACKAGE_NAME = "com.alecforbes.photomapapp.Controllers"
        const val RECEIVER = "com.alecforbes.photomapapp.Activities.Photomap.CustomPhotomap"
        const val RESULT_DATA_KEY = "${PACKAGE_NAME}.RESULT_DATA_KEY"
        const val LOCATION_DATA_EXTRA = "${PACKAGE_NAME}.LOCATION_DATA_EXTRA"
    }

    override fun onHandleIntent(intent: Intent?) {
        intent ?: return
        val geocoder = Geocoder(this, Locale.getDefault())

        var addresses: List<Address> = emptyList()

        val location = intent.getParcelableExtra(Constants.LOCATION_DATA_EXTRA)

        var error = ""
        try {
            addresses = geocoder.getFromLocation(location.latitude, location.longitude)
        } catch (ioEx: IOException){
            error = "Geocoder service unavailable"
            Log.e(TAG, error, ioEx)
        } catch (illegalArgEx: IllegalArgumentException){
            error = "Invalid Lat Long Used"
            Log.e(TAG, "$error when geocoding values.")

        }

        // Handle no addresses returned
        if (addresses.isEmpty()){
            if (error.isEmpty()){
                error = "No addresses found"
                Log.e(TAG, error)
            }
            deliverResultsToReceiver(Constants.FAILURE, error)
        } else {
            val address = addresses[0]

            val addressFragments = with(address){
                (0.maxAddressLineIndex).map { getAddressLine(it) }
            }
            Log.i(TAG, "Addresses found")
            deliverResultsToReceiver(Constants.SUCCESS){
                addressFragments.joinToString(separator = "\n")
            }
        }


    }

    private fun deliverResultsToReceiver(resultCode: Int, message: String){
        val bundle = Bundle().apply { putString(Constants.RESULT_DATA_KEY, message) }
        receiver?.send(resultCode, bundle)

    }

}