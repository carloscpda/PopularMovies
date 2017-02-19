
package me.cepeda.popularmovies.models;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class ReviewsData {

    @SerializedName("results") private List<Review> reviews = null;

    public List<Review> getReviews() {
        return reviews;
    }

}
