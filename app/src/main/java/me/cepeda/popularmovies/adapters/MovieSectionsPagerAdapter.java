package me.cepeda.popularmovies.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import me.cepeda.popularmovies.R;
import me.cepeda.popularmovies.ui.MovieOverviewFragment;
import me.cepeda.popularmovies.ui.MovieReviewsFragment;

public class MovieSectionsPagerAdapter extends FragmentPagerAdapter {

    Context mContext;

    public MovieSectionsPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new MovieOverviewFragment();
            case 1:
                return new MovieReviewsFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return mContext.getResources().getString(R.string.overview);
            case 1:
                return mContext.getResources().getString(R.string.reviews);
        }
        return null;
    }
}