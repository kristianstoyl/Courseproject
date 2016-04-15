package com.ntnu.kristian.courseproject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class tmdbSearchFragment extends Fragment {
    private final String LOG_TAG = BrowseFragment.class.getSimpleName();
    private View rootView;
    private AndroidFlavorAdapter mMovieAdapter;
    private GridView gridView;
    private EditText editText;
    private Button sendButton;
    private Button nextPageButton;

    int page = 1;
    Boolean newPage = false;
    String query;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_tmdb_search, container, false);
        gridView = (GridView) rootView.findViewById(R.id.search_gridview);
        mMovieAdapter = new AndroidFlavorAdapter(getActivity(), new ArrayList<AndroidFlavor>());
        gridView.setAdapter(mMovieAdapter);

        editText = (EditText) rootView.findViewById(R.id.edit_message);
        sendButton = (Button) rootView.findViewById(R.id.send_button);
        nextPageButton= (Button) rootView.findViewById(R.id.nextpage_button);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editText.getText().toString() != ""){
                    // Resets page number
                    newPage = false;
                    page = 1;

                    query = editText.getText().toString();
                    TMDBQueryManager tmQuery = new TMDBQueryManager();
                    tmQuery.execute();
                }
            }
        });
        nextPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editText.getText().toString() != ""){
                    newPage = true;
                    page++;
                    query = editText.getText().toString();
                    TMDBQueryManager tmQuery = new TMDBQueryManager();
                    tmQuery.execute();
                }
            }
        });
        return rootView;
    }


    public void updateViewWithResults(AndroidFlavor[] result) {
        Log.d("updateViewWithResults", result.toString());
        // Add results to listView.

        // Update Activity to show listView
    }

    private class TMDBQueryManager extends AsyncTask<String, Void, AndroidFlavor[]> {

        private final String TMDB_API_KEY = "5aa5bc75c39f6d200fa6bd741896baaa";
        private static final String DEBUG_TAG = "TMDBQueryManager";

        @Override
        protected AndroidFlavor[] doInBackground(String... params) {
            try {
                return searchIMDB(query);
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(AndroidFlavor[] result) {
            if(result != null){
                // clears adapter, just to make sure there is no unnecessary objects in it
                if(!newPage)
                    mMovieAdapter.clear();
                for(AndroidFlavor movieList : result){
                    mMovieAdapter.add(movieList);
                }
            }
        };

        /**
         * Searches IMDBs API for the given query
         * @param query The query to search.
         * @return A list of all hits.
         */
        public AndroidFlavor[] searchIMDB(String query) throws IOException {
            // Build URL
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("http://api.themoviedb.org/3/search/movie");
            stringBuilder.append("?api_key=" + TMDB_API_KEY);
            stringBuilder.append("&query=" + query);
            stringBuilder.append("&page=" + page);
            URL url = new URL(stringBuilder.toString());

            String posterJsonStr = null;

            InputStream stream = null;
            try {
                // Establish a connection
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.addRequestProperty("Accept", "application/json"); // Required to get TMDB to play nicely.
                conn.setDoInput(true);
                conn.connect();

                int responseCode = conn.getResponseCode();
                Log.d(DEBUG_TAG, "The response code is: " + responseCode + " " + conn.getResponseMessage());

                stream = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                String line;
                StringBuffer buffer = new StringBuffer();
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                posterJsonStr = buffer.toString();
            } catch (IOException e){
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (final IOException e) {
                        Log.e("MainActivityFragment", "Error closing stream", e);
                    }
                }
            }


            try {
                return parseResult(posterJsonStr);
            } catch (JSONException e){
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        private AndroidFlavor[] parseResult(String json)
                throws JSONException {
            //
            // Used a jsonformatter to look at how the json is arranged, to know what arrays and objects I need
            final String OWM_RESULTS = "results";
            final String OWM_TITLE = "title";
            final String OWM_NUMBER = "poster_path";
            final String OWM_IMAGE = "id";

            JSONObject posterJson = new JSONObject(json);
            JSONArray posterArray = posterJson.getJSONArray(OWM_RESULTS);

            // json returns a list of 20 most popular movies
            // TMDB only allows 20 results per search
            AndroidFlavor[] posterList = new AndroidFlavor[20];

            for(int i = 0; i < posterArray.length(); i++){
                String name;
                String number;
                int image;

                JSONObject movie = posterArray.getJSONObject(i);

                // Title of movie
                name = movie.getString(OWM_TITLE);
                // Number is the posternumber of the url. Every poster has the same baseurl, but different number
                number = movie.getString(OWM_NUMBER);
                // ID of movie, never really used anywhere
                image = movie.getInt(OWM_IMAGE);
                // adds movies to the list of posters.
                posterList[i] = new AndroidFlavor(name, number, image);
            }
            return posterList;
        }

    }
}
