package me.cepeda.popularmovies.utils;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by CEPEDA on 22/1/17.
 */

public class NetworkUtils {

    private static final String API_BASE_URL = "http://api.themoviedb.org/3/movie/";
    private static final String API_POPULAR_TAG = "popular";
    private static final String API_TOP_RATED_TAG = "top_rated";
    private static final String API_KEY_PARAM = "api_key";
    private static final String API_KEY = "";

    private static final String IMG_BASE_URL = "http://image.tmdb.org/t/p/";
    private static final String IMG_SMALL_SIZE = "w185";
    private static final String IMG_BIG_SIZE = "w500";


    private static URL buildURL(String sortOrder) {
        Uri buildUri = Uri.parse(API_BASE_URL).buildUpon()
                .appendPath(sortOrder)
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .build();

        URL url = null;

        try {
            url = new URL(buildUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static URL buildPopularMoviesURL() {
        return buildURL(API_POPULAR_TAG);
    }

    public static URL buildTopRatedMoviesURL() {
        return buildURL(API_TOP_RATED_TAG);
    }

    public static URL buildMovieThumbnailURL(String pathUrl) {
        Uri buildUri = Uri.parse(IMG_BASE_URL).buildUpon()
                .appendPath(IMG_SMALL_SIZE)
                .appendEncodedPath(pathUrl).build();
        URL url = null;

        try {
            url = new URL(buildUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static URL buildMoviePosterURL(String pathUrl) {
        Uri buildUri = Uri.parse(IMG_BASE_URL).buildUpon()
                .appendPath(IMG_BIG_SIZE)
                .appendEncodedPath(pathUrl).build();
        URL url = null;

        try {
            url = new URL(buildUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
