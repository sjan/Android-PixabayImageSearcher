# Android-PixabayImageSearcher
Android image searcher app powered by Pixabay

The PixabayImageSearcher queries the [pixabay](https://pixabay.com/api/docs/) api for images from a user's query. Implementation handles screen orientation change and infinte scrolling. Scroll is triggered when the user scrolls to the buttom of the list. 

3rd libraries that this app uses:
* [Butterknife](http://jakewharton.github.io/butterknife/) : Handles binding views to activities.
* [Picasso](http://square.github.io/picasso/) : Picasso handles downloading images and populating them into the image view.
* [Retrofit](http://square.github.io/retrofit/) : Retrofit handles the api call to Pixabay. Originally i was planning on using async task but Retrofit abstracts the call into their api.

