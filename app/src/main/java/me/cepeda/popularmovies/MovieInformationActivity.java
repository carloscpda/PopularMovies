package me.cepeda.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.net.URL;

import me.cepeda.popularmovies.models.Movie;
import me.cepeda.popularmovies.utils.NetworkUtils;

public class MovieInformationActivity extends AppCompatActivity {

    private ImageView mMoviePosterImageView;
    private TextView mOriginalTitleTextView;
    private TextView mOverviewTextView;
    private TextView mUserRatingTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_information);

        mMoviePosterImageView = (ImageView) findViewById(R.id.iv_movie_big_poster);
        mOriginalTitleTextView = (TextView) findViewById(R.id.tv_original_title);
        mOverviewTextView = (TextView) findViewById(R.id.tv_overview);
        mUserRatingTextView = (TextView) findViewById(R.id.tv_user_rating);

        Intent intent = getIntent();
        Movie movie = intent.getParcelableExtra(Intent.EXTRA_INTENT);

        String posterPath = movie.getMoviePosterPath();
        URL posterURL = NetworkUtils.buildMoviePosterURL(posterPath);
        Picasso.with(this).load(String.valueOf(posterURL)).into(mMoviePosterImageView);


        String titleAndYear = movie.getOriginalTitle() + " (" + movie.getReleaseDate().getYear() + ")";

        mOriginalTitleTextView.setText(titleAndYear);
        mOverviewTextView.setText(movie.getOverview());
        mUserRatingTextView.setText(String.valueOf(movie.getUserRating()));

    }
}
