package com.example.sommayahsoliman.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sommayahsoliman on 8/10/15.
 */
public class MovieItem implements Parcelable{
    public final String name;
    public final String path;
    public final String releaseDate;
    public final String overView;
    public final long vote;

    MovieItem(String name, String path, String releaseDate, long vote, String overView){
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
        vote = in.readInt();
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
        dest.writeLong(vote);
        dest.writeString(overView);

    }
    public final Parcelable.Creator<MovieItem> CREATOR = new Parcelable.Creator<MovieItem>() {
        @Override
        public MovieItem createFromParcel(Parcel parcel) {
            return new MovieItem(parcel);
        }

        @Override
        public MovieItem[] newArray(int i) {
            return new MovieItem[i];
        }

    };
}
