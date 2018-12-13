package com.example.android.popularmoviesstage2;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmoviesstage2.data.MovieContract;
import com.example.android.popularmoviesstage2.utilities.JsonUtils;
import com.example.android.popularmoviesstage2.utilities.NetworkUtils;

import java.net.URL;

public class MainActivity extends AppCompatActivity implements PosterAdapter.ListItemClickListener, LoaderCallbacks<String[]> {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String LIFECYCLE_CALLBACKS_TEXT_KEY = "callbacks";

    static String jsonMovieResponse;
    static String[] simpleJsonMovieData;

    private RecyclerView mRecyclerView;
    private PosterAdapter mPosterAdapter;

    private String sortOrder;

    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;

    private static final int MOVIE_LOADER_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.recyclerview_posters);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mPosterAdapter = new PosterAdapter(this);
        mRecyclerView.setAdapter(mPosterAdapter);

        sortOrder = getString(R.string.most_popular_key);

        mErrorMessageDisplay = findViewById(R.id.tv_error_message_display);
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(LIFECYCLE_CALLBACKS_TEXT_KEY)) {
                String savedSortOrder = savedInstanceState
                        .getString(LIFECYCLE_CALLBACKS_TEXT_KEY);
                sortOrder = savedSortOrder;
            }
        }

        int loaderId = MOVIE_LOADER_ID;
        LoaderCallbacks<String[]> callback = MainActivity.this;
        Bundle bundleForLoader = null;
        getSupportLoaderManager().initLoader(loaderId, bundleForLoader, callback);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        String savedSortOrder = sortOrder;
        outState.putString(LIFECYCLE_CALLBACKS_TEXT_KEY, savedSortOrder);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuItemThatWasSelected = item.getItemId();

        if (menuItemThatWasSelected == R.id.action_most_popular) {
            sortOrder = getString(R.string.most_popular_key);
            invalidateData();
            getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);
        } else if (menuItemThatWasSelected == R.id.action_top_rated) {
            sortOrder = getString(R.string.top_rated_key);
            invalidateData();
            getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);
        } else if (menuItemThatWasSelected == R.id.action_favorites) {
            sortOrder = getString(R.string.favorites_key);
            invalidateData();
            getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);
        }
        return true;
    }

    private void invalidateData() {
        mPosterAdapter.setMovieData(null);
    }

    private void showMovieDataView() {
        /* First, make sure the error is invisible */
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, make sure the movie data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        /* First, hide the currently visible data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    @Override
    public Loader<String[]> onCreateLoader(final int id, final Bundle args) {
        return new AsyncTaskLoader<String[]>(this) {

            String[] cachedMoviesData;

            @Override
            protected void onStartLoading() {
                /*
                 * If we already have cached results, just deliver them now. If we don't have any
                 * cached results, force a load.
                 */
                if (cachedMoviesData != null) {
                    Log.d(TAG, "cached movies data is not null");
                    deliverResult(cachedMoviesData);
                } else {
                    Log.d(TAG, "cached movies data is null");
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }

            @Override
            public String[] loadInBackground() {

                if (sortOrder == null || TextUtils.isEmpty(sortOrder)) {
                    return null;
                }

                if (sortOrder == getString(R.string.favorites_key)) {
                    Cursor cursor = getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI, new String[]{MovieContract.MovieEntry.COLUMN_POSTER_PATH}, null, null, null);

                    if(cursor != null){
                        int count = cursor.getCount();
                        if (count > 0) {
                            int titleIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH);
                            simpleJsonMovieData = new String[count];

                            int i = 0;
                            while (cursor.moveToNext()) {
                                simpleJsonMovieData[i] = cursor.getString(titleIndex);
                            }
                        } else {
                            simpleJsonMovieData = new String[0];
                        }
                        cursor.close();
                    }
                    return simpleJsonMovieData;
                }

                URL movieRequestUrl = NetworkUtils.buildUrl(sortOrder, MainActivity.this);
                Log.d(TAG, "Movie Request URL: " + movieRequestUrl);

                try {
                    jsonMovieResponse = NetworkUtils.getResponseFromHttpUrl(movieRequestUrl);

                    simpleJsonMovieData = JsonUtils.getMoviePostersFromJson(jsonMovieResponse);

                    return simpleJsonMovieData;

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void deliverResult(String[] data) {
                cachedMoviesData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String[]> loader, String[] data) {

        mLoadingIndicator.setVisibility(View.INVISIBLE);
        if (data != null) {
            showMovieDataView();

            mPosterAdapter.setMovieData(data);
        } else {
            showErrorMessage();
        }
    }

    @Override
    public void onLoaderReset(Loader<String[]> loader) {
        /*
         * We aren't using this method in our application, but we are required to Override
         * it to implement the LoaderCallbacks<String> interface
         */
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        Log.d("PosterAdapter", "onClick of: " + clickedItemIndex);

        Class destinationActivity = DetailActivity.class;

        Context context = MainActivity.this;
        Intent intent = new Intent(context, destinationActivity);
        intent.putExtra(Intent.EXTRA_TEXT, clickedItemIndex);

        startActivity(intent);
    }
}
