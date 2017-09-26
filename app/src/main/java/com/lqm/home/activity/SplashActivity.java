package com.lqm.home.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;

import com.lqm.home.R;
import com.lqm.home.nimsdk.NimAccountSDK;
import com.lqm.home.utils.PrefUtils;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.StatusCode;

/**
 * 闪屏页
 */
public class SplashActivity extends BaseActivity {
    private LinearLayout rootLayout;

    @Override
    protected void onCreate(Bundle arg0) {
        setContentView(R.layout.activity_splash);
        super.onCreate(arg0);
        // 渐进显示的动画
        rootLayout = (LinearLayout) findViewById(R.id.splash_root);
        AlphaAnimation animation = new AlphaAnimation(0.3f, 1.0f);
        animation.setDuration(1500);
        rootLayout.startAnimation(animation);



    }

    @Override
    protected void onStart() {
        super.onStart();

        new Thread(new Runnable() {
            public void run() {
                boolean userGuide = PrefUtils.getBoolean(SplashActivity.this, "is_user_guide_showed", false);
                if (!userGuide) { //新手引导页面
                    startActivity(new Intent(SplashActivity.this, GuideActivity.class));
                    finish();
                } else {
                    if (canAutoLogin()) {
                        StatusCode userStatus =  NIMClient.getStatus();
                        if (userStatus.wontAutoLogin()){ // 被踢出、账号被禁用、密码错误等情况，自动登录失败，需要返回到登录界面进行重新登录操作
                            startActivity(new Intent(SplashActivity.this, LoginOrRegistActivity.class));
                        }else{
                            startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        }
                    } else {
                        startActivity(new Intent(SplashActivity.this, LoginOrRegistActivity.class));

                    }
                    finish();
                }
            }
        }).start();

    }

    /**
     * 判断是否可以自动登录
     *
     * @return
     */
    public boolean canAutoLogin() {
        String account = NimAccountSDK.getUserAccount();
        String token = NimAccountSDK.getUserToken();
        return !TextUtils.isEmpty(account) && !TextUtils.isEmpty(token);
    }

}
