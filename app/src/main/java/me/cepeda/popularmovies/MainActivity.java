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

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.cepeda.popularmovies.adapters.MoviesAdapter;
import me.cepeda.popularmovies.models.Movie;
import me.cepeda.popularmovies.models.MoviesData;
import me.cepeda.popularmovies.utils.TMDbService;
import me.cepeda.popularmovies.utils.TMDbUtils;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.MoviesAdapterOnClickHandler {

    private final static int NUM_COLUMNS_PORTRAIT = 3;
    private final static int NUM_COLUMNS_LANDSCAPE = 5;

    @BindView(R.id.rv_grid_movies) RecyclerView mGridMoviesRecyclerView;
    @BindView(R.id.pb_loading_indicator) ProgressBar mLoadingIndicatorProgressBar;
    @BindView(R.id.tv_error_message_display) TextView mErrorMessageTextView;

    MoviesAdapter mMoviesAdapter;

    Observable<MoviesData> mPopularMoviesObservable;
    Observable<MoviesData> mTopRatedMoviesObservable;
    Disposable mDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

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

        Retrofit retrofit = TMDbUtils.getRetrofit();
        TMDbService service = retrofit.create(TMDbService.class);

        mPopularMoviesObservable = service.getPopularMovieData().cache();
        mTopRatedMoviesObservable = service.getTopRatedMovieData().cache();

        loadPopularMoviesData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDisposable.dispose();
    }

    private void subscribe(Observable<MoviesData> observable) {
        mDisposable = observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> mLoadingIndicatorProgressBar.setVisibility(View.VISIBLE))
                .doOnError(throwable -> showErrorMessageView())
                .subscribe(moviesData -> {
                    showMoviesDataView();
                    mMoviesAdapter.setMovies(moviesData.getMovies());
                });
    }

    private void loadPopularMoviesData() {
        subscribe(mPopularMoviesObservable);
    }

    private void loadTopRatedMoviesData() {
        subscribe(mTopRatedMoviesObservable);
    }

    private void showMoviesDataView() {
        mLoadingIndicatorProgressBar.setVisibility(View.INVISIBLE);
        mErrorMessageTextView.setVisibility(View.INVISIBLE);
        mGridMoviesRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessageView() {
        mLoadingIndicatorProgressBar.setVisibility(View.INVISIBLE);
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
