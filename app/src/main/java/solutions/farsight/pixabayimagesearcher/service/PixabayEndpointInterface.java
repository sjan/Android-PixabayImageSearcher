package solutions.farsight.pixabayimagesearcher.service;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import solutions.farsight.pixabayimagesearcher.model.ImageSearchResult;

/**
 * Created by Stephen on 2/23/2018.
 */

public interface PixabayEndpointInterface {
    // Request method and URL specified in the annotation
    // Callback for the parsed response is the last parameter

    @GET("/api")
    Call<ImageSearchResult> queryImages(@Query("q") String query, @Query("key") String key);

    @GET("/api")
    Call<ImageSearchResult> queryImages(@Query("q") String query, @Query("key") String key, @Query("page")
        int pageNumber);

}