package com.ntnu.kristian.courseproject;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DetailActivityFragment extends Fragment {
    private AndroidFlavor poster;
    private TextView tv_overView;
    private TextView tv_release;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        tv_overView = (TextView) rootView.findViewById(R.id.detail_overViewTV);
        tv_release = (TextView) rootView.findViewById(R.id.detail_release);
        Intent intent = getActivity().getIntent();
        if (intent != null){
            // receives the poster object from intent
            poster = intent.getParcelableExtra("movieTag");
            // initializes imageview from fragment_detail
            ImageView imgView = (ImageView) rootView.findViewById(R.id.detail_posterIV);
            // base url, common for all movieposters
            // w780 size, bigger is always better! (assuming you have fast internet)
            String baseUrl = "http://image.tmdb.org/t/p/w342";
            // Uses picasso library to load image from url to imageview

            tv_overView.setText(poster.overView);
            tv_release.setText(poster.releaseDate);

            Picasso.with(getContext()).load(baseUrl + poster.posterNumber).into(imgView);
        }
        return rootView;
    }

    public class FetchPosterTask extends AsyncTask<String, Void, String> {
        private final String LOG_TAG = FetchPosterTask.class.getSimpleName();

        @Override
        protected String doInBackground(String... params){
            // JsonList
            // http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=5aa5bc75c39f6d200fa6bd741896baaa

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String API_KEY = "5aa5bc75c39f6d200fa6bd741896baaa";
            String posterJsonStr = null;

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("http://api.themoviedb.org/3/movie/550/videos");
            stringBuilder.append("&api_key=" + API_KEY);

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

        private String getPosterDataFromJson(String json)
                throws JSONException {
            //
            // Used a jsonformatter to look at how the json is arranged, to know what arrays and objects I need
            final String OWM_RESULTS = "results";
            final String OWM_KEY = "key";

            JSONObject posterJson = new JSONObject(json);
            JSONArray posterArray = posterJson.getJSONArray(OWM_RESULTS);

            return posterArray.getJSONObject(0).getString(OWM_KEY);
        }

        @Override
        protected void onPostExecute(String result){
            if(result != null){
                // result = youtube id for trailer, e.g: youtube.com/watch?v=<result>
            }
        }
    }
}
