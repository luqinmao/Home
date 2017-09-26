package com.lqm.home.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.lqm.home.R;
import com.lqm.home.app.AppConst;
import com.lqm.home.utils.T;
import com.lqm.home.widget.Topbar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import static com.lqm.home.R.id.btn_register;
import static com.lqm.home.R.id.et_code;
import static com.mob.tools.utils.ResHelper.getStringRes;

/**
 * 注册页面
 */

public class RegisterActivity extends BaseActivity {


    @Bind(R.id.regist_topbar)
    Topbar registTopbar;
    @Bind(R.id.et_phone)
    EditText etPhone;
    @Bind(et_code)
    EditText etCode;
    @Bind(R.id.btn_getcode)
    Button btnGetcode;
    @Bind(R.id.et_psw)
    EditText etPsw;
    @Bind(btn_register)
    Button btnRegister;
    @Bind(R.id.iv_hide)
    ImageView ivHide;
    @Bind(R.id.iv_show)
    ImageView ivShow;
    private TextChange textChange;

    private EventHandler eventHandler;
    private String phoneString;


    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int event = msg.arg1;
            int result = msg.arg2;
            Object data = msg.obj;
            Log.e("event", "event=" + event);
            if (result == SMSSDK.RESULT_COMPLETE) {
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    T.showShort(RegisterActivity.this,"提交验证码成功");
                    Intent intent = new Intent(RegisterActivity.this, RegisterNextActivity.class);
                    intent.putExtra("phone", etPhone.getText().toString().trim());
                    intent.putExtra("password", etPsw.getText().toString().trim());
                    startActivity(intent);

                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    T.showShort(RegisterActivity.this,"验证码已经发送");
                }
            } else {
                ((Throwable) data).printStackTrace();
                int resId = getStringRes(RegisterActivity.this,
                        "smssdk_network_error");
                T.showShort(RegisterActivity.this,"验证码错误");
                if (resId > 0) {
                    T.showShort(RegisterActivity.this,resId);
                }
            }

        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        ButterKnife.bind(this);

        initView();
        initSmsCode();
        registTopbar.setTopbarOnClickListener(new Topbar.TopbarOnClickListener() {
            @Override
            public void leftOnClick() {
                finish();
            }

            @Override
            public void rightOnClick() {
            }
        });

    }

    private void initSmsCode() {
        SMSSDK.initSDK(this, AppConst.MOBAPPKEY, AppConst.MOBAPPSECRET);
        eventHandler = new EventHandler() {

            @Override
            public void afterEvent(int event, int result, Object data) {
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                handler.sendMessage(msg);
            }

        };
        SMSSDK.registerEventHandler(eventHandler);
    }

    private void initView() {
        // 同时监听多个EditText
        btnRegister.setEnabled(false);
        textChange = new TextChange();
        etPhone.addTextChangedListener(textChange);
        etCode.addTextChangedListener(textChange);
        etPsw.addTextChangedListener(textChange);

    }

    @OnClick({R.id.btn_getcode, R.id.btn_register, R.id.regist_topbar, R.id.iv_hide, R.id.iv_show})
    public void onClick(View view)  {
        switch (view.getId()) {
            case R.id.btn_getcode:
                getCode();
                break;
            case btn_register:
                String code = etCode.getText().toString().trim();
                if (!isMobileNO(etPhone.getText().toString().trim())) {
                    T.showShort(this, "请输入正确的手机号码!");
                    return;
                }
                if (!TextUtils.isEmpty(code)) {
                    SMSSDK.submitVerificationCode("86", phoneString, code);
                } else {
                    T.showShort(this, "验证码不能为空!");
                }
//                Intent intent = new Intent(RegisterActivity.this, RegisterNextActivity.class);
//                intent.putExtra("phone", etPhone.getText().toString().trim());
//                intent.putExtra("password", etPsw.getText().toString().trim());
//                startActivity(intent);
                break;

            case R.id.iv_hide:
                showPassword(true);
                break;
            case R.id.iv_show:
                showPassword(false);
                break;
        }
    }

    private void getCode() {
        if (!TextUtils.isEmpty(etPhone.getText().toString())) {
            phoneString = etPhone.getText().toString();
            SMSSDK.getVerificationCode("86", phoneString);
            timer.start();
        } else {
            Toast.makeText(this, "电话不能为空", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 验证码按钮倒计时
     */
    private CountDownTimer timer = new CountDownTimer(60000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            btnGetcode.setEnabled(false);
            btnGetcode.setText((millisUntilFinished / 1000) + "s");
        }
        @Override
        public void onFinish() {
            btnGetcode.setEnabled(true);
            btnGetcode.setText("获取验证码");
        }
    };


    private void showPassword(boolean isShow) {
        if (isShow) {
            etPsw.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            ivShow.setVisibility(View.VISIBLE);
            ivHide.setVisibility(View.GONE);
        } else {
            etPsw.setTransformationMethod(PasswordTransformationMethod.getInstance());
            ivShow.setVisibility(View.GONE);
            ivHide.setVisibility(View.VISIBLE);
        }
        // 最后将光标移至字符串尾部
        CharSequence charSequence = etPsw.getText();
        if (charSequence instanceof Spannable) {
            Spannable spanText = (Spannable) charSequence;
            Selection.setSelection(spanText, charSequence.length());
        }

    }

    /**
     * 验证手机格式
     */
    public boolean isMobileNO(String mobiles) {
        String telRegex = "[1][3578]\\d{9}";
        return mobiles.matches(telRegex);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterAllEventHandler();
    }

    class TextChange implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            boolean Sign1 = etPhone.getText().length() > 0;
            boolean Sign2 = etCode.length() > 0;
            boolean Sign3 = etPsw.getText().length() > 0;

            if (Sign1 & Sign2 & Sign3) {
                btnRegister.setEnabled(true);
            } else {
                btnRegister.setEnabled(false);
            }
        }
    }
}

