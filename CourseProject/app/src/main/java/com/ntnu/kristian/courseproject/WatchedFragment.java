package com.ntnu.kristian.courseproject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
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
import android.widget.Toast;

import com.ntnu.kristian.courseproject.Data.WishlistDbHelper;

import java.util.ArrayList;


public class WatchedFragment extends Fragment {
    /**
     * This fragment is showing a list of movies found from the
     * watched database table. It's purpose is to show all movies
     * the user has seen
     */
    private final String LOG_TAG = WatchedFragment.class.getSimpleName();
    WishlistDbHelper db;
    ListView listView;
    ArrayAdapter adapter;
    Cursor res;

    public WatchedFragment() {
        setHasOptionsMenu(true); // Turns on optionmenu so we can use sharing
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_watched, container, false);
        getActivity().setTitle(R.string.watched_title);

        listView = (ListView) rootView.findViewById(R.id.watched_listView);
        // Database class
        db = new WishlistDbHelper(getActivity());

        // Returns a cursor with all data from the SQLite database
        res = db.watchedGetAllData();
        // Returns an arraylist of Strings containing the names of all movies from SQLite database
        ArrayList<String> movies = getAllMovies(res);
        // Sets the adapter as an ArrayAdapter with simple_expandable_list_item_1 layout
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_expandable_list_item_1, movies);
        listView.setAdapter(adapter);

        // Click listener for listview
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final int position_final = position;

                // Pops up an alertbox for user to confirm deletion of entry
                new AlertDialog.Builder(getContext())
                        .setTitle("Delete entry")
                        .setMessage("Are you sure you want to delete this entry?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                Cursor cursor = getCursor();
                                cursor.moveToPosition(position_final);
                                // Cursor.getString(0) is the ID
                                // Cursor.getString(1) is first table entry(movie_ID)
                                // cursor.getString(2) is second table entry(movie name)
                                Integer deleted = db.watchedDeleteData(cursor.getString(0));
                                updateViews(); // Refreshes views so listentry gets removed
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
        updateViews();
        return rootView;
    }
    public Cursor getCursor(){
        return res;
    }

    public void updateViews(){ // Refreshes listview
        res = db.watchedGetAllData();
        ArrayList<String> movies = getAllMovies(res);
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_expandable_list_item_1, movies);
        listView.setAdapter(adapter);
    }

    /**
     * Takes in a cursor object with database data, returns list of ID and names
     * @param res
     * @return Arraylist of movienames
     */
    public ArrayList<String> getAllMovies(Cursor res){
        ArrayList<String> results = new ArrayList<>();
        if(res.getCount() == 0)
            // No data available
            ;
        else {
            //StringBuffer buffer = new StringBuffer();
            while(res.moveToNext()){
                //buffer.append("Id :" + res.getString(1)+"\n");
                //buffer.append("Name :" + res.getString(2)+"\n");
                results.add(res.getString(2));
            }
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
                res = db.watchedGetAllData();
                ArrayList<String> movies = getAllMovies(res);
                Log.e(LOG_TAG, movies.size() + " <---");
                String movieString = "Watched: ";
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
