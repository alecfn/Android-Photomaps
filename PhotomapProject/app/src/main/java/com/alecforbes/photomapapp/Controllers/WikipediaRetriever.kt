package com.alecforbes.photomapapp.Controllers

import android.util.Log
import org.jsoup.Jsoup
import java.io.IOException

/**
 * Created by Alec on 6/3/2018.
 * This class will retrieve the first paragraph from Wikipedia for a selected Landmark. This is used
 * to populate the information for each individual image.
 *
 * Must be an Async task as network connections cannot be run on the main thread, using Anko.
 *
 * Based on the follow example:
 * https://stackoverflow.com/questions/43021074/getting-the-first-paragraph-of-wikipedias-article-using-jsoup
 */

class WikipediaRetriever {

    /**
     * Just get the first paragraph from Wikipedia for the supplied link, this is used to describe
     * images on a place map. Note, this paragraph may not always be the description, though in
     * most cases should be.
     */
    fun getFirstParagraphFromWikipedia(wikiUrl: String): String? {

        var firstParaText: String?

            firstParaText = try {
                val wikiDocument = Jsoup.connect(wikiUrl).get()
                val paragraphs = wikiDocument.select("p")
                val firstPara = paragraphs.first()
                firstPara.text()

            } catch (ioEx: IOException) {
                // Just log a failure, the app can continue regardless.
                Log.e("Wiki Error", "Failed to get Wikipedia data for $wikiUrl")
                "Failed to retrieve Wikipedia entry for address"
            }
            return firstParaText
    }
}