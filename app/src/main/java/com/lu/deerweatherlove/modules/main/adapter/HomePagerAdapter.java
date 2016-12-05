package com.lu.deerweatherlove.modules.main.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.widget.TableLayout;

import com.lu.deerweatherlove.modules.main.view.MainFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by L on 16/12/5.
 */
public class HomePagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragments = new ArrayList();
    private List<String> titles = new ArrayList();
    private TableLayout mTablayout;

    public HomePagerAdapter(FragmentManager supportFragmentManager) {
        super(supportFragmentManager);
    }

    public HomePagerAdapter(FragmentManager supportFragmentManager, TableLayout tableLayout) {
        super(supportFragmentManager);

        mTablayout = tableLayout;
    }

    public void addTab(Fragment fragment, String title) {
        fragments.add(fragment);
        titles.add(title);

    }

    /**
     * Return the Fragment associated with a specified position.
     */
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return fragments.size();
    }


    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }
}
