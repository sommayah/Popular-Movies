package com.example.sommayahsoliman.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    ArrayList<MovieItem> movieItems;
    ImageAdapter adapter;
    SharedPreferences.OnSharedPreferenceChangeListener listener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(getActivity());

        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                updateMovies();
            }
        };

        prefs.registerOnSharedPreferenceChangeListener(listener);


        if(savedInstanceState != null && savedInstanceState.containsKey("movies")) {
             movieItems = savedInstanceState.getParcelableArrayList("movies");
        }else{
            //movieItems = new ArrayList<MovieItem>();
            updateMovies();
        }
        //to listen to setting changes and fetch new data when changed


    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        prefs.unregisterOnSharedPreferenceChangeListener(listener);
    }

    public MainActivityFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("movies", movieItems);
        super.onSaveInstanceState(outState);
    }


    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    void updateMovies(){
        //strings: vote_average.desc, popularity.desc
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sort_by = sharedPref.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_default));
       // Log.v(LOG_TAG,"sort by: "+ sort_by);
        if(OnlineUtils.isOnline(getActivity()) == false){
            Toast.makeText(getActivity(), "no internet connection",
                    Toast.LENGTH_SHORT).show();
        }else {
            new FetchMovieTask().execute(sort_by);

        }
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        GridView gridview = (GridView) rootView.findViewById(R.id.gridView_movies);
        adapter = new ImageAdapter(getActivity());
        if(movieItems !=null){
            adapter.add(movieItems);
        }
        gridview.setAdapter(adapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                // Toast.makeText(getActivity(), "" + adapter.getItem(position).name,
                //         Toast.LENGTH_SHORT).show();
                Intent detailIntent = new Intent(getActivity(), DetailActivity.class);
                MovieItem movieItem = adapter.getItem(position);
                detailIntent.putExtra("title", movieItem.getName());
                detailIntent.putExtra("path", movieItem.getPath());
                detailIntent.putExtra("release_date", movieItem.getReleaseDate());
                detailIntent.putExtra("vote", movieItem.getVote());
                detailIntent.putExtra("overview", movieItem.getOverView());
                startActivity(detailIntent);
            }
        });

        return rootView;
    }


    public class FetchMovieTask extends AsyncTask<String, Void, ArrayList<MovieItem>> {

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();



        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         *
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private ArrayList<MovieItem> getMovieDataFromJson(String moviesJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String MOVIE_LIST = "results";
            final String MOVIE_TITLE = "original_title";
            final String MOVIE_PATH = "poster_path";
            final String MOVIE_OVERVIEW = "overview";
            final String MOVIE_RELEASE_DATE = "release_date";
            final String MOVIE_VOTE = "vote_average";


            JSONObject movieJson = new JSONObject(moviesJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(MOVIE_LIST);
            //movie title and movie poster path FOR LOG PURPOSES ONLY
            String[] resultTitles = new String[movieArray.length()];
            String[] resultPaths = new String[movieArray.length()];
            movieItems = new ArrayList<MovieItem>();

            for(int i = 0; i < movieArray.length(); i++) {
                String movie_title;
                String movie_path;
                String movie_overview;
                double movie_vote;
                String movie_date;
                // Get the JSON object representing the movie title and path
                JSONObject movie = movieArray.getJSONObject(i);
                movie_title = movie.getString(MOVIE_TITLE);
                movie_path = movie.getString(MOVIE_PATH);
                movie_overview=movie.getString(MOVIE_OVERVIEW);
                movie_vote = movie.getDouble(MOVIE_VOTE);
                movie_date = movie.getString(MOVIE_RELEASE_DATE);
                resultTitles[i] = movie_title;
                resultPaths[i] = movie_path;

                movieItems.add(new MovieItem(movie_title,movie_path,movie_date,movie_vote,movie_overview));
            }


            for (String s : resultTitles) {
                 //  Log.v(LOG_TAG, "movie entry: " + s);
            }
            for (String s : resultPaths) {

               // Log.v(LOG_TAG,"movie path: " + s);
            }
            return movieItems;

        }

        @Override
        protected ArrayList<MovieItem> doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String movieJsonStr = null;

            String format = "json";
            String api = ApiKey.API_KEY;


            try {

                // Construct the URL for the Movie query

                // themoviedb.org


                final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
                final String SORT_PARAM = "sort_by";
                final String API_PARAM = "api_key";
                //URL url = new URL("http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=[YOUR API KEY]");
                Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_PARAM,params[0])
                        .appendQueryParameter(API_PARAM, api)
                        .build();

                URL url = new URL(builtUri.toString());
                 Log.v(LOG_TAG,"Built uri "+ builtUri.toString());

                // Create the request to themoviedb, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                movieJsonStr = buffer.toString();

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;

            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                Log.v(LOG_TAG,movieJsonStr);
                return getMovieDataFromJson(movieJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<MovieItem> movieList) {
            if (movieList != null) {
                adapter.clear();
                adapter.add(movieList);
                adapter.notifyDataSetChanged();
                super.onPostExecute(movieList);
            }
        }
    }

}
