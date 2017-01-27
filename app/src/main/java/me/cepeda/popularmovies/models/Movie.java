package me.cepeda.popularmovies.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.joda.time.DateTime;

/**
 * Created by CEPEDA on 22/1/17.
 */

public class Movie implements Parcelable {

    private String originalTitle;
    private String moviePosterPath;
    private String overview;
    private Float userRating;
    private DateTime releaseDate;

    public Movie(String originalTitle, String moviePosterPath, String overview, Float userRating, DateTime releaseDate) {
        this.originalTitle = originalTitle;
        this.moviePosterPath = moviePosterPath;
        this.overview = overview;
        this.userRating = userRating;
        this.releaseDate = releaseDate;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getMoviePosterPath() {
        return moviePosterPath;
    }

    public String getOverview() {
        return overview;
    }

    public Float getUserRating() {
        return userRating;
    }

    public DateTime getReleaseDate() {
        return releaseDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.originalTitle);
        dest.writeString(this.moviePosterPath);
        dest.writeString(this.overview);
        dest.writeValue(this.userRating);
        dest.writeSerializable(this.releaseDate);
    }

    protected Movie(Parcel in) {
        this.originalTitle = in.readString();
        this.moviePosterPath = in.readString();
        this.overview = in.readString();
        this.userRating = (Float) in.readValue(Float.class.getClassLoader());
        this.releaseDate = (DateTime) in.readSerializable();
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}