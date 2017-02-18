package me.cepeda.popularmovies.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import me.cepeda.popularmovies.ui.MainFragment;
import me.cepeda.popularmovies.R;

/**
 * Created by CEPEDA on 17/2/17.
 */

public class SortedSectionsPagerAdapter extends FragmentPagerAdapter {

    public static final String KEY = "sort_by";

    private Context mContext;

    public SortedSectionsPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {

        Bundle bundle = new Bundle();
        bundle.putInt(KEY, position);
        MainFragment fragment = new MainFragment();
        fragment.setArguments(bundle);
        return fragment;

    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return mContext.getResources().getString(R.string.sort_by_popular);
            case 1:
                return mContext.getResources().getString(R.string.sort_by_rating);
            case 2:
                return mContext.getResources().getString(R.string.sort_by_favourites);
        }
        return null;
    }
}