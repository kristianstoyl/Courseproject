package com.ntnu.kristian.courseproject;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Kristian on 13.04.2016.
 */
public class AndroidFlavor implements Parcelable {
    String versionName;
    String posterNumber;
    int image; // drawable reference id
    int id;
    String releaseDate;
    String overView;

    String trailerKey; //youtube link for trailer, e.g: youtube.com/watch?v=<trailerKey>


    public AndroidFlavor(int id, String vName, String vNumber, int image, String releaseDate, String overView)
    {
        this.id = id;
        this.versionName = vName;
        this.posterNumber = vNumber;
        this.image = image;
        this.releaseDate = releaseDate;
        this.overView = overView;
    }

    // Parcel part
    protected AndroidFlavor(Parcel in) {
        String[] data = new String[6];
        in.readStringArray(data);
        this.id = Integer.parseInt(data[0]);
        this.versionName = data[1];
        this.posterNumber = data[2];
        this.image = Integer.parseInt(data[3]);
        this.releaseDate = data[4];
        this.overView = data[5];
    }

    public static final Creator<AndroidFlavor> CREATOR = new Creator<AndroidFlavor>() {
        @Override
        public AndroidFlavor createFromParcel(Parcel in) {
            return new AndroidFlavor(in);
        }

        @Override
        public AndroidFlavor[] newArray(int size) {
            return new AndroidFlavor[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeStringArray(new String[]{String.valueOf(this.id), this.versionName, this.posterNumber
                , String.valueOf(this.image), this.releaseDate, this.overView});
    }
}