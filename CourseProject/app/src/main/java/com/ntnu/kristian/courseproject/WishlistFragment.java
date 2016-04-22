package com.ntnu.kristian.courseproject;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

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

        res = db.getAllData();
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
                Integer deleted = db.deleteData(cursor.getString(0));
                updateViews();
            }
        });
        return rootView;
    }
    public Cursor getCursor(){
        return res;
    }

    public void updateViews(){
        res = db.getAllData();
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
    public ArrayList<Movies> getAllMoviesMovie(WishlistDbHelper db){
        ArrayList<Movies> results = new ArrayList<>();
        Cursor res = db.getAllData();
        if(res.getCount() == 0)
            // No data available
            ;
        else {
            StringBuffer buffer = new StringBuffer();
            while(res.moveToNext()){
                buffer.append("Id :" + res.getString(1)+"\n");
                buffer.append("Name :" + res.getString(2)+"\n");
                Movies mov = new Movies(Integer.getInteger(res.getString(1)), res.getString(2));
                results.add(mov);
            }
            //tv.setText(buffer);
        }
        return results;
    }
}
