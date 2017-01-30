package me.cepeda.popularmovies.utils;

import io.reactivex.Observable;
import me.cepeda.popularmovies.models.MoviesData;
import retrofit2.http.GET;

/**
 * Created by CEPEDA on 29/1/17.
 */

public interface TMDbService {
    String API_KEY = "";

    @GET("popular?api_key=" + API_KEY)
    Observable<MoviesData> getPopularMovieData();

    @GET("top_rated?api_key=" + API_KEY)
    Observable<MoviesData> getTopRatedMovieData();
}
