# About Photomaps
##### Author: Alec Forbes-Nicholson, Monash Student ID 25991590

The following about page lists the used libraries from third parties, Google and base development
libraries for Android used in this application. Any tutorials followed
for code implementation are referenced in the class header comments (such as StackOverflow etc.) 
where that code was implemented.

Known issues with the application are also listed at the bottom of this page.

# Third Party Libraries and Licenses:

## Development Libraries: 

* Kotlin Standard Library 1.2.41, Apache 2.0 https://github.com/JetBrains/kotlin/tree/master/license
   * Provides Kotlin extension functions to Java code. Used in all class written by myself.
* Anko Kotlin Support Library, Apache 2.0 https://github.com/Kotlin/anko/blob/master/LICENSE
   * Used to provide some additional ease of use and helper functions to Kotlin for Android.
   Used mainly for defining ASync tasks with doAsync.

## Third Party Libraries:

* OneMoreFABMenu by DeKoServidoni, Apache 2.0 https://github.com/DeKoServidoni/OMFM
   * The FAB menu used for user interaction with a custom photomap is an instance of OneMoreFABMenu.
* JSoup used to collect information on place images from Wikipedia, MIT License https://jsoup.org/license
   * Wikipedia first paragraph descriptions are retrieved using JSoup.

## Google Libraries (All Apache 2.0):

* Standard Google Android libraries (specific layouts, views etc.)
* Firebase Authentication
* Firebase Storage
   * Google Map Utils 0.5
   * Followed the Map Utils MultiDrawable Clustering Example and used MultiDrawable Java class, under Apache 2.0
     https://github.com/googlemaps/android-maps-utils/tree/master/library/src/com/google/maps/android/clustering
* Google Play Services 3.1.0
   * Necessary to run map services in the application. Note: Emulators need to have Google Play installed to run services.

## Image Attribution:
* The majority images of used in this application images were obtained from Pexels, a free stock image website. https://www.pexels.com/
* This includes images used on the main app menu screens and for the application icon.
* Other images (For example images used for place markers) were obtained through Google.

## Known Issues:
There are a few minor issues with the application, they are as follows:

* Android security sandboxing does not allow for apps to access 'Content URIs' from other applications without explicitly 
being given permission through a user intent. As URIs are used to get image selections from users, this meant that saving 
a URI to SQLite then accessing it later (without user interaction through the app) to load an image from a saved map 
is a security violation. 
To get around this, images are copied to the /data directory of the application and instead they are loaded. 
However this makes it possible to then add those duplicates to a map, which puts two copies of an image on
the same map despite this not being otherwise possible. I have attempted to stop this by using some workaround Regex code which
looks for save file names with the same IDs, which should stop this for the most part, though I can't guarantee that it 
will never happen.
* Sometimes when updating a saved map by adding new images, then going back to the saved maps list and reopening the map,
the map will not show the new images. If you go back to the main menu, then reopen the map they should appear however. 
I think I have fixed this but it may still happen occasionally.
* Occasionally when clicking an image in a place map, a view for an individual image will appear
but will be blank and contain no information. This only seems to happen if multiple markers are clicked in rapid succession at map load.
* Some image applications do not allow for the selection of multiple images at once (despite the ALLOW_MULTIPLE flag being set 
in the intent). You can still select a single image at a time however. I recommend using something like Google Photos which allows multiple.
