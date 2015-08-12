package com.example.sommayahsoliman.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    private final String IMAGE_SIZE = "w342";
    private final String RELEASE_DATE = "Release Date: ";
    private final String VOTE = "Vote: ";
    private final String BASE_URL = "http://image.tmdb.org/t/p/";
    private String name;
    private String path;
    private String release_date;
    private long vote;
    private String overview;

    public DetailActivityFragment() {
        setHasOptionsMenu(false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        Intent intent = getActivity().getIntent();
        if(intent != null && intent.hasExtra("title")){
            name = intent.getStringExtra("title");
            path = intent.getStringExtra("path");
            release_date = intent.getStringExtra("release_date");
            vote = intent.getLongExtra("vote", 0);
            overview = intent.getStringExtra("overview");

            TextView textView = (TextView)rootView.findViewById(R.id.textViewTitle);
            textView.setText(name);
            TextView dateTextView = (TextView)rootView.findViewById(R.id.textViewDate);
            dateTextView.setText(RELEASE_DATE+release_date);
            TextView voteTextView = (TextView)rootView.findViewById(R.id.textViewVote);
            voteTextView.setText(VOTE + String.valueOf(vote));
            TextView overviewTextView = (TextView)rootView.findViewById(R.id.textViewOverView);
            overviewTextView.setText(overview);
           // overviewTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
            ImageView imageView = (ImageView)rootView.findViewById(R.id.imageView);
            if(isOnline() == false){
                Toast.makeText(getActivity(), "no internet connection",
                        Toast.LENGTH_SHORT).show();
            }else {
                new DownloadImageTask(imageView)
                        .execute(BASE_URL + IMAGE_SIZE + path);
            }


        }
        return rootView;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {

                String urldisplay = urls[0];
                Bitmap mIcon11 = null;
                try {
                    InputStream in = new java.net.URL(urldisplay).openStream();
                    mIcon11 = BitmapFactory.decodeStream(in);
                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                    e.printStackTrace();
                }
                return mIcon11;

        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }

    }
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

}
