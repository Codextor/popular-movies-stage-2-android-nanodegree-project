package com.example.android.popularmoviesstage2.utilities;

import android.content.Context;
import android.net.Uri;

import com.example.android.popularmoviesstage2.BuildConfig;
import com.example.android.popularmoviesstage2.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * These utilities will be used to communicate with the movie database servers.
 */
public final class NetworkUtils {

    private final static String API_KEY_PARAM = "api_key";

    /*
     * Base url for The Movie Database api endpoints
     */
    private static final String BASE_URL = "https://api.themoviedb.org/3";

    /*
     * Second part of the api endpoint for getting a list
     * of current popular movies
     */
    private static final String POPULAR_MOVIES = "movie/popular";

    /*
     * Second part of the api endpoint for getting a list
     * of the top rated movies
     */
    private static final String TOP_RATED_MOVIES = "movie/top_rated";

    /**
     * Builds the URL used to talk to the movie database server.
     *
     * @param sortOrder Flag to determine the sort order.
     * @param context   Context of the calling activity.
     * @return The URL to use to query the movie database server.
     */
    public static URL buildUrl(String sortOrder, Context context) {
        Uri builtUri;

        /*
         * API key accessing The Movie Database api endpoints
         */
        final String API_KEY = BuildConfig.THE_MOVIE_DATABASE_API_KEY;

        if (sortOrder.equals(context.getResources().getString(R.string.most_popular_key))) {
            builtUri = Uri.parse(BASE_URL).buildUpon().appendEncodedPath(POPULAR_MOVIES).appendQueryParameter(API_KEY_PARAM, API_KEY).build();
        } else if (sortOrder.equals(context.getResources().getString(R.string.top_rated_key))) {
            builtUri = Uri.parse(BASE_URL).buildUpon().appendEncodedPath(TOP_RATED_MOVIES).appendQueryParameter(API_KEY_PARAM, API_KEY).build();
        } else
            return null;
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
