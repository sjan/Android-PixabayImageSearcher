package solutions.farsight.pixabayimagesearcher;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.SearchView;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
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

    private Call<ImageSearchResult> imageSearchCall;
    private PixabayEndpointInterface pixabayService;
    private List<ImageResult> dataSet = new ArrayList<ImageResult>();
    private ImageResultRecyclerAdapter imageResultRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        imageResultRecyclerAdapter = new ImageResultRecyclerAdapter(dataSet, R
            .layout.item);
        searchResultView.setAdapter(imageResultRecyclerAdapter);
        searchResultView.setLayoutManager(new GridLayoutManager(this, 2));
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
                        //TODO: handle the case where multiple queries come in seq
                        formattedQuery = URLEncoder.encode(query, "UTF-8");
                        imageSearchCall = pixabayService.queryImages(formattedQuery, apiKey);
                        imageSearchCall.enqueue(new Callback<ImageSearchResult>() {

                            @Override
                            public void onResponse(Call<ImageSearchResult> call,
                                Response<ImageSearchResult> response) {
                                dataSet.clear();
                                //extract image Url's

                                int responseCode = response.code();
                                if(responseCode!=200) {
                                    //TODO: error out. for example, 429 too many requests
                                    return;
                                }

                                Log.d(TAG, " code " + response.code());

                                int resultCount = response.body().getTotalHits();

                                for(Hit hit: response.body().getHits()) {
                                    dataSet.add(new ImageResult(hit));
                                }

                                imageResultRecyclerAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onFailure(Call<ImageSearchResult> call, Throwable t) {
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

        Log.d(TAG, "onRestoreInstanceState restored results: " + dataSet.size());
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        //save screen state: search term, scroll location
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putParcelableArrayList("result" ,
            (ArrayList<? extends Parcelable>) dataSet);
        savedInstanceState.putString("searchString", searchView.getQuery().toString());

        Log.d(TAG, "onSaveInstanceState save results: " + dataSet.size());
    }
}
