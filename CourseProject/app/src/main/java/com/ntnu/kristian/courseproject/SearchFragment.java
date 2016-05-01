package com.ntnu.kristian.courseproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
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
import java.net.URLEncoder;
import java.util.ArrayList;

public class SearchFragment extends Fragment {
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
        getActivity().setTitle(R.string.search_title);

        gridView = (GridView) rootView.findViewById(R.id.search_gridview);
        mMovieAdapter = new AndroidFlavorAdapter(getActivity(), new ArrayList<AndroidFlavor>());
        gridView.setAdapter(mMovieAdapter);

        editText = (EditText) rootView.findViewById(R.id.edit_message);
        sendButton = (Button) rootView.findViewById(R.id.send_button);
        nextPageButton= (Button) rootView.findViewById(R.id.nextpage_button);
        nextPageButton.setVisibility(View.INVISIBLE);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editText.getText().toString() != ""){
                    // Resets page number
                    editText.clearFocus();
                    sendButton.requestFocus();

                    newPage = false;
                    page = 1;
                    query = editText.getText().toString();

                    TMDBQueryManager tmQuery = new TMDBQueryManager();
                    tmQuery.execute();
                    editText.clearFocus();
                    // Makes next button visible
                    nextPageButton.setVisibility(View.VISIBLE);
                    // Removes keyboard on press
                    InputMethodManager inputManager =
                            (InputMethodManager) getContext().
                                    getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(
                            getActivity().getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);

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

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(LOG_TAG, "onItemClick, " + position);
                AndroidFlavor poster = mMovieAdapter.getItem(position);
                Intent i = new Intent(getActivity(), DetailActivity.class);
                // Inserts the entire poster object into intent, so we can use all its variables in detail
                // -activity, it is not used now, but right now only movieposter is used in detailactivity
                i.putExtra("movieTag", poster);
                startActivity(i);
            }
        });


        return rootView;
    }

    private class TMDBQueryManager extends AsyncTask<String, Void, AndroidFlavor[]> {

        private final String TMDB_API_KEY = "5aa5bc75c39f6d200fa6bd741896baaa";

        @Override
        protected AndroidFlavor[] doInBackground(String... params) {
            try {
                String s = query.replace(" ", "-");

                return searchIMDB(s);
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
            final String OWM_POSTER = "poster_path";
            final String OWM_IMAGE = "id";
            final String OWM_OVERVIEW = "overview";
            final String OWM_RELEASE = "release_date";
            final String OWM_ID = "id";

            JSONObject posterJson = new JSONObject(json);
            JSONArray posterArray = posterJson.getJSONArray(OWM_RESULTS);

            // json returns a list of 20 most popular movies
            // TMDB only allows 20 results per search
            AndroidFlavor[] posterList = new AndroidFlavor[20];

            for(int i = 0; i < posterArray.length(); i++){
                String name;
                String poster;
                int image;
                String release;
                String overview;
                int id;

                JSONObject movie = posterArray.getJSONObject(i);

                // Title of movie
                name = movie.getString(OWM_TITLE);
                // Number is the posternumber of the url. Every poster has the same baseurl, but different number
                poster = movie.getString(OWM_POSTER);
                // ID of movie, never really used anywhere
                image = movie.getInt(OWM_IMAGE);
                id = movie.getInt(OWM_ID);
                // adds movies to the list of posters.
                release = movie.getString(OWM_RELEASE);
                overview = movie.getString(OWM_OVERVIEW);
                posterList[i] = new AndroidFlavor(id, name, poster, image, release, overview);
            }
            return posterList;
        }

    }
}
