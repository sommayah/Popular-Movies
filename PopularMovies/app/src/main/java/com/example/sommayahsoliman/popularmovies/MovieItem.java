package com.example.sommayahsoliman.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sommayahsoliman on 8/10/15.
 */
public class MovieItem implements Parcelable{
    private final String name;
    private final String path;
    private final String releaseDate;
    private final String overView;
    private final double vote;

    MovieItem(String name, String path, String releaseDate, double vote, String overView){
        this.name = name;
        this.path = path;
        this.releaseDate = releaseDate;
        this.vote = vote;
        this.overView = overView;
    }


    private MovieItem(Parcel in){
        name = in.readString();
        path = in.readString();
        releaseDate = in.readString();
        vote = in.readDouble();
        overView=in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(path);
        dest.writeString(releaseDate);
        dest.writeDouble(vote);
        dest.writeString(overView);

    }
    public static final Parcelable.Creator<MovieItem> CREATOR = new Parcelable.Creator<MovieItem>() {
        @Override
        public MovieItem createFromParcel(Parcel parcel) {
            return new MovieItem(parcel);
        }

        @Override
        public MovieItem[] newArray(int i) {
            return new MovieItem[i];
        }

    };

    public String getName(){
        return name;
    }
    public String getPath(){
        return path;
    }
    public double getVote(){
        return vote;
    }
    public String getOverView(){
        return overView;
    }
    public String getReleaseDate(){
        return releaseDate;
    }
}
