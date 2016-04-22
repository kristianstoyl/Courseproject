package com.ntnu.kristian.courseproject;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Kristian on 22.04.2016.
 */
public class MoviesAdapter extends ArrayAdapter<Movies> {
    private static final String LOG_TAG = MoviesAdapter.class.getSimpleName();

    public MoviesAdapter(Activity context, List<Movies> movies) {
        super(context, 0, movies);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Movies movie = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.flavor_item, parent, false);
        }
        try {
            TextView versionNameView = (TextView) convertView.findViewById(R.id.item_wishlist_tv);
            versionNameView.setText(movie.getName());
        } catch (NullPointerException e){
        }


        return convertView;
    }
}