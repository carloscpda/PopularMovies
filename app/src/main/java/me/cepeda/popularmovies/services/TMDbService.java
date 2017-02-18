package me.cepeda.popularmovies.services;

import io.reactivex.Observable;
import me.cepeda.popularmovies.models.Movie;
import me.cepeda.popularmovies.models.MoviesData;
import me.cepeda.popularmovies.models.ReviewsData;
import me.cepeda.popularmovies.models.TrailersData;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by CEPEDA on 29/1/17.
 */

public interface TMDbService {
    String API_KEY = "";

    @GET("popular?api_key=" + API_KEY)
    Observable<MoviesData> getPopularMovieData();

    @GET("top_rated?api_key=" + API_KEY)
    Observable<MoviesData> getTopRatedMovieData();

    @GET("{id}?api_key=" + API_KEY)
    Observable<Movie> getMovieData(@Path("id") int id);

    @GET("{id}/videos?api_key=" + API_KEY)
    Observable<TrailersData> getTrailersData(@Path("id") int id);

    @GET("{id}/reviews?api_key=" + API_KEY)
    Observable<ReviewsData> getReviewsData(@Path("id") int id);
}
