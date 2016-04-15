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
 * Created by Kristian on 13.04.2016.
 */
public class AndroidFlavorAdapter extends ArrayAdapter<AndroidFlavor> {
    private static final String LOG_TAG = AndroidFlavorAdapter.class.getSimpleName();

    /**
     * This is our own custom constructor (it doesn't mirror a superclass constructor).
     * The context is used to inflate the layout file, and the List is the data we want
     * to populate into the lists
     *
     * @param context        The current context. Used to inflate the layout file.
     * @param androidFlavors A List of AndroidFlavor objects to display in a list
     */
    public AndroidFlavorAdapter(Activity context, List<AndroidFlavor> androidFlavors) {
        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for two TextViews and an ImageView, the adapter is not
        // going to use this second argument, so it can be any value. Here, we used 0.
        super(context, 0, androidFlavors);
    }

    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.)
     *
     * @param position    The AdapterView position that is requesting a view
     * @param convertView The recycled view to populate.
     *                    (search online for "android view recycling" to learn more)
     * @param parent The parent ViewGroup that is used for inflation.
     * @return The View for the position in the AdapterView.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Gets the AndroidFlavor object from the ArrayAdapter at the appropriate position
        AndroidFlavor androidFlavor = getItem(position);

        // Adapters recycle views to AdapterViews.
        // If this is a new View object we're getting, then inflate the layout.
        // If not, this view already has the layout inflated from a previous call to getView,
        // and we modify the View widgets as usual.
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.flavor_item, parent, false);
        }
        // selects the (now empty) imageview for poster
        ImageView iconView = (ImageView) convertView.findViewById(R.id.flavor_image);

        // w342 is for size of image, I found 342 worked well for my Galaxy S5, not tested on other phones
        // Some phones might be ok with lower resolutions for quicker load times
        String baseUrl = "http://image.tmdb.org/t/p/w342";
        // Uses picasso library to load image from url into the imageview
        try {
            Picasso.with(getContext()).load(baseUrl + androidFlavor.versionNumber).into(iconView);

            TextView versionNameView = (TextView) convertView.findViewById(R.id.flavor_text);
            versionNameView.setText(androidFlavor.versionName);
        } catch (NullPointerException e){
        }


        return convertView;
    }
}