package com.lqm.home.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.lqm.home.R;
import com.lqm.home.fragment.BaiduImgFragment;
import com.lqm.home.fragment.BaiduImgSearchFragment;
import com.lqm.home.widget.Topbar;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by luqinmao on 2017/1/3.
 * 百度图片页面
 */

public class BaiDuImageActivity extends BaseActivity {


    @Bind(R.id.topbar_baidu)
    Topbar topbarBaidu;
    @Bind(R.id.tab_baidu_title)
    TabLayout tabBaiduTitle;
    @Bind(R.id.tab_baidu_vp)
    ViewPager tabBaiduVp;

    private ArrayList<Fragment> mFragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baidu_image);
        ButterKnife.bind(this);

        initView();
        initData();

    }

    private void initView() {
        topbarBaidu.setTopbarOnClickListener(new Topbar.TopbarOnClickListener() {
            @Override
            public void leftOnClick() {
                finish();
            }

            @Override
            public void rightOnClick() {
            }
        });
    }

    private void initData() {
        mFragments = new ArrayList<>();
        String[] baiduImgCol = getResources().getStringArray(R.array.baidu_img_col);
        String[] baiduImgTag = getResources().getStringArray(R.array.baidu_img_tag);
        for (int i =0; i<baiduImgTag.length;i++){
            BaiduImgFragment baiduImgFragment = new BaiduImgFragment(baiduImgCol[i],baiduImgTag[i]);
            tabBaiduTitle.addTab(tabBaiduTitle.newTab().setText(baiduImgCol[i]));
            mFragments.add(baiduImgFragment);
        }
        BaiduImgSearchFragment searchFragment = new BaiduImgSearchFragment(this);
        mFragments.add(searchFragment);

        tabBaiduTitle.setTabMode(TabLayout.MODE_SCROLLABLE);
        BaiDuImgTabAdapter TopAdapter = new BaiDuImgTabAdapter
                (this.getSupportFragmentManager(), mFragments,baiduImgCol);
        tabBaiduVp.setAdapter(TopAdapter);
        tabBaiduVp.setOffscreenPageLimit(3);
        tabBaiduTitle.setupWithViewPager(tabBaiduVp);

    }


    private class BaiDuImgTabAdapter extends FragmentPagerAdapter {
        private final String[] tabTitles;
        private List<Fragment> fragments;

        public BaiDuImgTabAdapter(FragmentManager fm, List<Fragment> fragments, String[] tabTitle) {
            super(fm);
            this.fragments = fragments;
            this.tabTitles = tabTitle;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position].toString();
        }
    }

}
