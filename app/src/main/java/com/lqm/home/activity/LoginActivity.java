package com.lqm.home.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lqm.home.R;
import com.lqm.home.app.AppConst;
import com.lqm.home.model.ResponseData;
import com.lqm.home.model.UserCache;
import com.lqm.home.model.UserVO;
import com.lqm.home.nimsdk.NimAccountSDK;
import com.lqm.home.nimsdk.NimUserInfoSDK;
import com.lqm.home.parse.JsonCallback;
import com.lqm.home.utils.PrefUtils;
import com.lqm.home.utils.T;
import com.lqm.home.utilslqr.UIUtils;
import com.lqm.home.widget.Topbar;
import com.lzy.okgo.OkGo;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.LoginInfo;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;


public class LoginActivity extends BaseActivity {
    private EditText accountEdit;
    private EditText pswEdit;
    private Button loginBtn;
    private TextView tv_forget_pwd;
    private Topbar login_topbar;
    private AbortableFuture<LoginInfo> mLoginRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        initData();
    }

    private void initView() {
        accountEdit = (EditText) findViewById(R.id.account_edit);
        pswEdit = (EditText) findViewById(R.id.token_edit);
        loginBtn = (Button) findViewById(R.id.login);
        tv_forget_pwd = (TextView) findViewById(R.id.tv_forget_pwd);
        login_topbar = (Topbar) findViewById(R.id.login_topbar);

    }

    private void initData() {
        accountEdit.setText(getIntent().getStringExtra("accid"));
//        pswEdit.setText(getIntent().getStringExtra("password"));

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
        tv_forget_pwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                T.showShort(LoginActivity.this, "忘记密码");
            }
        });
        login_topbar.setTopbarOnClickListener(new Topbar.TopbarOnClickListener() {
            @Override
            public void leftOnClick() {
                finish();
            }

            @Override
            public void rightOnClick() {
            }
        });

    }


    private void login() {
        final String accid = accountEdit.getText().toString().toLowerCase();
        final String psd = pswEdit.getText().toString().toLowerCase();

        if (accid.equals("") || psd.equals("")) {
            T.showShort(this, "账号或密码不能为空");
            return;
        }
        showWaitingDialog("登录中...");

        OkGo.post(AppConst.User.LOGIN)
                .params("accid", accid)
                .params("password", psd)
                .execute(new JsonCallback<ResponseData<UserVO>>() {
                    @Override
                    public void onSuccess(final ResponseData<UserVO> responseData, Call call, Response response) {

                        if (responseData.isSuccess() == false) {
                            T.showShort(LoginActivity.this, "用户名或密码错误，登录失败");
                            hideWaitingDialog();
                            return;
                        } else {
                            PrefUtils.saveUserServerInfo(responseData.getData().getMyselfInfo());
                            //配置登录信息，并开始登录
                            NimAccountSDK.login(accid, psd, new RequestCallback<LoginInfo>() {
                                @Override
                                public void onSuccess(LoginInfo param) {
                                    hideWaitingDialog();

                                    //保存用户名到内存中
                                    UserCache.setAccount(param.getAccount());
                                    //保存用户信息到本地，方便下次启动APP做自动登录用
                                    NimAccountSDK.saveUserAccount(param.getAccount());
                                    NimAccountSDK.saveUserToken(psd);
                                    //更新本地用户资料
                                    List<String> list = new ArrayList<String>();
                                    list.add(UserCache.getAccount());
                                    NimUserInfoSDK.getUserInfosFormServer(list, null);

                                    //进行主界面
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                    hideWaitingDialog();
                                }

                                @Override
                                public void onFailed(int code) {
                                    if (code == 302 || code == 404) {
                                        UIUtils.showToast("登录云信帐号或密码错误");
                                    } else {
                                        UIUtils.showToast("登录云信失败: " + code);
                                    }
                                    hideWaitingDialog();
                                }

                                @Override
                                public void onException(Throwable exception) {
                                    UIUtils.showToast("无效输入");
                                    hideWaitingDialog();
                                }
                            });
                        }

                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        T.showShort(LoginActivity.this, e + "");
                        hideWaitingDialog();
                    }
                });
    }

}
