package me.cepeda.popularmovies.services;

import android.support.v4.util.ArrayMap;
import android.util.Log;

import java.util.Map;

import io.reactivex.Observable;
import me.cepeda.popularmovies.models.Movie;
import me.cepeda.popularmovies.models.MoviesData;
import me.cepeda.popularmovies.models.ReviewsData;
import me.cepeda.popularmovies.utils.TMDbUtils;
import retrofit2.Retrofit;

public class ObservablesService {

    private static ObservablesService ourInstance = new ObservablesService();

    public static ObservablesService getInstance() {
        return ourInstance;
    }

    private TMDbService mService;
    private Observable<MoviesData> mPopularMoviesObservable;
    private Observable<MoviesData> mTopRatedMoviesObservable;
    private Map<Integer, Observable<Movie>> mFavouriteMovieObservablesMap;
    private Map<Integer, Observable<ReviewsData>> mMovieReviewsObservablesMap;



    private ObservablesService() {
        Retrofit retrofit = TMDbUtils.getRetrofit();
        mService = retrofit.create(TMDbService.class);
        mPopularMoviesObservable = mService.getPopularMovieData().cache();
        mTopRatedMoviesObservable = mService.getTopRatedMovieData().cache();
        mFavouriteMovieObservablesMap = new ArrayMap<>();
        mMovieReviewsObservablesMap = new ArrayMap<>();

    }

    public Observable<MoviesData> getPopularMoviesObservable() {
        return mPopularMoviesObservable;
    }

    public Observable<MoviesData> getTopRatedMoviesObservable() {
        return mTopRatedMoviesObservable;
    }

    public Observable<Movie> getMovieObservable(int id) {
        if (!mFavouriteMovieObservablesMap.containsKey(id))
            mFavouriteMovieObservablesMap.put(id, mService.getMovieData(id).cache());
        return mFavouriteMovieObservablesMap.get(id);
    }

    public Observable<ReviewsData> getMovieReviewsObservable(int id) {
        if (!mMovieReviewsObservablesMap.containsKey(id))
            mMovieReviewsObservablesMap.put(id, mService.getReviewsData(id).cache());
        return mMovieReviewsObservablesMap.get(id);
    }


}
