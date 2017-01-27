package me.cepeda.popularmovies;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.URL;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.cepeda.popularmovies.adapters.MoviesAdapter;
import me.cepeda.popularmovies.models.Movie;
import me.cepeda.popularmovies.utils.NetworkUtils;
import me.cepeda.popularmovies.utils.ParseJsonUtils;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.MoviesAdapterOnClickHandler {

    private final static int NUM_COLUMNS_PORTRAIT = 3;
    private final static int NUM_COLUMNS_LANDSCAPE = 5;

    RecyclerView mGridMoviesRecyclerView;
    MoviesAdapter mMoviesAdapter;

    ProgressBar mLoadingIndicatorProgressBar;
    TextView mErrorMessageTextView;

    Observable<Movie[]> mPopularMoviesObservable;
    Observable<Movie[]> mTopRatedMoviesObservable;
    Observer<Movie[]> mObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGridMoviesRecyclerView = (RecyclerView) findViewById(R.id.rv_grid_movies);
        mLoadingIndicatorProgressBar = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        mErrorMessageTextView = (TextView) findViewById(R.id.tv_error_message_display);

        GridLayoutManager layoutManager;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutManager = new GridLayoutManager(this, NUM_COLUMNS_PORTRAIT);
        } else {
            layoutManager = new GridLayoutManager(this, NUM_COLUMNS_LANDSCAPE);
        }

        mGridMoviesRecyclerView.setLayoutManager(layoutManager);
        mGridMoviesRecyclerView.setHasFixedSize(true);

        mMoviesAdapter = new MoviesAdapter(this);
        mGridMoviesRecyclerView.setAdapter(mMoviesAdapter);

        mPopularMoviesObservable = createObservable(NetworkUtils.buildPopularMoviesURL());
        mTopRatedMoviesObservable = createObservable(NetworkUtils.buildTopRatedMoviesURL());
        mObserver = createObserver();

        loadPopularMoviesData();
    }

    private void loadPopularMoviesData() {
        mPopularMoviesObservable.subscribe(mObserver);
    }

    private void loadTopRatedMoviesData() {
        mTopRatedMoviesObservable.subscribe(mObserver);
    }

    private Observable<Movie[]> createObservable(final URL requestUrl) {
        return Observable.fromCallable(new Callable<Movie[]>() {
            @Override
            public Movie[] call() throws Exception {
                String jsonResponse = NetworkUtils.getResponseFromHttpUrl(requestUrl);
                return ParseJsonUtils.getMoviesFromJson(jsonResponse);
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Observer<Movie[]> createObserver() {
        return new Observer<Movie[]>() {
            @Override
            public void onSubscribe(Disposable d) {
                mLoadingIndicatorProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNext(Movie[] movies) {
                if (movies != null) {
                    showMoviesDataView();
                    mMoviesAdapter.setMovies(movies);
                } else {
                    showErrorMessageView();
                }
            }

            @Override
            public void onError(Throwable t) {
                mLoadingIndicatorProgressBar.setVisibility(View.INVISIBLE);
                showErrorMessageView();
            }

            @Override
            public void onComplete() {
                mLoadingIndicatorProgressBar.setVisibility(View.INVISIBLE);
            }
        };
    }

    private void showMoviesDataView() {
        mErrorMessageTextView.setVisibility(View.INVISIBLE);
        mGridMoviesRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessageView() {
        mGridMoviesRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
            case R.id.menu_sort_popular:
                loadPopularMoviesData();
                return true;
            case R.id.menu_sort_rating:
                loadTopRatedMoviesData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(Movie movie) {
        Intent intent = new Intent(this, MovieInformationActivity.class);
        intent.putExtra(Intent.EXTRA_INTENT, movie);
        startActivity(intent);
    }

}
