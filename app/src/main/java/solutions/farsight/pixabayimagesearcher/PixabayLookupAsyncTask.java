package solutions.farsight.pixabayimagesearcher;

import android.os.AsyncTask;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

/**
 * Created by Stephen on 2/23/2018.
 */

public class PixabayLookupAsyncTask extends AsyncTask<String, Void, JSONObject> {
    private static final String TAG = PixabayLookupAsyncTask.class.getSimpleName();

    private final PixabayLookupAsyncTaskCallbackListener pixabayLookupAsyncTaskCallbackListener;

    public PixabayLookupAsyncTask(PixabayLookupAsyncTaskCallbackListener
        pixabayLookupAsyncTaskCallbackListener) {
        this.pixabayLookupAsyncTaskCallbackListener = pixabayLookupAsyncTaskCallbackListener;
    }

    @Override
    protected JSONObject doInBackground(String... query) {
        //TODO: initialize http client
        //TODO: query pixabay
        //TODO: parse result
        return null;
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        Log.d(TAG, "onPostExecute " + jsonObject);
        //parse Result
        //TODO: consider what if error. call error?
        pixabayLookupAsyncTaskCallbackListener.success(new ArrayList<ImageResult>());
    }

    public interface PixabayLookupAsyncTaskCallbackListener {
        void success(List<ImageResult> result);
        void fail();
    }
}
