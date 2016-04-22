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
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "wishlist.db";


    public WishlistDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create tables, what should I include?
        final String SQL_CREATE_WISHLIST_TABLE = "CREATE TABLE " + WishlistContract.WishlistEntry.TABLE_NAME+ " (" +
                WishlistContract.WishlistEntry._ID + " INTEGER PRIMARY KEY," +
                WishlistContract.WishlistEntry.MOVIE_ID + " INTEGER UNIQUE NOT NULL," +
                WishlistContract.WishlistEntry.MOVIE_NAME + " TEXT NOT NULL" +
                " );";
        // Creates table
        db.execSQL(SQL_CREATE_WISHLIST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + WishlistContract.WishlistEntry.TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(int id, String name){
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

    public Cursor getAllData(){
        SQLiteDatabase db = this.getReadableDatabase(); // Gets database instance
        Cursor res = db.rawQuery("select * from " + WishlistContract.WishlistEntry.TABLE_NAME, null);
        return res;
    }

    public Integer deleteData(String id){
        SQLiteDatabase db = this.getReadableDatabase(); // Gets database instance
        return db.delete(WishlistContract.WishlistEntry.TABLE_NAME, WishlistContract.WishlistEntry._ID +" = ?", new String[]{id});
    }
}
