package me.cepeda.popularmovies.ui;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.cepeda.popularmovies.R;
import me.cepeda.popularmovies.adapters.ReviewsAdapter;
import me.cepeda.popularmovies.models.Movie;
import me.cepeda.popularmovies.models.ReviewsData;
import me.cepeda.popularmovies.services.TMDbService;
import me.cepeda.popularmovies.utils.TMDbUtils;
import retrofit2.Retrofit;

public class MovieReviewsFragment extends Fragment {

    private final String TAG = getClass().getName();

    @BindView(R.id.rv_reviews) RecyclerView mRecyclerView;
    ReviewsAdapter mReviewsAdapter;

    private Movie movie;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getActivity().getIntent();
        movie = intent.getParcelableExtra(Intent.EXTRA_INTENT);

        mReviewsAdapter = new ReviewsAdapter();

        loadReviewsData();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_reviews, container, false);

        ButterKnife.bind(this, rootView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mReviewsAdapter);

        return rootView;
    }

    private void loadReviewsData() {
        Retrofit retrofit = TMDbUtils.getRetrofit();
        TMDbService service = retrofit.create(TMDbService.class);

        Observable<ReviewsData> observable = service.getReviewsData(movie.getId());
        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(reviewsData -> {
                    mReviewsAdapter.setReviews(reviewsData.getReviews());
                });
    }


}
