Author: Alec Forbes-Nicholson, Monash Student ID 25991590

The following about page lists the used libraries from third parties, Google and base development
libraries for Android used in this application. Any tutorials followed
for code implementation are referenced in the class header comments (such as StackOverflow etc.) 
where that code was implemented.

Third Party Libraries and Licenses:

- Development Libraries:
    - Kotlin Standard Library 1.2.41
        - Provides Kotlin extension functions to Java code. Used in all class written by myself.
    - Anko Kotlin Support Library
        - Used to provide some additional ease of use and helper functions to Kotlin for Android.
        Used mainly for defining ASync tasks with doAsync.

- Third Party Libraries:
    - OneMoreFABMenu by DeKoServidoni, Apache 2.0, https://github.com/DeKoServidoni/OMFM
        - The FAB menu used for user interaction with a custom photomap is an instance of OneMoreFABMenu.
    - JSoup used to collect information on place images from Wikipedia
        - Wikipedia first paragraph descriptions are retrieved using JSoup.

- Google Libraries:
    - Firebase Authentication
    - Firebase Storage
    - Google Map Utils 0.5
        - Followed the Map Utils MultiDrawable Clustering Example and used MultiDrawable Java class, under Apache 2.0
        https://github.com/googlemaps/android-maps-utils/tree/master/library/src/com/google/maps/android/clustering
    - Google Play Services 3.1.0
        - Necessary to run map services in the application. Note: Emulators need to have Google Play installed to run services.