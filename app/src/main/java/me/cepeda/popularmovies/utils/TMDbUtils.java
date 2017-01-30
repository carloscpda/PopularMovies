package me.cepeda.popularmovies.utils;

import android.net.Uri;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.net.MalformedURLException;
import java.net.URL;

import me.cepeda.popularmovies.models.Size;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by CEPEDA on 30/1/17.
 */

public class TMDbUtils {

    public static Retrofit getRetrofit() {
        return new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://api.themoviedb.org/3/movie/")
                .build();
    }

    public static URL buildMoviePosterURL(String pathUrl, Size size) {
        Uri buildUri = Uri.parse("http://image.tmdb.org/t/p/")
                .buildUpon()
                .appendPath(size.getSize())
                .appendEncodedPath(pathUrl).build();

        try {
            return new URL(buildUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

}
