package solutions.farsight.pixabayimagesearcher;

import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.SearchView;
import butterknife.BindView;
import butterknife.ButterKnife;
import java.util.List;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements PixabayLookupAsyncTask
    .PixabayLookupAsyncTaskCallbackListener {
    private static final String tag = MainActivity.class.getSimpleName();

    @BindView(R.id.searchBox) SearchView searchView;

    private String screenState; //TODO: put screen state information here

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        searchView.setOnQueryTextListener(
            new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String text) {
                    //maybe populate suggestion?
                    return true;
                }

                @Override
                public boolean onQueryTextSubmit(String query) {
                    Log.d(tag , "imageSearch for " + query);
                    new PixabayLookupAsyncTask(MainActivity.this).execute(query);
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

    @Override
    public void success(List<ImageResult> result) {
        //do something with image results fetched
        //update screen
        //trigger image download?
    }

    @Override
    public void fail() {
        //do something with image
        //update screen with chip
    }
}
