package solutions.farsight.pixabayimagesearcher;

import android.arch.lifecycle.ViewModel;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.SearchView;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity  {
    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.searchBox) SearchView searchView;
    @BindView(R.id.searchResults) RecyclerView searchResultView;
    @BindString(R.string.pixabayBaseUrl) String baseUrl;
    @BindString(R.string.pixabayApiKey) String apiKey;

    private String screenState; //TODO: put screen state information here

    private Call<ImageResult> imageSearchCall;
    private PixabayEndpointInterface pixabayService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        searchResultView.setAdapter(new ImageResultRecyclerAdapter(new ArrayList<ViewModel>(), R
            .layout.item));
        searchResultView.setLayoutManager(new LinearLayoutManager(this));
        searchResultView.setItemAnimator(new DefaultItemAnimator());

        //TODO: move retrofit binding elsewhere.
        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
        pixabayService = retrofit.create(PixabayEndpointInterface.class);

        searchView.setOnQueryTextListener(
            new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String text) {
                    //populate suggestion?
                    return true;
                }

                @Override
                public boolean onQueryTextSubmit(String query) {
                    Log.d(TAG , "search for " + query);

                    String formattedQuery;
                    try {
                        //TODO: handle multiple calls
                        formattedQuery = URLEncoder.encode(query, "UTF-8");
                        imageSearchCall = pixabayService.queryImages(formattedQuery, apiKey);
                        imageSearchCall.enqueue(new Callback<ImageResult>() {
                            @Override
                            public void onResponse(Call<ImageResult> call, Response<ImageResult> response) {
                                //extract image Url's
                                for(Hit hit: response.body().getHits()) {
                                    Log.d(TAG, "Hit " + hit.getPreviewURL());
                                }
                            }

                            @Override
                            public void onFailure(Call<ImageResult> call, Throwable t) {
                                //TODO: handle error
                            }
                        });
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    return true;
                }
            });
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState,
        PersistableBundle persistentState) {
        //restore screen state: search term, scroll location
        super.onRestoreInstanceState(savedInstanceState, persistentState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        //save screen state: search term, scroll location
        super.onSaveInstanceState(outState, outPersistentState);
    }
}
