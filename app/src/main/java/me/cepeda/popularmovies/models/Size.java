package me.cepeda.popularmovies.models;

/**
 * Created by CEPEDA on 30/1/17.
 */

public enum  Size {
    SMALL("w185"), BIG("w500");

    private String size;

    Size(String size) {
        this.size = size;
    }

    public String getSize() {
        return size;
    }
}