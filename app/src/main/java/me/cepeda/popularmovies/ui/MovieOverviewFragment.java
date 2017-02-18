package me.cepeda.popularmovies.ui;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.cepeda.popularmovies.R;
import me.cepeda.popularmovies.models.Movie;
import me.cepeda.popularmovies.utils.TMDbUtils;

public class MovieOverviewFragment extends Fragment {

    private final String TAG = getClass().getName();

    @BindView(R.id.iv_movie_poster) ImageView mMoviePosterImageView;
    @BindView(R.id.tv_original_title) TextView mOriginalTitleTextView;
    @BindView(R.id.tv_date) TextView mDateTextView;
    @BindView(R.id.tv_overview) TextView mOverviewTextView;
    @BindView(R.id.tv_user_rating) TextView mUserRatingTextView;

    private Movie movie;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getActivity().getIntent();
        movie = intent.getParcelableExtra(Intent.EXTRA_INTENT);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_overview, container, false);

        ButterKnife.bind(this, rootView);

        String posterPath = movie.getPosterPath();
        URL posterURL = TMDbUtils.buildMoviePosterURL(posterPath);
        Picasso.with(getActivity())
                .load(String.valueOf(posterURL))
                .into(mMoviePosterImageView);

        mOriginalTitleTextView.setText(movie.getOriginalTitle());
        mDateTextView.setText("(" + movie.getReleaseDate().substring(0, 4) + ")");
        mOverviewTextView.setText(movie.getOverview());

        String rate = String.valueOf(movie.getVoteAverage() + "/10");
        SpannableString spannableRate =  new SpannableString(rate);
        spannableRate.setSpan(new RelativeSizeSpan(2f), 0, 3, 0); // set size
        mUserRatingTextView.setText(spannableRate);

        return rootView;
    }


}
