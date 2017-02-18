package me.cepeda.popularmovies.ui;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.cepeda.popularmovies.R;
import me.cepeda.popularmovies.adapters.SortedSectionsPagerAdapter;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.view_pager) ViewPager mViewPager;
    @BindView(R.id.tab_layout) TabLayout mTabLayout;

    private SortedSectionsPagerAdapter mSortedSectionsPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mSortedSectionsPagerAdapter = new SortedSectionsPagerAdapter(getSupportFragmentManager(), this);
        mViewPager.setAdapter(mSortedSectionsPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }



}
