
package me.cepeda.popularmovies.models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TrailersData {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("results")
    @Expose
    private List<Trailer> trailers = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Trailer> getTrailers() {
        return trailers;
    }

    public void setTrailers(List<Trailer> trailers) {
        this.trailers = trailers;
    }

}
