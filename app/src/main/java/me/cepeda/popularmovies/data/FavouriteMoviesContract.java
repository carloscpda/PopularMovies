package me.cepeda.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class FavouriteMoviesContract {

    public static final String AUTHORITY = "me.cepeda.popularmovies";
    public static final Uri BASE_CONTENT_URI = android.net.Uri.parse("content://" + AUTHORITY);
    public static final String PATH_MOVIES = "movies";

    public static final class FavouriteMoviesEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String TABLE_NAME = "favourites_movies";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_TMDB_ID = "tmdb_id";
    }

}
