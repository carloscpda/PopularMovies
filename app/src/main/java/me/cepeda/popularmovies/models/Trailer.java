
package me.cepeda.popularmovies.models;

import com.google.gson.annotations.SerializedName;

public class Trailer {

    @SerializedName("key") private String key;
    @SerializedName("site") private String site;

    public String getKey() {
        return key;
    }

    public String getSite() {
        return site;
    }

}
