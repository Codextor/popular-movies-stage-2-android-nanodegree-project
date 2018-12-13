package com.example.android.popularmoviesstage2.utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Utility functions to handle the movie database JSON data.
 */
public class JsonUtils {

    /**
     * This method parses JSON from a web response and returns an array of Strings
     * describing the movies sorted by either most popular or top rated.
     *
     * @param jsonMovieResponse JSON response from server
     * @return Array of Strings describing movies
     * @throws JSONException If JSON data cannot be properly parsed
     */
    public static String[] getMoviePostersFromJson(String jsonMovieResponse)
            throws JSONException {

        /* Movie information. Each movie's info is an element of the "results" array */
        final String OWN_RESULT = "results";

        /* Path for poster of the movie */
        final String POSTER_PATH = "poster_path";
        final String STATUS_CODE = "status_code";

        final String BASE_URL = "http://image.tmdb.org/t/p/";
        final String SIZE = "w185";

        /* String array to hold each movie */
        String[] parsedMoviesData;

        JSONObject movieJson = new JSONObject(jsonMovieResponse);

        /* Is there an error? */
        if (movieJson.has(STATUS_CODE)) {
            int errorCode = movieJson.getInt(STATUS_CODE);

            switch (errorCode) {
                case 7:
                    /* Invalid API key */
                    return null;
                case 34:
                    /* Resource not found */
                    return null;
                default:
                    /* Server probably down */
                    return null;
            }
        }

        JSONArray movieArray = movieJson.getJSONArray(OWN_RESULT);

        parsedMoviesData = new String[movieArray.length()];

        for (int i = 0; i < movieArray.length(); i++) {

            String poster_path;

            /* Get the JSON object representing the movie */
            JSONObject movieObject = movieArray.getJSONObject(i);

            poster_path = movieObject.getString(POSTER_PATH);

            parsedMoviesData[i] = BASE_URL + SIZE + poster_path;
        }

        return parsedMoviesData;
    }

    public static JSONObject getMovieDetailsFromJson(String jsonMovieResponse, int position)
            throws JSONException {

        final String OWN_RESULT = "results";

        JSONObject moviesJson = new JSONObject(jsonMovieResponse);

        JSONArray moviesArray = moviesJson.getJSONArray(OWN_RESULT);

        /* Get the JSON object representing the movie */
        JSONObject movieObject = moviesArray.getJSONObject(position);

        return movieObject;
    }
}
