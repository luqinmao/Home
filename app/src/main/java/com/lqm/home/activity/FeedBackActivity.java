package com.lqm.home.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;

import com.lqm.home.R;
import com.lqm.home.app.AppConst;
import com.lqm.home.model.UserCache;
import com.lqm.home.utils.T;
import com.lqm.home.widget.Topbar;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 反馈页面
 */

public class FeedBackActivity extends BaseActivity {

    @Bind(R.id.topbar_about)
    Topbar topbarAbout;
    @Bind(R.id.et_content)
    EditText etContent;
    @Bind(R.id.et_contact)
    EditText etContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        ButterKnife.bind(this);


        initView();
    }

    private void initView() {

        topbarAbout.setTopbarOnClickListener(new Topbar.TopbarOnClickListener() {
            @Override
            public void leftOnClick() {
                finish();
            }

            @Override
            public void rightOnClick() {
                showWaitingDialog("反馈中，请稍等...");
                String content = etContent.getText().toString();
                String userContact = etContact.getText().toString();

                if (TextUtils.isEmpty(content) || TextUtils.isEmpty(userContact)){
                    T.showShort(FeedBackActivity.this,"请填写信息");
                    hideWaitingDialog();
                    return;
                }

                OkGo.post(AppConst.FEEDBACK)
                        .params("feedbackcontent",content+"")
                        .params("username",UserCache.getAccount())
                        .params("userContact",userContact)
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(String s, Call call, Response response) {
                                T.showShort(FeedBackActivity.this,"反馈成功");
                                etContent.setText("");
                                etContact.setText("");
                            }

                            @Override
                            public void onError(Call call, Response response, Exception e) {
                                T.showShort(FeedBackActivity.this,"反馈失败");
                            }

                            @Override
                            public void onAfter(String s, Exception e) {
                                hideWaitingDialog();
                            }
                        });

            }
        });
    }
}
