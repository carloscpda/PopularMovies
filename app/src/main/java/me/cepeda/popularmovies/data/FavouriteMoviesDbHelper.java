package me.cepeda.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import me.cepeda.popularmovies.data.FavouriteMoviesContract.FavouriteMoviesEntry;

public class FavouriteMoviesDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "favourite_movies.db";
    private static final int DATABASE_VERSION = 1;

    public FavouriteMoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIES_TABLE =

                "CREATE TABLE " + FavouriteMoviesEntry.TABLE_NAME + " (" +
                        FavouriteMoviesEntry.COLUMN_TMDB_ID    + " INTEGER PRIMARY KEY, " +
                        FavouriteMoviesEntry.COLUMN_TITLE      + " TEXT NOT NULL)";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavouriteMoviesEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
