package com.alecforbes.photomapapp.Model

/**
 * Created by Alec on 4/28/2018.
 * This class is hashmap of each content link of a file stored in Firebase. The key is the location
 * that will be clicked in the places list.
 */

class PlacesLinksHashmap{

    private val placesLinksHashmap = HashMap<String, List<String>>()

    init {
        val sydneyLinks = listOf("PlacesTestData/Sydney/harbourbridge.jpg",
                "PlacesTestData/Sydney/operahouse.jpg",
                "PlacesTestData/Sydney/parliament.jpeg")

        val newYorkLinks = listOf("PlacesTestData/NewYork/brooklynbridge.jpg",
                "PlacesTestData/NewYork/empirestate.jpg",
                "PlacesTestData/NewYork/statueofliberty.jpg")

        val londonLinks = listOf("PlacesTestData/London/londoneye.jpg",
                "PlacesTestData/London/parliament.jpg",
                "PlacesTestData/London/toweroflondon.jpg")

        placesLinksHashmap["Sydney"] = sydneyLinks
        placesLinksHashmap["New York"] = newYorkLinks
        placesLinksHashmap["London"] = londonLinks
    }

    fun getPlaceLinks(key: String): List<String>? {
        return placesLinksHashmap[key]
    }
}