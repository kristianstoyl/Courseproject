package com.ntnu.kristian.courseproject.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ntnu.kristian.courseproject.Data.WishlistContract;
/**
 * Created by Kristian on 22.04.2016.
 */
public class WishlistDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 2;

    static final String DATABASE_NAME = "wishlist.db";


    public WishlistDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create tables, with ThemovieDB id and movie name columns
        final String SQL_CREATE_WISHLIST_TABLE = "CREATE TABLE " + WishlistContract.WishlistEntry.TABLE_NAME+ " (" +
                WishlistContract.WishlistEntry._ID + " INTEGER PRIMARY KEY," +
                WishlistContract.WishlistEntry.MOVIE_ID + " INTEGER UNIQUE NOT NULL," +
                WishlistContract.WishlistEntry.MOVIE_NAME + " TEXT NOT NULL" +
                " );";

        final String SQL_CREATE_WATCHED_TABLE = "CREATE TABLE " + WishlistContract.WatchedlistEntry.TABLE_NAME+ " (" +
                WishlistContract.WatchedlistEntry._ID + " INTEGER PRIMARY KEY," +
                WishlistContract.WatchedlistEntry.MOVIE_ID + " INTEGER UNIQUE NOT NULL," +
                WishlistContract.WatchedlistEntry.MOVIE_NAME + " TEXT NOT NULL" +
                " );";
        // Creates table
        db.execSQL(SQL_CREATE_WISHLIST_TABLE);
        db.execSQL(SQL_CREATE_WATCHED_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Runs when you update database versions. Currently clears tables
        db.execSQL("DROP TABLE IF EXISTS " + WishlistContract.WishlistEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + WishlistContract.WatchedlistEntry.TABLE_NAME);
        onCreate(db);
    }

    // Insert data to database, only needs TMDB id and name
    public boolean wishlistInsertData(int id, String name){
        SQLiteDatabase db = this.getReadableDatabase(); // Gets database instance
        ContentValues contentValues = new ContentValues(); // Create contentValue instance for storing table
        contentValues.put(WishlistContract.WishlistEntry.MOVIE_ID, id); // Add a table row to Movie_ID with the int id
        contentValues.put(WishlistContract.WishlistEntry.MOVIE_NAME, name); // Add a table row to MOVIE_NAME with the string name
        long result = db.insert(WishlistContract.WishlistEntry.TABLE_NAME, null, contentValues); // Inserts into database, returns -1 if it fails
        if(result == -1)
            return false;
        else
            return true;
    }

    // Returns a cursor with all data
    public Cursor wishlistGetAllData(){
        SQLiteDatabase db = this.getReadableDatabase(); // Gets database instance
        Cursor res = db.rawQuery("select * from " + WishlistContract.WishlistEntry.TABLE_NAME, null);
        return res;
    }

    // Deletes data from the _ID received. (database ID, NOT themoviedb ID)
    public Integer wishlistDeleteData(String id){
        SQLiteDatabase db = this.getReadableDatabase(); // Gets database instance
        return db.delete(WishlistContract.WishlistEntry.TABLE_NAME, WishlistContract.WishlistEntry._ID +" = ?", new String[]{id});
    }

    /**
     * Searches database for themoviedb ID received
     * @param tmdbID
     * @return  True or false depending if movie is found
     */
    public boolean wishlistSearchData(String tmdbID){
        SQLiteDatabase db = this.getReadableDatabase(); // Gets database instance

        String Query = "Select * from " + WishlistContract.WishlistEntry.TABLE_NAME + " where " + WishlistContract.WishlistEntry.MOVIE_ID + " = " + tmdbID;
        Cursor cursor = db.rawQuery(Query, null);
        if(cursor!=null) {
            if(cursor.getCount()>0) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }

    /**
     * Same as wishlistSearchData, but this one returns the datatable itself instead of just true or falce
     * @param id themovieDB ID
     * @return Cursor with table data
     */
    public Cursor wishlistSearchDataCursor(String id){
        SQLiteDatabase db = this.getReadableDatabase(); // Gets database instance

        String Query = "Select * from " + WishlistContract.WishlistEntry.TABLE_NAME + " where " + WishlistContract.WishlistEntry.MOVIE_ID + " = " + id;
        Cursor cursor = db.rawQuery(Query, null);
        return cursor;
    }

    // Watched methods, --EXACT SAME AS WISHLIST METHODS, just for watched tables.
    public boolean watchedInsertData(int id, String name){
        SQLiteDatabase db = this.getReadableDatabase(); // Gets database instance
        ContentValues contentValues = new ContentValues(); // Create contentValue instance for storing table
        contentValues.put(WishlistContract.WatchedlistEntry.MOVIE_ID, id); // Add a table row to Movie_ID with the int id
        contentValues.put(WishlistContract.WatchedlistEntry.MOVIE_NAME, name); // Add a table row to MOVIE_NAME with the string name
        long result = db.insert(WishlistContract.WatchedlistEntry.TABLE_NAME, null, contentValues); // Inserts into database, returns -1 if it fails
        if(result == -1)
            return false;
        else
            return true;
    }

    public Cursor watchedGetAllData(){
        SQLiteDatabase db = this.getReadableDatabase(); // Gets database instance
        Cursor res = db.rawQuery("select * from " + WishlistContract.WatchedlistEntry.TABLE_NAME, null);
        return res;
    }

    public Integer watchedDeleteData(String id){
        SQLiteDatabase db = this.getReadableDatabase(); // Gets database instance
        return db.delete(WishlistContract.WatchedlistEntry.TABLE_NAME, WishlistContract.WatchedlistEntry._ID +" = ?", new String[]{id});
    }

    public boolean watchedSearchData(String id){
        SQLiteDatabase db = this.getReadableDatabase(); // Gets database instance

        String Query = "Select * from " + WishlistContract.WatchedlistEntry.TABLE_NAME + " where " + WishlistContract.WatchedlistEntry.MOVIE_ID + " = " + id;
        Cursor cursor = db.rawQuery(Query, null);
        if(cursor!=null) {
            if(cursor.getCount()>0) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }

    public Cursor watchedSearchDataCursor(String id){
        SQLiteDatabase db = this.getReadableDatabase(); // Gets database instance

        String Query = "Select * from " + WishlistContract.WatchedlistEntry.TABLE_NAME + " where " + WishlistContract.WatchedlistEntry.MOVIE_ID + " = " + id;
        Cursor cursor = db.rawQuery(Query, null);
        return cursor;
    }
}
