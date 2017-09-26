package com.lqm.home.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import com.lqm.home.R;
import com.lqm.home.imageloader.ImageLoaderManager;
import com.lqm.home.model.UserCache;
import com.lqm.home.nimsdk.NimTeamSDK;
import com.lqm.home.nimsdk.NimUserInfoSDK;
import com.lqm.home.utilslqr.UIUtils;
import com.lqr.adapter.LQRAdapterForRecyclerView;
import com.lqr.adapter.LQRViewHolderForRecyclerView;
import com.lqr.recyclerview.LQRRecyclerView;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.team.model.Team;
import com.netease.nimlib.sdk.team.model.TeamMember;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @创建者 CSDN_LQR
 * @描述 移除群成员界面
 */
public class TeamCheatRemoveMemberActivity extends BaseActivity {

    public static final String TEAMID = "teamId";
    public static final String REMOVE_TEAM_MEMBER = "remove_team_member";//踢人出群
    private String mTeamId;
    private Team mTeam;
    private List<TeamMember> mTeamMembers = new ArrayList<>();
    private ArrayList<String> mWillBeRemovedAccounts = new ArrayList<>();
    private LQRAdapterForRecyclerView<TeamMember> mAdapter;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.btnOk)
    Button mBtnOk;

    @Bind(R.id.etKey)
    EditText mEtKey;
    @Bind(R.id.rvMember)
    LQRRecyclerView mRvMember;

    @OnClick({R.id.btnOk})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.btnOk:
                if (mWillBeRemovedAccounts.size() > 0) {
                    Intent intent = new Intent();
                    intent.putStringArrayListExtra(REMOVE_TEAM_MEMBER, mWillBeRemovedAccounts);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        initView();
        initData();
    }

    public void init() {
        mTeamId = getIntent().getStringExtra(TEAMID);
//        if (TextUtils.isEmpty(mTeamId)) {
//            interrupt();
//        }

        mTeam = NimTeamSDK.queryTeamBlock(mTeamId);
    }

    public void initView() {
        setContentView(R.layout.activity_team_cheat_remove_member);
        ButterKnife.bind(this);
        initToolbar();
    }

    public void initData() {
        //获取群成员
        NimTeamSDK.queryMemberList(mTeamId, new RequestCallback<List<TeamMember>>() {
            @Override
            public void onSuccess(List<TeamMember> param) {
                mTeamMembers.clear();
                mTeamMembers.addAll(param);

                //调整群主位置置顶
                int creatorPosi = -1;
                for (int i = 0; i < param.size(); i++) {
                    TeamMember tm = param.get(i);
                    if (mTeam.getCreator().equals(tm.getAccount())) {
                        creatorPosi = i;
                        break;
                    }
                }
                if (creatorPosi != -1) {
                    mTeamMembers.remove(creatorPosi);
                    mTeamMembers.add(0, param.get(creatorPosi));
                }
                setAdapter();
            }

            @Override
            public void onFailed(int code) {
                UIUtils.showToast("获取群成员失败" + code);
            }

            @Override
            public void onException(Throwable exception) {
                exception.printStackTrace();
            }
        });
    }

    private void setAdapter() {
        if (mAdapter == null) {
            mAdapter = new LQRAdapterForRecyclerView<TeamMember>(this, R.layout.item_contact_cv, mTeamMembers) {
                @Override
                public void convert(LQRViewHolderForRecyclerView helper, final TeamMember item, int position) {
                    helper.setText(R.id.tvName, NimTeamSDK.getTeamMemberDisplayNameWithoutMe(item.getTid(), item.getAccount()));
                    ImageView ivHeader = helper.getView(R.id.ivHeader);
                    NimUserInfo userInfo = NimUserInfoSDK.getUser(item.getAccount());
                    if (userInfo != null && !TextUtils.isEmpty(userInfo.getAvatar())) {
                        ImageLoaderManager.LoadNetImage(userInfo.getAvatar(), ivHeader);
                    } else {
                        ivHeader.setImageResource(R.mipmap.default_header);
                    }
                    final CheckBox cb = helper.getView(R.id.cb);
                    if (UserCache.getAccount().equals(item.getAccount())) {
                        cb.setVisibility(View.GONE);
                    } else {
                        cb.setVisibility(View.VISIBLE);
                    }

                    helper.getView(R.id.root).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (UserCache.getAccount().equals(item.getAccount())) {
                                return;
                            } else {
                                if (cb.isChecked()) {
                                    cb.setChecked(false);
                                    mWillBeRemovedAccounts.remove(item.getAccount());
                                } else {
                                    cb.setChecked(true);
                                    mWillBeRemovedAccounts.add(item.getAccount());
                                }
                            }
                        }
                    });
                }
            };
            mRvMember.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("聊天成员(" + mTeam.getMemberCount() + ")");
        mToolbar.setNavigationIcon(R.mipmap.ic_back);
        mBtnOk.setVisibility(View.VISIBLE);
        mBtnOk.setText("删除");
//        mBtnOk.setBackgroundResource(R.drawable.shape_btn_delete);
    }
}
