package me.cepeda.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static me.cepeda.popularmovies.data.FavouriteMoviesContract.FavouriteMoviesEntry.COLUMN_TMDB_ID;
import static me.cepeda.popularmovies.data.FavouriteMoviesContract.FavouriteMoviesEntry.TABLE_NAME;

public class FavouriteMoviesProvider extends ContentProvider {

    public static final int MOVIES = 100;
    public static final int MOVIE_WITH_ID = 101;

    public static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(
                FavouriteMoviesContract.AUTHORITY,
                FavouriteMoviesContract.PATH_MOVIES,
                MOVIES);
        uriMatcher.addURI(
                FavouriteMoviesContract.AUTHORITY,
                FavouriteMoviesContract.PATH_MOVIES + "/#",
                MOVIE_WITH_ID
        );
        return uriMatcher;
    }

    private FavouriteMoviesDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new FavouriteMoviesDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String orderBy) {
        final SQLiteDatabase db = mDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor rCursor;

        switch (match) {
            case MOVIES:
                rCursor = db.query(
                        TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null, null,
                        orderBy
                );
                break;
            case MOVIE_WITH_ID:
                String id = uri.getPathSegments().get(1);
                rCursor = db.query(
                        TABLE_NAME,
                        projection,
                        COLUMN_TMDB_ID + "=?",
                        new String[]{id},
                        null, null, null
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri " + uri);
        }
        if (getContext() != null) rCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return rCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIE_WITH_ID:
                String idTMDb = uri.getPathSegments().get(1);
                contentValues.put(COLUMN_TMDB_ID, idTMDb);
                long id = db.insert(TABLE_NAME, null, contentValues);
                if (id <= 0) throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri " + uri);
        }
        if (getContext() != null) getContext().getContentResolver().notifyChange(uri, null);
        return uri;
    }

    @Override
    public int delete(@NonNull Uri uri, String s, String[] strings) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int rInteger;

        switch (match) {
            case MOVIE_WITH_ID:
                String id = uri.getPathSegments().get(1);
                rInteger = db.delete(TABLE_NAME, COLUMN_TMDB_ID + "=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri " + uri);
        }
        if (getContext() != null) getContext().getContentResolver().notifyChange(uri, null);
        return rInteger;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
