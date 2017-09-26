package com.lqm.home.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by luqinmao on 2017/4/22.
 * tab指示器 viewpager适配器
 */

public class TabViewPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> Fragments;
    private List mTabTitles;

    public TabViewPagerAdapter(FragmentManager fm, List<Fragment> fragments,List tabTitles) {
        super(fm);
        this.Fragments = fragments;
        mTabTitles = tabTitles;
    }

    @Override
    public Fragment getItem(int position) {
        return Fragments.get(position);
    }

    @Override
    public int getCount() {
        return mTabTitles.size();
    }

    //此方法用来显示tab上的名字  (不添加将不显示文字)
    @Override
    public CharSequence getPageTitle(int position) {
        return mTabTitles.get(position).toString();
    }
}