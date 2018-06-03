package com.alecforbes.photomapapp.Model

/**
 * Created by Alec on 4/28/2018.
 * This class is hashmap of each content link of a file stored in Firebase. The key is the location
 * that will be clicked in the places list.
 *
 * Adding a new image set is simple, after being added to fire base, define the links here, then
 * pass the name of the hashmap key to access the links to the place map activity.
 */

class PlacesLinksHashmap{

    private val placesLinksHashmap = HashMap<String, List<List<String>>>()

    init {
        val sydneyLinks =
                listOf(listOf("PlacesTestData/Sydney/harbourbridge.jpg", "https://en.wikipedia.org/wiki/Sydney_Harbour_Bridge"),
                listOf("PlacesTestData/Sydney/operahouse.jpg", "https://en.wikipedia.org/wiki/Sydney_Opera_House"),
                        listOf("PlacesTestData/Sydney/parliament.jpeg", "https://en.wikipedia.org/wiki/Parliament_House,_Sydney"))

        val newYorkLinks = listOf(listOf("PlacesTestData/NewYork/brooklynbridge.jpg", "https://en.wikipedia.org/wiki/Brooklyn_Bridge"),
                listOf("PlacesTestData/NewYork/empirestate.jpg", "https://en.wikipedia.org/wiki/Empire_State_Building"),
                listOf("PlacesTestData/NewYork/statueofliberty.jpg", "https://en.wikipedia.org/wiki/Statue_of_Liberty"))

        val londonLinks = listOf(listOf("PlacesTestData/London/londoneye.jpg", "https://en.wikipedia.org/wiki/London_Eye"),
                listOf("PlacesTestData/London/parliament.jpg", "https://en.wikipedia.org/wiki/Palace_of_Westminster"),
                listOf("PlacesTestData/London/toweroflondon.jpg", "https://en.wikipedia.org/wiki/Tower_of_London"))

        val worldLinks = listOf(listOf("PlacesTestData/World/brandenburggate.jpg", "https://en.wikipedia.org/wiki/Brandenburg_Gate"),
                listOf("PlacesTestData/World/eiffeltower.jpg", "https://en.wikipedia.org/wiki/Eiffel_Tower"),
                listOf("PlacesTestData/World/goldengate.jpg", "https://en.wikipedia.org/wiki/Golden_Gate_Bridge"),
                listOf("PlacesTestData/World/hollywoodsign.jpg", "https://en.wikipedia.org/wiki/Hollywood_Sign"),
                listOf("PlacesTestData/World/liberty.jpg", "https://en.wikipedia.org/wiki/Statue_of_Liberty"),
                listOf("PlacesTestData/World/operahouse.jpg", "https://en.wikipedia.org/wiki/Sydney_Opera_House"),
                listOf("PlacesTestData/World/pyramids.jpg", "https://en.wikipedia.org/wiki/Giza_pyramid_complex"),
                listOf("PlacesTestData/World/rushmore.jpg", "https://en.wikipedia.org/wiki/Mount_Rushmore"),
                listOf("PlacesTestData/World/touhontower.jpg", "https://en.wikipedia.org/wiki/Tokyo_Tower"))

        val ausBigThingsLinks = listOf(listOf("PlacesTestData/AusBigThings/bigbanana.jpg", "https://en.wikipedia.org/wiki/Big_Banana"),
                listOf("PlacesTestData/AusBigThings/bigkoala.jpg", "https://en.wikipedia.org/wiki/Australia%27s_big_things"),
                listOf("PlacesTestData/AusBigThings/biglobster.jpg", "https://en.wikipedia.org/wiki/Big_Lobster"),
                listOf("PlacesTestData/AusBigThings/bigpineapple.jpg", "https://en.wikipedia.org/wiki/Big_Pineapple"),
                listOf("PlacesTestData/AusBigThings/bigram.jpg", "https://en.wikipedia.org/wiki/Big_Merino"),
                listOf("PlacesTestData/AusBigThings/bigstrawberry.jpg", "https://en.wikipedia.org/wiki/Australia%27s_big_things"))

        placesLinksHashmap["Sydney"] = sydneyLinks
        placesLinksHashmap["New York"] = newYorkLinks
        placesLinksHashmap["London"] = londonLinks
        placesLinksHashmap["World Landmarks"] = worldLinks
        placesLinksHashmap["Australia's Big Things"] = ausBigThingsLinks

    }

    fun getPlaceLinks(key: String): List<List<String>>? {
        return placesLinksHashmap[key]
    }
}