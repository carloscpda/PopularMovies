package me.cepeda.popularmovies.ui;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.cepeda.popularmovies.R;
import me.cepeda.popularmovies.adapters.ReviewsAdapter;
import me.cepeda.popularmovies.models.Movie;
import me.cepeda.popularmovies.models.ReviewsData;
import me.cepeda.popularmovies.services.ObservablesService;

public class MovieReviewsFragment extends Fragment {

    private static final String TAG_POSITION = "position";

    @BindView(R.id.rv_reviews) RecyclerView mRecyclerView;

    private LinearLayoutManager mLinearLayoutManager;
    private ReviewsAdapter mReviewsAdapter;
    private Disposable mDisposable;
    private Movie mMovie;
    private int mScrollPosition = 0;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getActivity().getIntent();
        mMovie = intent.getParcelableExtra(Intent.EXTRA_INTENT);
        if (savedInstanceState != null) mScrollPosition = savedInstanceState.getInt(TAG_POSITION);

        mReviewsAdapter = new ReviewsAdapter();
        loadReviewsData();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_reviews, container, false);

        ButterKnife.bind(this, rootView);

        mLinearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mReviewsAdapter);

        return rootView;
    }

    @Override
    public void onDestroy() {
        mDisposable.dispose();
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        int position = mLinearLayoutManager.findFirstVisibleItemPosition();
        outState.putInt(TAG_POSITION, position);
        super.onSaveInstanceState(outState);
    }

    private void loadReviewsData() {

        Observable<ReviewsData> observable =
                ObservablesService.getInstance().getMovieReviewsObservable(mMovie.getId());
        mDisposable = observable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(reviewsData -> {
                    mReviewsAdapter.setReviews(reviewsData.getReviews());
                    mLinearLayoutManager.scrollToPosition(mScrollPosition);
                });
    }


}
