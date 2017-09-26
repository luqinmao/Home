package com.lqm.home.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.lqm.home.R;
import com.lqm.home.broadcast.UpdateVillageEvent;
import com.lqm.home.fragment.BaseFragment;
import com.lqm.home.fragment.ChatFragment;
import com.lqm.home.fragment.FindFragment;
import com.lqm.home.fragment.HomeFragment;
import com.lqm.home.fragment.NewsMainFragment;
import com.lqm.home.imageloader.GlideImageLoader;
import com.lqm.home.imageloader.ImageLoaderManager;
import com.lqm.home.model.UserCache;
import com.lqm.home.nimsdk.NimUserInfoSDK;
import com.lqm.home.utils.L;
import com.lqm.home.utils.LocationUtils;
import com.lqm.home.utils.T;
import com.lqm.home.utilslqr.UIUtils;
import com.lzy.ninegrid.NineGridView;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.uinfo.constant.UserInfoFieldEnum;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.lqm.home.R.id.tv_login_status;


public class MainActivity extends BaseActivity {

    public static final int CODE_SIGN = 11;

    @Bind(R.id.tv_sign)
    TextView tvSign;
    @Bind(R.id.iv_login_status)
    CircleImageView ivLoginStatus;
    @Bind(tv_login_status)
    TextView tvLoginStatus;
    @Bind(R.id.ll_login_status)
    LinearLayout llLoginStatus;
    @Bind(R.id.tv_create_home)
    TextView tvCreateHome;
    @Bind(R.id.tv_change_color)
    TextView tvChangeColor;
    @Bind(R.id.tv_notes)
    TextView tvNotes;
    @Bind(R.id.tv_setting)
    TextView tvSetting;
    @Bind(R.id.tv_add_friend)
    TextView tv_addFriend;
    @Bind(R.id.layout_about)
    LinearLayout layoutAbout;
    @Bind(R.id.layout_feedback)
    LinearLayout layoutFeedback;
    @Bind(R.id.layout_query)
    LinearLayout layoutQuery;
    public static final int REQ_CLEAR_UNREAD = 100;
    private RadioGroup rg_tab;
    private ViewPager vp_content;
    private ArrayList<BaseFragment> mPagerList;
    private long mExitTime;
    private Context mContext;
    public static DrawerLayout sm_menu;
    public static TextView tvMessageCount;

    private HomeFragment homeFragment;
    private ChatFragment chatFragment;
    private NewsMainFragment newsMainFragment;
    private FindFragment findFragment;
    private NimUserInfo mNimUserInfo;
    private LinearLayout llMenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mContext = this.getApplicationContext();

        NineGridView.setImageLoader(new GlideImageLoader());
        initView();
        initData();

        EventBus.getDefault().register(this);
    }

    private void initView() {
        sm_menu = (DrawerLayout) findViewById(R.id.sm_menu);
        llMenu = (LinearLayout)findViewById(R.id.ll_menu);
        rg_tab = (RadioGroup) findViewById(R.id.rg_tab);
        vp_content = (ViewPager) findViewById(R.id.vp_content);
        tvMessageCount = (TextView)findViewById(R.id.unread_msg_number);
    }

    private void initData() {
        rg_tab.check(R.id.rb_home);
        mPagerList = new ArrayList<>();

        homeFragment = new HomeFragment().newInstance();
        chatFragment = new ChatFragment().newInstance();
        newsMainFragment = new NewsMainFragment().newInstance();
        findFragment = new FindFragment().newInstance();

        mPagerList.add(homeFragment);
        mPagerList.add(chatFragment);
        mPagerList.add(newsMainFragment);
        mPagerList.add(findFragment);

        vp_content.setAdapter(new ContentPagerAdapter(this.getSupportFragmentManager()));
        vp_content.setCurrentItem(0, false);
        vp_content.setOffscreenPageLimit(3);  //缓存页数

        rg_tab.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup arg0, int id) {
                switch (id) {
                    case R.id.rb_home:
                        vp_content.setCurrentItem(0);
                        break;

                    case R.id.rb_chat:
                        vp_content.setCurrentItem(1);
                        break;

                    case R.id.rb_ba:
                        vp_content.setCurrentItem(2);
                        break;

                    case R.id.rb_find:
                        vp_content.setCurrentItem(3);
                        break;

                }
            }
        });

        //设置头像名字
        setUserInfo();

        //保存用户位置
//        setCurrentPosition();
    }

    private void setUserInfo() {
        mNimUserInfo = NimUserInfoSDK.getUser(UserCache.getAccount());
        if (mNimUserInfo == null) {
            getUserInfoFromRemote();
        } else {
            //头像
            if (!TextUtils.isEmpty(mNimUserInfo.getAvatar()) && ivLoginStatus != null) {
                ImageLoaderManager.LoadNetImage(mNimUserInfo.getAvatar(), ivLoginStatus);
            }
            //用户名、个性签名
            if (tvLoginStatus != null)
                tvLoginStatus.setText(mNimUserInfo.getName());
            if (tvSign != null)
                tvSign.setText(mNimUserInfo.getSignature());
        }
    }

    private void setCurrentPosition() {
        final LocationUtils locationUtils = new LocationUtils(MainActivity.this);
        locationUtils.setCallBack(new LocationUtils.LocationInterface() {
            @Override
            public void addressCallBack(String city, double lon, double lat) {
                Map<UserInfoFieldEnum, Object> userparam = new HashMap(1);
                userparam.put(UserInfoFieldEnum.EXTEND,lon+","+lat);
                NimUserInfoSDK.updateUserInfo(userparam, new RequestCallbackWrapper<Void>() {
                    @Override
                    public void onResult(int code, Void result, Throwable exception) {
                        if (code == ResponseCode.RES_SUCCESS) {
                            L.d("位置保存成功");
                        } else {
                            L.d("位置保存失败");
                        }
                    }
                });
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        //保存用户位置
        setCurrentPosition();
    }

    @OnClick({R.id.ll_login_status, R.id.tv_create_home, R.id.tv_change_color,
            R.id.tv_notes, R.id.tv_setting,R.id.tv_add_friend, R.id.layout_about,
            R.id.layout_feedback, R.id.layout_query})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_login_status:
                Intent intent = new Intent(mContext, MyInfoActivity.class);
                startActivityForResult(intent, CODE_SIGN);
                break;
            case R.id.tv_create_home:
                startActivity(new Intent(this,SelectHomeActivity.class));
                break;
            case R.id.tv_change_color:
                T.showShort(mContext,"主题换肤");
                break;
            case R.id.tv_notes:
                startActivity(new Intent(this,NotesActivity.class));
                break;
            case R.id.tv_setting:
                startActivity(new Intent(this,SettingActivity.class));
                break;
            case R.id.tv_add_friend:
                startActivity(new Intent(this,NewFriendActivity.class));
                break;
            case R.id.layout_about:
                startActivity(new Intent(this,AboutActivity.class));
                break;
            case R.id.layout_feedback:
                startActivity(new Intent(this,FeedBackActivity.class));
                break;
            case R.id.layout_query:
                WebViewActivity.runActivity(MainActivity.this,"有疑问请百度","http://www.baidu.com","baidu");
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (sm_menu.isDrawerOpen(GravityCompat.START)){
                sm_menu.closeDrawer(llMenu);
                return true;
            }
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                T.showShort(this, "再按一次退出");
                mExitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {   //发布完帖子刷新界面
            case 10:
                homeFragment.setRefreshing(true);
                homeFragment.onRefresh();
                break;

            case CODE_SIGN:
                setUserInfo();
                break;

        }
    }

    private void getUserInfoFromRemote() {
        List<String> accountList = new ArrayList<>();
        accountList.add(UserCache.getAccount());
        NimUserInfoSDK.getUserInfosFormServer(accountList, new RequestCallback<List<NimUserInfo>>() {
            @Override
            public void onSuccess(List<NimUserInfo> param) {
                setUserInfo();
            }

            @Override
            public void onFailed(int code) {
                UIUtils.showToast("获取用户信息失败" + code);
            }

            @Override
            public void onException(Throwable exception) {
                exception.printStackTrace();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void UpdateVillage(UpdateVillageEvent updateVillageEvent) {
        homeFragment.initData();
    }

    class ContentPagerAdapter extends FragmentPagerAdapter {

        public ContentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mPagerList.get(position);
        }

        @Override
        public int getCount() {
            return mPagerList.size();
        }

    }
}
