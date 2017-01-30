package me.cepeda.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.cepeda.popularmovies.models.Movie;
import me.cepeda.popularmovies.models.Size;
import me.cepeda.popularmovies.utils.TMDbUtils;

public class MovieInformationActivity extends AppCompatActivity {

    @BindView(R.id.iv_movie_big_poster) ImageView mMoviePosterImageView;
    @BindView(R.id.tv_original_title) TextView mOriginalTitleTextView;
    @BindView(R.id.tv_overview) TextView mOverviewTextView;
    @BindView(R.id.tv_user_rating) TextView mUserRatingTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_information);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        Movie movie = intent.getParcelableExtra(Intent.EXTRA_INTENT);

        String posterPath = movie.getPosterPath();
        URL posterURL = TMDbUtils.buildMoviePosterURL(posterPath, Size.BIG);
        Picasso.with(this).
                load(String.valueOf(posterURL)).
                into(mMoviePosterImageView);

        String titleAndYear = movie.getOriginalTitle() + " (" + movie.getReleaseDate() + ")";

        mOriginalTitleTextView.setText(titleAndYear);
        mOverviewTextView.setText(movie.getOverview());
        mUserRatingTextView.setText(String.valueOf(movie.getVoteAverage()));
    }
}
