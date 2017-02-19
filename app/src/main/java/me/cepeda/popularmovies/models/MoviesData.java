
package me.cepeda.popularmovies.models;

import java.util.List;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class MoviesData implements Parcelable {

    @SerializedName("results") private List<Movie> movies = null;

    public final static Parcelable.Creator<MoviesData> CREATOR = new Creator<MoviesData>() {

        public MoviesData createFromParcel(Parcel in) {
            MoviesData instance = new MoviesData();
            in.readList(instance.movies, (Movie.class.getClassLoader()));
            return instance;
        }

        public MoviesData[] newArray(int size) {
            return (new MoviesData[size]);
        }

    };

    public List<Movie> getMovies() {
        return movies;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(movies);
    }

    public int describeContents() {
        return  0;
    }

}
