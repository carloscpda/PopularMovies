package me.cepeda.popularmovies.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.cepeda.popularmovies.R;
import me.cepeda.popularmovies.adapters.MoviesAdapter;
import me.cepeda.popularmovies.adapters.SortedSectionsPagerAdapter;
import me.cepeda.popularmovies.data.FavouriteMoviesContract.FavouriteMoviesEntry;
import me.cepeda.popularmovies.services.ObservablesService;
import me.cepeda.popularmovies.models.Movie;
import me.cepeda.popularmovies.models.MoviesData;

public class MainFragment extends Fragment implements MoviesAdapter.MoviesAdapterOnClickHandler {

    private final static int NUM_COLUMNS_PORTRAIT = 3;
    private final static int NUM_COLUMNS_LANDSCAPE = 5;

    private final static String TAG_POSITION = "position";

    @BindView(R.id.rv_grid_movies) RecyclerView mGridMoviesRecyclerView;
    @BindView(R.id.pb_loading_indicator) ProgressBar mLoadingIndicatorProgressBar;
    @BindView(R.id.tv_error_message_display) TextView mErrorMessageTextView;

    private final CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private GridLayoutManager mGridLayoutManager;
    private MoviesAdapter mMoviesAdapter;

    private int mTabPosition;
    private int mScrollPosition = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        mTabPosition = bundle.getInt(SortedSectionsPagerAdapter.KEY);

        if (savedInstanceState != null)
            mScrollPosition = savedInstanceState.getInt(TAG_POSITION);

        mMoviesAdapter = new MoviesAdapter(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ButterKnife.bind(this, rootView);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            mGridLayoutManager = new GridLayoutManager(getContext(), NUM_COLUMNS_PORTRAIT);
        else mGridLayoutManager = new GridLayoutManager(getContext(), NUM_COLUMNS_LANDSCAPE);

        mGridMoviesRecyclerView.setLayoutManager(mGridLayoutManager);
        mGridMoviesRecyclerView.setHasFixedSize(true);
        mGridMoviesRecyclerView.setAdapter(mMoviesAdapter);

        mLoadingIndicatorProgressBar.getIndeterminateDrawable().setColorFilter(
                ContextCompat.getColor(getContext(), R.color.colorPrimary),
                PorterDuff.Mode.MULTIPLY
        );

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        switch (mTabPosition) {
            case 0:
                subscribe(ObservablesService.getInstance().getPopularMoviesObservable());
                break;
            case 1:
                subscribe(ObservablesService.getInstance().getTopRatedMoviesObservable());
                break;
            case 2:
                subscribe();
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.dispose();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mGridLayoutManager != null) {
            int position = mGridLayoutManager.findFirstVisibleItemPosition();
            outState.putInt(TAG_POSITION, position);
        }
        super.onSaveInstanceState(outState);
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

    private void subscribe(Observable<MoviesData> observable) {
        mCompositeDisposable.add(
                observable.subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnError(throwable -> showErrorMessageView())
                        .subscribe(moviesData -> {
                            mMoviesAdapter.setMovies(moviesData.getMovies());
                            mGridLayoutManager.scrollToPosition(mScrollPosition);
                            showMoviesDataView();
                        })
        );
    }

    private void subscribe() {
        Disposable subscribe = Observable.fromIterable(getIDs())
                .flatMap(integer -> ObservablesService.getInstance().getMovieObservable(integer))
                .toList()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> mLoadingIndicatorProgressBar.setVisibility(View.VISIBLE))
                .doOnError(throwable -> showErrorMessageView())
                .subscribe(movies -> {
                    mMoviesAdapter.setMovies(movies);
                    mGridLayoutManager.scrollToPosition(mScrollPosition);
                    showMoviesDataView();
                });
        mCompositeDisposable.add(subscribe);
    }

    private List<Integer> getIDs() {
        Cursor cursor = getContext().getContentResolver().query(FavouriteMoviesEntry.CONTENT_URI,
                new String[]{ FavouriteMoviesEntry.COLUMN_TMDB_ID },
                null, null, FavouriteMoviesEntry.COLUMN_TMDB_ID);

        List<Integer> ids = new ArrayList<>();
        if (cursor != null) {
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);
                ids.add(i, cursor.getInt(cursor.getColumnIndex(FavouriteMoviesEntry.COLUMN_TMDB_ID)));
            }
            cursor.close();
        }
        return ids;
    }

    @Override
    public void onClick(Movie movie) {
        Intent intent = new Intent(getContext(), MovieDetailsActivity.class);
        intent.putExtra(Intent.EXTRA_INTENT, movie);
        startActivity(intent);
    }
}
