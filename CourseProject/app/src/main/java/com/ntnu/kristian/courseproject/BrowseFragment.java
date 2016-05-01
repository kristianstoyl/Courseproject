package com.ntnu.kristian.courseproject;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

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

public class BrowseFragment extends Fragment {
    private final String LOG_TAG = BrowseFragment.class.getSimpleName();
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private AndroidFlavorAdapter mMovieAdapter;
    public ProgressBar progressBar;
    private Button nextPageButton;

    int page = 1;
    Boolean newPage = false;


    @Override
    public void onStart(){
        super.onStart();
        updatePosters();
    }
    public void updatePosters(){
        // Makes progressbar start spinning before it downloads data
        progressBar.setVisibility(View.VISIBLE);
        //
        Log.d(LOG_TAG, "updatePosters");
        FetchPosterTask posterTask = new FetchPosterTask();
        posterTask.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_browse, container, false);
        Log.d(LOG_TAG, "onCreateView");

        getActivity().setTitle(R.string.browse_title);
        // initializes progressBar from ID in fragment_main.xml file
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        nextPageButton= (Button) rootView.findViewById(R.id.nextpage_button);

        // AndroidFlavorAdapter is a custom adapter used for the movieposters
        mMovieAdapter = new AndroidFlavorAdapter(getActivity(), new ArrayList<AndroidFlavor>());
        // Initializes the gridview with posters
        GridView gridView = (GridView) rootView.findViewById(R.id.gridview);
        // Sets gridview adapter as mMovieAdapter
        gridView.setAdapter(mMovieAdapter);

        // For detailActivity
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

        nextPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    newPage = true;
                    page++;
                    updatePosters();
            }
        });
        return rootView;
    }

    public class FetchPosterTask extends AsyncTask<String, Void, AndroidFlavor[]> {
        private final String LOG_TAG = FetchPosterTask.class.getSimpleName();

        @Override
        protected AndroidFlavor[] doInBackground(String... params){
            // JsonList
            // http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=5aa5bc75c39f6d200fa6bd741896baaa

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String API_KEY = "5aa5bc75c39f6d200fa6bd741896baaa";
            String posterJsonStr = null;

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc");
            stringBuilder.append("&api_key=" + API_KEY);
            stringBuilder.append("&page=" + page);

            try {
                URL url = new URL(stringBuilder.toString());

                // Create the request to theMovieDataBase, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while((line = reader.readLine()) != null){
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
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("MainActivityFragment", "Error closing stream", e);
                    }
                }
            }

            try {
                return getPosterDataFromJson(posterJsonStr);
            } catch (JSONException e){
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        private AndroidFlavor[] getPosterDataFromJson(String json)
                throws JSONException {
            //
            // Used a jsonformatter to look at how the json is arranged, to know what arrays and objects I need
            final String OWM_RESULTS = "results";
            final String OWM_TITLE = "title";
            final String OWM_NUMBER = "poster_path";
            final String OWM_IMAGE = "id";
            final String OWM_OVERVIEW = "overview";
            final String OWM_RELEASE = "release_date";
            final String OWM_ID = "id";

            JSONObject posterJson = new JSONObject(json);
            JSONArray posterArray = posterJson.getJSONArray(OWM_RESULTS);

            // json returns a list of 20 most popular movies
            AndroidFlavor[] posterList = new AndroidFlavor[20];

            for(int i = 0; i < posterArray.length(); i++){
                String name;
                String number;
                int image;
                int id;
                String release;
                String overview;

                JSONObject movie = posterArray.getJSONObject(i);

                // Title of movie
                name = movie.getString(OWM_TITLE);
                // Number is the posternumber of the url. Every poster has the same baseurl, but different number
                number = movie.getString(OWM_NUMBER);
                // ID of movie, never really used anywhere
                image = movie.getInt(OWM_IMAGE);
                id = movie.getInt(OWM_ID);
                // adds movies to the list of posters.
                release = movie.getString(OWM_RELEASE);
                overview = movie.getString(OWM_OVERVIEW);
                posterList[i] = new AndroidFlavor(id, name, number, image, release, overview);
            }
            return posterList;
        }

        @Override
        protected void onPostExecute(AndroidFlavor[] result){
            if(result != null){
                // Only clears if you are not looking for a new page
                if(!newPage)
                    mMovieAdapter.clear();
                for(AndroidFlavor movieList : result){
                    mMovieAdapter.add(movieList);
                }

                // Removes progressbar, because the data is finished downloading
                progressBar.setVisibility(View.GONE);
            }
        }
    }
}
