package me.cepeda.popularmovies.utils;

import android.net.Uri;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.net.MalformedURLException;
import java.net.URL;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TMDbUtils {

    public static Retrofit getRetrofit() {
        return new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://api.themoviedb.org/3/movie/")
                .build();
    }

    public static URL buildMoviePosterURL(String pathUrl) {
        Uri buildUri = Uri.parse("http://image.tmdb.org/t/p/w342/")
                .buildUpon()
                .appendEncodedPath(pathUrl)
                .build();

        try {
            return new URL(buildUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static URL buildMovieBackdropURL(String pathUrl) {
        Uri buildUri = Uri.parse("http://image.tmdb.org/t/p/w780/")
                .buildUpon()
                .appendEncodedPath(pathUrl)
                .build();

        try {
            return new URL(buildUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

}
