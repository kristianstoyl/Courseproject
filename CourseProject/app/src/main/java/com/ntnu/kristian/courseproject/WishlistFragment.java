package com.ntnu.kristian.courseproject;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ntnu.kristian.courseproject.Data.WishlistDbHelper;

import java.util.ArrayList;


public class WishlistFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private final String LOG_TAG = WishlistFragment.class.getSimpleName();
    WishlistDbHelper db;
    ListView listView;
    ArrayAdapter adapter;
    Cursor res;

    public WishlistFragment(){
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_wishlist, container, false);
        getActivity().setTitle("Wishlist");
        Log.d(LOG_TAG, "wishList - onCreateView");

        // DB
        listView = (ListView) rootView.findViewById(R.id.listView);
        db = new WishlistDbHelper(getActivity());

        res = db.wishlistGetAllData();
        ArrayList<String> movies = getAllMovies(res);
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, movies);
        listView.setAdapter(adapter);

        final ArrayList<String> finalMovies = movies;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = getCursor();
                Object o = listView.getItemAtPosition(position);
                cursor.moveToPosition(position);
                Log.d(LOG_TAG, String.valueOf(position) + " ---" + " -  - " + cursor.getString(2)); // Delete
                String stringId = String.valueOf(o);
                Integer deleted = db.wishlistDeleteData(cursor.getString(0));

                try {
                    if (db.watchedInsertData(Integer.valueOf(cursor.getString(1)), cursor.getString(2)))
                        Log.d(LOG_TAG, "Added to Database!");
                    else
                        Log.d(LOG_TAG, "Could not add to Database for some reason");
                } catch(NumberFormatException e){
                    Log.w(LOG_TAG, e);
                }
                updateViews();
            }
        });
        return rootView;
    }
    public Cursor getCursor(){
        return res;
    }

    public void updateViews(){
        res = db.wishlistGetAllData();
        ArrayList<String> movies = getAllMovies(res);
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, movies);
        listView.setAdapter(adapter);
    }

    public ArrayList<String> getAllMovies(Cursor res){
        ArrayList<String> results = new ArrayList<>();
        if(res.getCount() == 0)
            // No data available
            ;
        else {
            StringBuffer buffer = new StringBuffer();
            while(res.moveToNext()){
                buffer.append("Id :" + res.getString(1)+"\n");
                buffer.append("Name :" + res.getString(2)+"\n");
                results.add(res.getString(2));
            }
            //tv.setText(buffer);
        }
        return results;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_detail, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        ShareActionProvider mShareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // Attach an intent to this ShareActionProvider.  You can update this at any time,
        // like when the user selects a new piece of data they might like to share.
        try {
            if (mShareActionProvider != null) {
                res = db.wishlistGetAllData();
                ArrayList<String> movies = getAllMovies(res);
                    Log.e(LOG_TAG, movies.size() + " <---");
                String movieString = "Wishlist: ";
                for(int i = 0; i < movies.size(); i++){
                    movieString = movieString + "\n \n " + movies.get(i);
                }
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, movieString);
                mShareActionProvider.setShareIntent(shareIntent);
            }
        } catch(NullPointerException e){
            Log.d(LOG_TAG, "Share Action Provider is null?");
        }
    }
}
