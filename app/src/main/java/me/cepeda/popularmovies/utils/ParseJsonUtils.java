package me.cepeda.popularmovies.utils;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import me.cepeda.popularmovies.models.Movie;

/**
 * Created by CEPEDA on 22/1/17.
 */

public class ParseJsonUtils {

    public static Movie[] getMoviesFromJson(String jsonString) throws JSONException {

        final String TAG_LIST = "results";
        final String TAG_TITLE = "original_title";
        final String TAG_POSTER_PATH = "poster_path";
        final String TAG_OVERVIEW = "overview";
        final String TAG_RATING = "vote_average";
        final String TAG_RELEASE_DATE = "release_date";

        JSONObject jsonData = new JSONObject(jsonString);
        JSONArray moviesDataArray = jsonData.getJSONArray(TAG_LIST);

        Movie[] movies = new Movie[moviesDataArray.length()];

        for (int i = 0; i < moviesDataArray.length(); i++) {
            JSONObject movieJSON = moviesDataArray.getJSONObject(i);
            movies[i] = new Movie(
                    movieJSON.getString(TAG_TITLE),
                    movieJSON.getString(TAG_POSTER_PATH),
                    movieJSON.getString(TAG_OVERVIEW),
                    (float) movieJSON.getDouble(TAG_RATING),
                    getDateFromString(movieJSON.getString(TAG_RELEASE_DATE))
            );
        }

        return movies;
    }

    private static DateTime getDateFromString(String s) {
        String[] dataString = s.split("-");

        int year = Integer.parseInt(dataString[0]);
        int month = Integer.parseInt(dataString[1]);
        int day = Integer.parseInt(dataString[2]);

       return new DateTime(year, month, day, 0, 0);
    }

}