package com.lqm.home.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lqm.home.R;
import com.lqm.home.broadcast.UpdateTeamListEvent;
import com.lqm.home.imageloader.ImageLoaderManager;
import com.lqm.home.model.Contact;
import com.lqm.home.nimsdk.NimFriendSDK;
import com.lqm.home.nimsdk.NimTeamSDK;
import com.lqm.home.nimsdk.NimUserInfoSDK;
import com.lqm.home.utilslqr.SortUtils;
import com.lqm.home.utilslqr.StringUtils;
import com.lqm.home.utilslqr.UIUtils;
import com.lqm.home.widget.QuickIndexBar;
import com.lqr.adapter.LQRAdapterForRecyclerView;
import com.lqr.adapter.LQRViewHolderForRecyclerView;
import com.lqr.recyclerview.LQRRecyclerView;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.friend.model.Friend;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.team.constant.TeamFieldEnum;
import com.netease.nimlib.sdk.team.constant.TeamTypeEnum;
import com.netease.nimlib.sdk.team.model.Team;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

import static com.lqm.home.R.id.ivHeader;


/**
 * @创建者 CSDN_LQR
 * @描述 发起群聊
 */
public class TeamCheatCreateActvitiy extends BaseActivity {

    public static final String ADD_TEAM_MEMBER = "add_team_member";//拉人入群
    private boolean isAddTeamMemberMode = false;//标记当前是否是拉人入群模式（默认是创建群）
    private List<String> mSelectedTeamMemberAccounts = new ArrayList<>();//已经在群中的成员账号

    private List<Contact> mContacts = new ArrayList<>();
    private List<Friend> mFriends = new ArrayList<>();
    private LQRAdapterForRecyclerView<Contact> mAdapter;
    private int i;

    private List<Contact> mSelectedContacts = new ArrayList<>();//选中的联系人
    private LQRAdapterForRecyclerView<Contact> mSelectedContactsAdapter;
    private Drawable mSearchDrawable;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.btnOk)
    Button mBtnOk;

    @Bind(R.id.rvSelectedContacts)
    LQRRecyclerView mRvSelectedContacts;
    @Bind(R.id.etKey)
    EditText mEtKey;
    @Bind(R.id.vTop)
    View mVTop;

    @Bind(R.id.rvContacts)
    LQRRecyclerView mRvContacts;
    @Bind(R.id.quickIndexBar)
    QuickIndexBar mQuickIndexBar;
    @Bind(R.id.tvLetter)
    TextView mTvLetter;

    @OnClick({R.id.btnOk})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.btnOk:
                if (mSelectedContacts.size() == 0)
                    return;
                ArrayList<String> accounts = new ArrayList<>(mSelectedContacts.size());
                for (Contact contact : mSelectedContacts) {
                    accounts.add(contact.getAccount());
                }

                if (isAddTeamMemberMode) {
                    Intent intent = new Intent();
                    intent.putStringArrayListExtra(ADD_TEAM_MEMBER, accounts);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    showWaitingDialog("正在发起群聊");
                    HashMap<TeamFieldEnum, Serializable> fields = new HashMap<>();
//                fields.put(TeamFieldEnum.Name, "群聊(" + accounts.size() + 1 + ")");
                    NimTeamSDK.createTeam(fields, TeamTypeEnum.Normal, accounts, new RequestCallback<Team>() {
                        @Override
                        public void onSuccess(Team param) {
                            hideWaitingDialog();
                            //更新群聊列表
                            EventBus.getDefault().post(new UpdateTeamListEvent());

                            //跳转到SessionActivity
                            Intent intent = new Intent(TeamCheatCreateActvitiy.this, SessionActivity.class);
                            intent.putExtra(SessionActivity.SESSION_ACCOUNT, param.getId());
                            intent.putExtra(SessionActivity.SESSION_TYPE, SessionTypeEnum.Team);
                            startActivity(intent);
                            setResult(RESULT_OK);
                            finish();
                        }

                        @Override
                        public void onFailed(int code) {
                            UIUtils.showToast("建群失败" + code);
                            hideWaitingDialog();
                        }

                        @Override
                        public void onException(Throwable exception) {
                            exception.printStackTrace();
                            hideWaitingDialog();
                        }
                    });
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
        initListener();
    }

    public void init() {
        //已经在群中的成员账号
        ArrayList<String> stringArrayListExtra = getIntent().getStringArrayListExtra(ADD_TEAM_MEMBER);
        if (stringArrayListExtra == null) {
            isAddTeamMemberMode = false;
        } else {
            isAddTeamMemberMode = true;
        }
        if (!StringUtils.isEmpty(stringArrayListExtra)) {
            mSelectedTeamMemberAccounts.addAll(stringArrayListExtra);
        }
    }

    public void initView() {
        setContentView(R.layout.activity_team_cheat_create);
        ButterKnife.bind(this);

        initToolbar();

        //得到搜索框的左图标
        mSearchDrawable = UIUtils.getResource().getDrawable(R.mipmap.ic_search1);
        mSearchDrawable.setBounds(0, 0, mSearchDrawable.getMinimumWidth(), mSearchDrawable.getMinimumHeight());
    }

    public void initData() {
        try {
            mFriends.clear();
            mContacts.clear();

            //得到好友列表
            List<Friend> friends = NimFriendSDK.getFriends();
            if (!StringUtils.isEmpty(friends)) {
                mFriends.addAll(friends);

                //得到本地没有信息的账号
                List<String> accountList = new ArrayList<>();
                for (int i = 0; i < mFriends.size(); i++) {
                    String account = mFriends.get(i).getAccount();
                    if (NimUserInfoSDK.getUser(account) == null) {
                        accountList.add(account);
                    }
                }

                //从服务器上获取用户信息
                if (!StringUtils.isEmpty(accountList)) {
                    NimUserInfoSDK.getUserInfosFormServer(accountList, new RequestCallback<List<NimUserInfo>>() {
                        @Override
                        public void onSuccess(List<NimUserInfo> param) {
                            setDataAndUpdateView();
                        }

                        @Override
                        public void onFailed(int code) {
                            UIUtils.showToast("获取联系人信息失败" + code);
                        }

                        @Override
                        public void onException(Throwable exception) {
                            exception.printStackTrace();
                        }
                    });
                } else {
                    setDataAndUpdateView();
                }
            } else {
                setDataAndUpdateView();
            }
        } catch (Exception e) {
            e.printStackTrace();
            initData();
        }

        setSelectedContactsAdapter();
    }

    public void initListener() {
        mQuickIndexBar.setListener(new QuickIndexBar.OnLetterUpdateListener() {
            @Override
            public void onLetterUpdate(String letter) {
                //显示字母提示
                showLetter(letter);

                //滑动对对应字母条目处
                if ("↑".equalsIgnoreCase(letter)) {
                    mRvContacts.moveToPosition(0);
                } else if ("☆".equalsIgnoreCase(letter)) {
                    mRvContacts.moveToPosition(0);
                } else {
                    //找出第一个对应字母的位置后，滑动到指定位置
                    for (i = 0; i < mContacts.size(); i++) {
                        Contact contact = mContacts.get(i);
                        String c = contact.getPinyin().charAt(0) + "";
                        if (c.equalsIgnoreCase(letter)) {
                            mRvContacts.moveToPosition(i);
                            break;
                        }
                    }
                }
            }
        });
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
        getSupportActionBar().setTitle("发起群聊");
        mToolbar.setNavigationIcon(R.mipmap.ic_back);
        mBtnOk.setVisibility(View.VISIBLE);
        mBtnOk.setText("确定");
    }


    private void setDataAndUpdateView() {
        if (mFriends != null) {
            for (int i = 0; i < mFriends.size(); i++) {
                mContacts.add(new Contact(mFriends.get(i).getAccount()));
            }
            //整理排序
            SortUtils.sortContacts(mContacts);
        }
        setContactsAdapter();
    }

    /**
     * 设置联系人列表适配器
     */
    private void setContactsAdapter() {
        mAdapter = new LQRAdapterForRecyclerView<Contact>(this, R.layout.item_contact_cv, mContacts) {
            @Override
            public void convert(final LQRViewHolderForRecyclerView helper, final Contact item, int position) {
                helper.setText(R.id.tvName, TextUtils.isEmpty(item.getAlias()) ? item.getName() : item.getAlias());
                if (!TextUtils.isEmpty(item.getAvatar())) {
                    ImageLoaderManager.LoadNetImage(item.getAvatar(), (ImageView) helper.getView(ivHeader));
                } else {
                    helper.setImageResource(ivHeader, R.mipmap.default_header);
                }

                String str = "";
                //得到当前字母
                String currentLetter = item.getPinyin().charAt(0) + "";

                if (position == 0) {
                    str = currentLetter;
                } else {
                    //得到上一个字母
                    String preLetter = mContacts.get(position - 1).getPinyin().charAt(0) + "";
                    //如果和上一个字母的首字母不同则显示字母栏
                    if (!preLetter.equalsIgnoreCase(currentLetter)) {
                        str = currentLetter;
                    }

                    int nextIndex = position + 1;
                    if (nextIndex < mContacts.size() - 1) {
                        //得到下一个字母
                        String nextLetter = mContacts.get(nextIndex).getPinyin().charAt(0) + "";
                        //如果和下一个字母的首字母不同则隐藏下划线
                        if (!nextLetter.equalsIgnoreCase(currentLetter)) {
                            helper.setViewVisibility(R.id.vLine, View.INVISIBLE);
                        } else {
                            helper.setViewVisibility(R.id.vLine, View.VISIBLE);
                        }
                    } else {
                        helper.setViewVisibility(R.id.vLine, View.INVISIBLE);
                    }
                }
                if (position == mContacts.size() - 1) {
                    helper.setViewVisibility(R.id.vLine, View.GONE);
                }

                //根据str是否为空决定字母栏是否显示
                if (TextUtils.isEmpty(str)) {
                    helper.setViewVisibility(R.id.tvIndex, View.GONE);
                } else {
                    helper.setViewVisibility(R.id.tvIndex, View.VISIBLE);
                    helper.setText(R.id.tvIndex, currentLetter);
                }


                final CheckBox cb = helper.getView(R.id.cb);
                helper.setViewVisibility(R.id.cb, View.VISIBLE);

                if (isAddTeamMemberMode) {
                    //判断当前的联系人是否已经在群中，是则显示灰色勾选图标
                    if (mSelectedTeamMemberAccounts.contains(item.getAccount())) {
                        cb.setEnabled(false);
                        cb.setChecked(true);
                    } else {
                        cb.setEnabled(true);
                    }
                }

                //条目点击勾选好友
                helper.getView(R.id.root).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isAddTeamMemberMode) {
                            //判断当前的联系人是否已经在群中，是否点击无效
                            if (mSelectedTeamMemberAccounts.contains(item.getAccount())) {
                                return;
                            }
                        }

                        if (cb.isChecked()) {
                            cb.setChecked(false);
                            //去掉选中项
                            mSelectedContactsAdapter.removeItem(item);
                        } else {
                            cb.setChecked(true);
                            //增加选中项
                            mSelectedContactsAdapter.addLastItem(item);
                        }
                        mBtnOk.setText("确定" + (mSelectedContacts.size() > 0 ? "(" + mSelectedContacts.size() + ")" : ""));
                        //根据选中联系人的个数显隐藏搜索框左图标
                        if (mSelectedContacts.size() > 0) {
                            mEtKey.setCompoundDrawables(null, null, null, null);
                        } else {
                            mEtKey.setCompoundDrawables(mSearchDrawable, null, null, null);
                        }
                    }
                });

            }
        };
        //设置适配器
        if (mRvContacts != null)
            mRvContacts.setAdapter(mAdapter.getHeaderAndFooterAdapter());
    }

    /**
     * 设置被选中联系人头像列表适配器
     */
    private void setSelectedContactsAdapter() {
//        for (int i = 0; i < 10; i++) {
//            mSelectedContacts.add(new Contact());
//        }
        if (mSelectedContactsAdapter == null) {
            mSelectedContactsAdapter = new LQRAdapterForRecyclerView<Contact>(this, R.layout.item_selected_contact_rv, mSelectedContacts) {
                @Override
                public void convert(LQRViewHolderForRecyclerView helper, Contact item, int position) {

                    //动态设置列表宽度
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mRvSelectedContacts.getLayoutParams();
//                    params.weight = mSelectedContacts.size() > 5 ? 4 : 0;
                    int parentWidth = ((LinearLayout) mRvSelectedContacts.getParent()).getWidth();
                    int childWidth = parentWidth * 4 / 5;
                    params.width = mSelectedContacts.size() > 5 ? childWidth : params.WRAP_CONTENT;
                    mRvSelectedContacts.setLayoutParams(params);

                    ImageView ivHeader = helper.getView(R.id.ivHeader);
                    if (TextUtils.isEmpty(item.getAvatar())) {
                        ivHeader.setImageResource(R.mipmap.default_header);
                    } else {
                        ImageLoaderManager.LoadNetImage(item.getAvatar(), ivHeader);
                    }
                }
            };
            mRvSelectedContacts.setAdapter(mSelectedContactsAdapter);
        } else {
            mSelectedContactsAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 显示所触摸到的字母
     *
     * @param letter
     */
    protected void showLetter(String letter) {
        mTvLetter.setVisibility(View.VISIBLE);// 设置为可见
        mTvLetter.setText(letter);

        UIUtils.getMainThreadHandler().removeCallbacksAndMessages(null);
        UIUtils.postTaskDelay(new Runnable() {
            @Override
            public void run() {
                mTvLetter.setVisibility(View.GONE);
            }
        }, 500);
    }
}
