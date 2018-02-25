package solutions.farsight.pixabayimagesearcher;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.widget.SearchView;
import android.widget.Toast;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import solutions.farsight.pixabayimagesearcher.model.Hit;
import solutions.farsight.pixabayimagesearcher.model.ImageSearchResult;
import solutions.farsight.pixabayimagesearcher.service.PixabayEndpointInterface;
import solutions.farsight.pixabayimagesearcher.view.ImageResult;
import solutions.farsight.pixabayimagesearcher.view.ImageResultRecyclerAdapter;

public class MainActivity extends AppCompatActivity  {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String EMPTY_STRING = "";

    @BindView(R.id.searchBox) SearchView searchView;
    @BindView(R.id.searchResults) RecyclerView searchResultView;
    @BindString(R.string.pixabayBaseUrl) String baseUrl;
    @BindString(R.string.pixabayApiKey) String apiKey;

    private Call<ImageSearchResult> imageSearchCall;
    private PixabayEndpointInterface pixabayService;
    private List<ImageResult> dataSet = new ArrayList<>();
    private ImageResultRecyclerAdapter imageResultRecyclerAdapter;
    private GridLayoutManager gridLayoutManager;

    //state variables
    private int previousItemCount =0;
    private int pageNumber =1;
    private int totalResults =0;
    private String currentQuery = EMPTY_STRING;
    private String requestQuery = EMPTY_STRING;

    private boolean loading = false;

    final OkHttpClient okHttpClient = new OkHttpClient.Builder()
        .readTimeout(10, TimeUnit.SECONDS)
        .connectTimeout(10, TimeUnit.SECONDS)
        .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        imageResultRecyclerAdapter = new ImageResultRecyclerAdapter(dataSet, R
            .layout.item);
        searchResultView.setAdapter(imageResultRecyclerAdapter);
        gridLayoutManager = new GridLayoutManager(this, 2);
        searchResultView.setLayoutManager(gridLayoutManager);
        searchResultView.setItemAnimator(new DefaultItemAnimator());

        //TODO: move retrofit binding elsewhere.
        pixabayService = new Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(PixabayEndpointInterface.class);

        searchView.setOnQueryTextListener(
            new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String text) {
                    //populate suggestion?
                    return true;
                }

                @Override
                public boolean onQueryTextSubmit(String newQuery) {
                    Log.d(TAG , "search for " + newQuery);
                    try {
                        newQuery = URLEncoder.encode(newQuery, "UTF-8");
                        //cancel in flight query if the inflight query is different from the new
                        // query
                        searchView.setIconified(true);
                        if(imageSearchCall!=null && imageSearchCall.isExecuted()) {
                            if(!requestQuery.equals(newQuery)) {
                                imageSearchCall.cancel(); //cancel old query if the query value
                                // is different
                            }
                        }

                        requestQuery = newQuery;
                        imageSearchCall = pixabayService.queryImages(requestQuery, apiKey);
                        imageSearchCall.enqueue(new ImageSearchCallback());
                    } catch (UnsupportedEncodingException e) {
                        MainActivity.this.toast("Problem with Query: " + newQuery);
                        e.printStackTrace();
                    }

                    return true;
                }
            });

        searchResultView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) { //scrolling down
                    int itemCount = gridLayoutManager.getItemCount();

                    if (itemCount != previousItemCount) {
                        loading = false;
                    }

                    if (!loading && gridLayoutManager.findLastVisibleItemPosition() >= itemCount - 1) {
                        previousItemCount = itemCount;
                        if(dataSet.size() < totalResults) {
                            loading = true;
                            imageSearchCall = pixabayService.queryImages(currentQuery, apiKey,
                                ++pageNumber);
                            imageSearchCall.enqueue(new ImageSearchCallback());
                        }
                    }
                }
            }
        });
    }

    private void toast(String message) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        //restore screen state: search term, scroll location
        super.onRestoreInstanceState(savedInstanceState);

        List <Parcelable> list = savedInstanceState.getParcelableArrayList("result");

        if(list!=null) {
            for (Parcelable p : list) {
                dataSet.add((ImageResult) p);
            }
        }

        imageResultRecyclerAdapter.notifyDataSetChanged();
        savedInstanceState.getString("searchString", EMPTY_STRING);
        currentQuery = savedInstanceState.getString("currentQuery", EMPTY_STRING);
        totalResults = savedInstanceState.getInt("currentTotal", 0);

        Log.d(TAG, "onRestoreInstanceState restored results: " + dataSet.size());
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        //save screen state: search term, scroll location
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putParcelableArrayList("result" ,
            (ArrayList<? extends Parcelable>) dataSet);
        savedInstanceState.putString("searchString", searchView.getQuery().toString());
        savedInstanceState.putString("currentQuery", currentQuery);
        savedInstanceState.putInt("currentTotal", totalResults);

        Log.d(TAG, "onSaveInstanceState save results: " + dataSet.size());
    }


    private class ImageSearchCallback implements Callback<ImageSearchResult> {

        @Override
        public void onResponse(Call<ImageSearchResult> call,
            Response<ImageSearchResult> response) {
            //extract image Url's
            int responseCode = response.code();
            Log.d(TAG, "response code " + response.code());
            if(responseCode!=200) {
                //TODO: error out. for example, 429 too many requests
                // just return and don't modify search state
                return;
            }

            Log.d(TAG, "addResults " + currentQuery + " " + requestQuery);

            //everything ok!
            if(requestQuery.equals(currentQuery)) {

                //add
                for(Hit hit: response.body().getHits()) {
                    dataSet.add(new ImageResult(hit));
                }
            } else {
                Log.d(TAG, "new Results");
                //replace existing set
                pageNumber = 1;
                dataSet.clear();
                currentQuery = requestQuery;
                totalResults = response.body().getTotalHits();
                //populate inital results
                for(Hit hit: response.body().getHits()) {
                    dataSet.add(new ImageResult(hit));
                }
            }

            imageResultRecyclerAdapter.notifyDataSetChanged();
        }

        @Override
        public void onFailure(Call<ImageSearchResult> call, Throwable t) {
            MainActivity.this.toast("Could not fetch results");
            t.printStackTrace();
        }
    }
}
