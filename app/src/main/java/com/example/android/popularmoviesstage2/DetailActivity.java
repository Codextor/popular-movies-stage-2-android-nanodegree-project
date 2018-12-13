package com.example.android.popularmoviesstage2;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.example.android.popularmoviesstage2.data.MovieContract;
import com.example.android.popularmoviesstage2.data.MovieDbHelper;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import static com.example.android.popularmoviesstage2.MainActivity.jsonMovieResponse;
import static com.example.android.popularmoviesstage2.MainActivity.simpleJsonMovieData;
import static com.example.android.popularmoviesstage2.utilities.JsonUtils.getMovieDetailsFromJson;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = DetailActivity.class.getSimpleName();

    private TextView mTitleText;
    private ImageView mPosterImage;
    private TextView mReleaseText;
    private TextView mRatingText;
    private TextView mSynopsisText;

    private JSONObject movieDetails;

    private int movieId;
    private String posterForThisMovie;
    private String releaseYear;
    private String rating;

    private SQLiteDatabase mDb;
    private Switch addToFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mTitleText = findViewById(R.id.tv_title);
        mPosterImage = findViewById(R.id.iv_poster);
        mReleaseText = findViewById(R.id.tv_release);
        mRatingText = findViewById(R.id.tv_rating);
        mSynopsisText = findViewById(R.id.tv_synopsis);

        addToFavorite = findViewById(R.id.switch_favorite);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int posterWidth = (int) (width / 2.4);

        MovieDbHelper dbHelper = new MovieDbHelper(this);
        mDb = dbHelper.getWritableDatabase();

        Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {

            int position = intentThatStartedThisActivity.getIntExtra(Intent.EXTRA_TEXT, 0);

            try {
                movieDetails = getMovieDetailsFromJson(jsonMovieResponse, position);
                mTitleText.setText(movieDetails.getString("title"));

                posterForThisMovie = simpleJsonMovieData[position];
                Picasso.get().load(posterForThisMovie).resize(posterWidth, 0).into(mPosterImage);

                releaseYear = movieDetails.getString("release_date").substring(0, 4);
                mReleaseText.setText(releaseYear);

                rating = movieDetails.getString("vote_average") + "/10";
                mRatingText.setText(rating);

                mSynopsisText.setText(movieDetails.getString("overview"));

                movieId = movieDetails.getInt("id");

                if (isFavorite(movieId)) {
                    Log.d(TAG, "movie is already in favorites");
                    addToFavorite.setChecked(true);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        addToFavorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    Log.d(TAG, "adding to favorites");
                    addToFavorite();
                } else {
                    // The toggle is disabled
                    Log.d(TAG, "removing from favorites");
                    removeFromFavorite();
                }
            }
        });

    }

    public boolean isFavorite(int id) {
        String selectString = "SELECT * FROM " + MovieContract.MovieEntry.TABLE_NAME + " WHERE " + MovieContract.MovieEntry.COLUMN_ID + " = " + id;
        Cursor cursor = mDb.rawQuery(selectString, null);

        boolean hasObject = false;
        if(cursor.moveToFirst()){
            hasObject = true;
        }

        cursor.close();
        return hasObject;
    }

    /**
     * This method is called when user toggles the switch to Add to favorite
     */
    public void addToFavorite() {

        // Add movie info to mDb
        addNewMovie(movieId, rating, mTitleText.getText().toString(), posterForThisMovie, mSynopsisText.getText().toString(), releaseYear);

    }

    /**
     * This method is called when user toggles the switch to Remove from favorite
     */
    public void removeFromFavorite() {

        // Remove movie info from mDb
        removeMovie(movieId);

    }

    /**
     * Adds a new movie to the mDb including movie details
     *
     * @param id           Movie's id in The Movie Database API
     * @param vote_average Movie's rating
     * @param title        Movie's name
     * @param poster_path  URL for movie's poster
     * @param overview     Movie's synopsis
     * @param release_date Movie's release year
     */
    private void addNewMovie(int id, String vote_average, String title, String poster_path, String overview, String release_date) {

        ContentValues cv = new ContentValues();

        cv.put(MovieContract.MovieEntry.COLUMN_ID, id);
        cv.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, vote_average);
        cv.put(MovieContract.MovieEntry.COLUMN_TITLE, title);
        cv.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, poster_path);
        cv.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, overview);
        cv.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, release_date);

        Uri uri = getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, cv);

    }

    /**
     * Removes the movie with the specified id
     *
     * @param id           Movie's id in The Movie Database API
     * @return True: if removed successfully, False: if failed
     */
    private boolean removeMovie(int id) {

        return getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI, MovieContract.MovieEntry.COLUMN_ID + " = ?", new String[]{Integer.toString(id)}) > 0;

    }
}
