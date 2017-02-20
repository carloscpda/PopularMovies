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
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
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
    @BindView(R.id.tv_error_message_display) TextView mErrorMessageTextView;
    @BindView(R.id.pb_loading_indicator) ProgressBar mLoagingIndicatorProgressBar;

    private LinearLayoutManager mLinearLayoutManager;
    private ReviewsAdapter mReviewsAdapter;
    private Disposable mDisposable;
    private Movie mMovie;
    private int mScrollPosition = 0;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getActivity().getIntent();

        Bundle extras = intent.getExtras();
        if (extras != null)
            if (extras.containsKey(Intent.EXTRA_INTENT))
                mMovie = extras.getParcelable(Intent.EXTRA_INTENT);

        if (savedInstanceState != null)
            if (savedInstanceState.containsKey(TAG_POSITION))
                mScrollPosition = savedInstanceState.getInt(TAG_POSITION);

        mReviewsAdapter = new ReviewsAdapter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_reviews, container, false);

        ButterKnife.bind(this, rootView);

        mLinearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mReviewsAdapter);

        loadReviewsData();

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
                .doOnSubscribe(disposable -> mLoagingIndicatorProgressBar.setVisibility(View.VISIBLE))
                .doOnTerminate(() -> mLoagingIndicatorProgressBar.setVisibility(View.INVISIBLE))
                .onErrorResumeNext((ObservableSource<? extends ReviewsData>) observer -> showErrorMessageView())
                .subscribe(reviewsData -> {
                    mReviewsAdapter.setReviews(reviewsData.getReviews());
                    mLinearLayoutManager.scrollToPosition(mScrollPosition);
                    showReviewsDataView();
                });
    }

    private void showReviewsDataView() {
        mErrorMessageTextView.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessageView() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageTextView.setVisibility(View.VISIBLE);
    }


}
