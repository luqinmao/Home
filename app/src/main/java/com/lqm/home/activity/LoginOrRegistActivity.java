package com.lqm.home.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.lqm.home.R;
import com.lqm.home.utils.T;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 选择登录或者注册界面
 */

public class LoginOrRegistActivity extends BaseActivity {


    @Bind(R.id.btn_register)
    Button btnRegister;
    @Bind(R.id.btn_login)
    Button btnLogin;


    private long mExitTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_or_regist);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_register, R.id.btn_login})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_register:
                startActivity(new Intent(this,RegisterActivity.class));
                break;
            case R.id.btn_login:
                startActivity(new Intent(this,LoginActivity.class));
                break;
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
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
}
