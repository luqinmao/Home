package com.lqm.home.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.lqm.home.R;
import com.lqm.home.nimsdk.NimTeamSDK;
import com.lqm.home.utilslqr.UIUtils;
import com.netease.nimlib.sdk.InvocationFuture;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.team.constant.TeamFieldEnum;
import com.netease.nimlib.sdk.team.model.Team;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * @创建者 CSDN_LQR
 * @描述 群名片界面
 */
public class TeamNameSetActivity extends BaseActivity {

    public static final String TEAM_ID = "teamId";

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.btnOk)
    Button mBtnOk;

    @Bind(R.id.etName)
    EditText mEtName;
    private String mTeamId;
    private Team mTeam;

    @OnClick(R.id.btnOk)
    public void click() {
        final String teamName = mEtName.getText().toString().trim();
//        if (!TextUtils.isEmpty(teamName)) {
            showWaitingDialog("修改群名片");
            Map<TeamFieldEnum, Serializable> fields = new HashMap<>(1);
            fields.put(TeamFieldEnum.Name, teamName);
            InvocationFuture<Void> invocationFuture = NimTeamSDK.updateTeamFields(mTeamId, fields);
            invocationFuture.setCallback(new RequestCallback<Void>() {
                @Override
                public void onSuccess(Void param) {
                    hideWaitingDialog();
                    finish();
                }

                @Override
                public void onFailed(int code) {
                    UIUtils.showToast("修改群名片失败" + code);
                    hideWaitingDialog();
                }

                @Override
                public void onException(Throwable exception) {
                    exception.printStackTrace();
                    hideWaitingDialog();
                }
            });
//        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        initView();
    }

    public void init() {
        mTeamId = getIntent().getStringExtra(TEAM_ID);
//        if (TextUtils.isEmpty(mTeamId)) {
//            interrupt();
//            return;
//        }

        mTeam = NimTeamSDK.queryTeamBlock(mTeamId);
    }

    public void initView() {
        setContentView(R.layout.activity_team_name_set);
        ButterKnife.bind(this);

        initToolbar();
        mEtName.setText(mTeam.getName());
        mEtName.setSelection(mTeam.getName().length());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("群名片");
        mToolbar.setNavigationIcon(R.mipmap.ic_back);

        mBtnOk.setVisibility(View.VISIBLE);
        mBtnOk.setText("保存");
    }
}
