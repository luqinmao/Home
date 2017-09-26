package com.lqm.home.fragment;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.lqm.home.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luqinmao on 2016/11/9.
 * 新闻资讯主页面
 */
public class NewsMainFragment extends BaseFragment {

    private TabLayout mTabNewsTitle;
    private ViewPager mVpNews;
    private ArrayList<Fragment> mFragments;

    //
    public static NewsMainFragment newInstance() {
        return new NewsMainFragment();
    }

    @Override
    public View initView() {
        View view = View.inflate(getActivity(),R.layout.frag_news_main,null);
        mTabNewsTitle = (TabLayout) view.findViewById(R.id.tab_news_title);
        mVpNews = (ViewPager)view.findViewById(R.id.tab_news_vp);
        return view;
    }

    @Override
    public void initData() {
        mFragments = new ArrayList<Fragment>();
        String[] news_type = getResources().getStringArray(R.array.news_type);
        String[] news_type_param = getResources().getStringArray(R.array.news_type_param);
        for (int i =0; i<news_type.length;i++){
            NewsFragment newsFragment = new NewsFragment(news_type_param[i]);
            mTabNewsTitle.addTab(mTabNewsTitle.newTab().setText(news_type[i]));
            mFragments.add(newsFragment);
        }
        //设置TabLayout的模式
        mTabNewsTitle.setTabMode(TabLayout.MODE_SCROLLABLE);
        ChatTopTabAdapter TopAdapter = new ChatTopTabAdapter(getChildFragmentManager(),
                mFragments,news_type);
        mVpNews.setAdapter(TopAdapter);
        mVpNews.setOffscreenPageLimit(3);  //缓存页数
        mTabNewsTitle.setupWithViewPager(mVpNews);

    }
    private class ChatTopTabAdapter extends FragmentPagerAdapter {
        private final String[] tabTitles;
        private List<Fragment> list_fragment;

        public ChatTopTabAdapter(FragmentManager fm, List<Fragment> list_fragment, String[] tabTitle) {
            super(fm);
            this.list_fragment = list_fragment;
            this.tabTitles = tabTitle;
        }

        @Override
        public Fragment getItem(int position) {
            return list_fragment.get(position);
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }

        //此方法用来显示tab上的名字  (不添加将不显示文字)
        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position].toString();
        }
    }

}
