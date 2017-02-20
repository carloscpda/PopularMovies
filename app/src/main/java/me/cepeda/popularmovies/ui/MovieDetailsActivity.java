package me.cepeda.popularmovies.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.cepeda.popularmovies.R;
import me.cepeda.popularmovies.adapters.MovieSectionsPagerAdapter;
import me.cepeda.popularmovies.data.FavouriteMoviesContract.FavouriteMoviesEntry;
import me.cepeda.popularmovies.models.Movie;
import me.cepeda.popularmovies.models.Trailer;
import me.cepeda.popularmovies.models.TrailersData;
import me.cepeda.popularmovies.services.TMDbService;
import me.cepeda.popularmovies.utils.TMDbUtils;
import retrofit2.Retrofit;

public class MovieDetailsActivity extends FragmentActivity
        implements View.OnClickListener, AppBarLayout.OnOffsetChangedListener {

    private static final String YOUTUBE_SITE = "YouTube";
    private static final String BUNDLE_TAG_BUTTON_SHOW = "button";
    private static final String BUNDLE_TAG_IS_COLLAPSED = "collapsed";

    @BindView(R.id.app_bar_layout) AppBarLayout mAppBarLayout;
    @BindView(R.id.tab_layout) TabLayout mTabLayout;
    @BindView(R.id.vp_container) ViewPager mViewPager;
    @BindView(R.id.iv_movie_backdrop) ImageView mMovieBackdropImageView;
    @BindView(R.id.ib_play_trailer) ImageButton mPlayTrailerImageButton;
    @BindView(R.id.fab_favourite) FloatingActionButton mFloatingActionButtonFavourite;

    private MovieSectionsPagerAdapter mMovieSectionsPagerAdapter;
    private Movie movie;
    private Trailer mainTrailer;
    private boolean isPlayButtonShow = true;
    private boolean isCollapsed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null)
            if (extras.containsKey(Intent.EXTRA_INTENT))
                movie = extras.getParcelable(Intent.EXTRA_INTENT);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(BUNDLE_TAG_BUTTON_SHOW))
                isPlayButtonShow = savedInstanceState.getBoolean(BUNDLE_TAG_BUTTON_SHOW);
            if (savedInstanceState.containsKey(BUNDLE_TAG_IS_COLLAPSED))
                isCollapsed = savedInstanceState.getBoolean(BUNDLE_TAG_IS_COLLAPSED);
        }

        mMovieSectionsPagerAdapter = new MovieSectionsPagerAdapter(getSupportFragmentManager(), this);
        mViewPager.setAdapter(mMovieSectionsPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mAppBarLayout.addOnOffsetChangedListener(this);
        mAppBarLayout.setExpanded(!isCollapsed);

        String backdropPath = movie.getBackdropPath();
        URL backdropURL = TMDbUtils.buildMovieBackdropURL(backdropPath);
        Picasso.with(this)
                .load(String.valueOf(backdropURL))
                .into(mMovieBackdropImageView);

        loadTrailerData();
        mPlayTrailerImageButton.setOnClickListener(this);
        mFloatingActionButtonFavourite.setOnClickListener(this);

        if (isFavouriteMovie()) mFloatingActionButtonFavourite.setImageResource(R.drawable.ic_favorite_red_a700_24dp);
        else mFloatingActionButtonFavourite.setImageResource(R.drawable.ic_favorite_grey_900_24dp);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(BUNDLE_TAG_BUTTON_SHOW, isPlayButtonShow);
        outState.putBoolean(BUNDLE_TAG_IS_COLLAPSED, isCollapsed);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();

        switch (id) {
            case R.id.ib_play_trailer:

                String idPath = mainTrailer.getKey();

                Intent startYoutubeApp = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("vnd.youtube:" + idPath)
                );
                Intent startYoutubeWeb = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://www.youtube.com/watch?v=" + idPath));

                try {
                    startActivity(startYoutubeApp);
                } catch (Exception e) {
                    if (startYoutubeWeb.resolveActivity(getPackageManager()) != null)
                        startActivity(startYoutubeWeb);
                    else {
                        String message = getResources().getString(R.string.error_show_trailer);
                        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                    }
                }

                break;

            case R.id.fab_favourite:
                if (isFavouriteMovie()) {
                    removeFavouriteMovie();
                    mFloatingActionButtonFavourite.setImageResource(R.drawable.ic_favorite_grey_900_24dp);
                } else {
                    addFavouriteMovie();
                    mFloatingActionButtonFavourite.setImageResource(R.drawable.ic_favorite_red_a700_24dp);
                }
        }
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        int swapLine = appBarLayout.getTotalScrollRange() / 3;

        if (isPlayButtonShow && -verticalOffset > swapLine) {
            isCollapsed = true;
            isPlayButtonShow = false;
            mPlayTrailerImageButton.animate()
                    .alpha(0.0f)
                    .setDuration(300)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            if (!isPlayButtonShow) mPlayTrailerImageButton.setVisibility(View.INVISIBLE);
                        }
                    });
        } else if (!isPlayButtonShow && -verticalOffset < swapLine) {
            isCollapsed = false;
            isPlayButtonShow = true;
            mPlayTrailerImageButton.setVisibility(View.VISIBLE);
            mPlayTrailerImageButton.animate()
                    .alpha(1.0f)
                    .setDuration(300);
        }
    }

    private void loadTrailerData() {
        Retrofit retrofit = TMDbUtils.getRetrofit();
        TMDbService service = retrofit.create(TMDbService.class);

        Observable<TrailersData> trailersDataObservable = service.getTrailersData(movie.getId());
        trailersDataObservable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext((ObservableSource<? extends TrailersData>) throwable -> mPlayTrailerImageButton.setVisibility(View.GONE))
                .subscribe(trailersData -> {
                    for (Trailer trailer: trailersData.getTrailers()) {
                        if (trailer.getSite().equals(YOUTUBE_SITE)) {
                            mainTrailer = trailer;
                            mPlayTrailerImageButton.setVisibility(View.VISIBLE);
                            return;
                        }
                    }
                    mPlayTrailerImageButton.setVisibility(View.GONE);
                });
    }

    private void addFavouriteMovie() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(FavouriteMoviesEntry.COLUMN_TITLE, movie.getOriginalTitle());
        Uri uri = FavouriteMoviesEntry.CONTENT_URI.buildUpon().appendPath(String.valueOf(movie.getId())).build();
        getContentResolver().insert(uri, contentValues);
    }

    private void removeFavouriteMovie() {
        Uri uri = FavouriteMoviesEntry.CONTENT_URI.buildUpon().appendPath(String.valueOf(movie.getId())).build();
        getContentResolver().delete(uri, null, null);
    }

    private Boolean isFavouriteMovie() {
        Uri uri = FavouriteMoviesEntry.CONTENT_URI.buildUpon().appendPath(String.valueOf(movie.getId())).build();
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        int count = 0;
        if (cursor != null) {
            count = cursor.getCount();
            cursor.close();
        }
        return count == 1;
    }

}
