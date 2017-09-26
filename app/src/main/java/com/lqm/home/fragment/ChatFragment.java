package com.lqm.home.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lqm.home.R;
import com.lqm.home.activity.MainActivity;
import com.lqm.home.activity.NewFriendActivity;
import com.lqm.home.activity.ScanActivity;
import com.lqm.home.activity.TeamCheatCreateActvitiy;
import com.lqm.home.adapter.TabViewPagerAdapter;
import com.lqm.home.broadcast.AuthBroadcastReceiver;
import com.lqm.home.broadcast.UpdateTeamListEvent;
import com.lqm.home.factory.PopupWindowFactory;
import com.lqm.home.nimsdk.NimAccountSDK;
import com.lqm.home.nimsdk.NimFriendSDK;
import com.lqm.home.nimsdk.NimSystemSDK;
import com.lqm.home.nimsdk.NimTeamSDK;
import com.lqm.home.nimsdk.NimUserInfoSDK;
import com.lqm.home.nimsdk.custom.CustomAttachParser;
import com.lqm.home.utilslqr.LogUtils;
import com.lqm.home.utilslqr.StringUtils;
import com.lqm.home.utilslqr.UIUtils;
import com.lqm.home.widget.Topbar;
import com.netease.nimlib.sdk.InvocationFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.friend.model.FriendChangedNotify;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SystemMessageType;
import com.netease.nimlib.sdk.msg.model.SystemMessage;
import com.netease.nimlib.sdk.team.model.Team;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;


/**
 * 聊天模块主界面
 */
public class ChatFragment extends BaseFragment {

    private TabLayout tab_chat_title;
    private ViewPager tab_chat_vp;
    private ArrayList<Fragment> list_fragment;
    private ArrayList<String> tabTitle;

    private static ChatMessageFragment chatMessageFragment;
    private ChatFriendFragment chatFriendFragment;
    private ChatGroupFragment chatGroupFragment;

    private AuthBroadcastReceiver mAuthBroadcastReceiver;
    private Observer<StatusCode> mOnlineStatusObserver;
    private List<SystemMessage> items = new ArrayList<>();//系统消息
    private MainActivity mActivity;
    private Topbar mChatTopBar;
    private PopupWindow mPopupWindow;
    private View mView;
    private TextView mTvNewFriendNotify;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mActivity = (MainActivity) activity;
    }

    public static ChatFragment newInstance() {
        return new ChatFragment();
    }

    @Override
    public void init() {
        //注册登录出错广播接收者
        registerBroadcastReceiver();
        //监听在线状态
        observerLineStatus();
        //监听用户信息更新
        observeUserInfoUpdate();
        //监听好友关系的变化
        observeFriendChangedNotify();
        //监听群聊关系的变化
        observeTeamChangedNotify();
        //监听系统消息通知
        observeReceiveSystemMsg();
        // 注册自定义附件解析器到
        NIMClient.getService(MsgService.class).registerCustomAttachmentParser(new CustomAttachParser());
    }

    @Override
    public View initView() {
        mView = View.inflate(getActivity(), R.layout.frag_chat, null);
        tab_chat_title = (TabLayout) mView.findViewById(R.id.tab_chat_title);
        tab_chat_vp = (ViewPager) mView.findViewById(R.id.tab_chat_vp);
        mChatTopBar = (Topbar) mView.findViewById(R.id.chat_topbar);
        mTvNewFriendNotify = (TextView)mView.findViewById(R.id.unread_new_friend);
        return mView;
    }

    @Override
    public void initData() {
        list_fragment = new ArrayList<>();
        tabTitle = new ArrayList<String>();
        tabTitle.add("消息");
        tabTitle.add("好友");
        tabTitle.add("群");
        for (int i = 0; i < tabTitle.size(); i++) {
            tab_chat_title.addTab(tab_chat_title.newTab().setText(tabTitle.get(i)));
        }

        chatMessageFragment = new ChatMessageFragment();
        chatFriendFragment = new ChatFriendFragment();
        chatGroupFragment = new ChatGroupFragment();

        list_fragment.add(chatMessageFragment);
        list_fragment.add(chatFriendFragment);
        list_fragment.add(chatGroupFragment);

        //设置TabLayout的模式
        tab_chat_title.setTabMode(TabLayout.MODE_FIXED);
        tab_chat_vp.setAdapter(new TabViewPagerAdapter(getChildFragmentManager(), list_fragment, tabTitle));
        tab_chat_vp.setOffscreenPageLimit(3);  //缓存页数
        tab_chat_title.setupWithViewPager(tab_chat_vp);

        //提示通讯录数据更新条目（未读数目）
        updateContactCount();

    }

    @Override
    public void initListener() {

        EventBus.getDefault().register(this);

        mChatTopBar.setTopbarOnClickListener(new Topbar.TopbarOnClickListener() {
            @Override
            public void leftOnClick() {
            }

            @Override
            public void rightOnClick() {
                showMenu();
            }
        });
    }

    private void showMenu() {
        View menuView = View.inflate(getActivity(), R.layout.popup_menu_main, null);
        //发起群聊
        menuView.findViewById(R.id.itemCreateGroupCheat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), TeamCheatCreateActvitiy.class));
                mPopupWindow.dismiss();
            }
        });
        //添加朋友
        menuView.findViewById(R.id.itemAddFriend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getActivity(), NewFriendActivity.class), MainActivity
                        .REQ_CLEAR_UNREAD);
                mPopupWindow.dismiss();
            }
        });
        //扫一扫
        menuView.findViewById(R.id.itemScan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ScanActivity.class));
                mPopupWindow.dismiss();
            }
        });
        mPopupWindow = PopupWindowFactory.getPopupWindowAtLocation
                (menuView, mView, Gravity.RIGHT | Gravity.TOP, UIUtils.dip2Px(0),
                        mChatTopBar.getHeight() + getStatusBarHeight());
    }

    // ======================   init ==================//

    /**
     * 注册广播接收者
     */
    private void registerBroadcastReceiver() {
        //登录出错广播接收者
        mAuthBroadcastReceiver = new AuthBroadcastReceiver();
        getActivity().registerReceiver(mAuthBroadcastReceiver,
                new IntentFilter(AuthBroadcastReceiver.ACTION));
    }

    /**
     * 反注册广播接收者
     */
    private void unRegisterBroadcastReceiver() {
        if (mAuthBroadcastReceiver != null) {
            getActivity().unregisterReceiver(mAuthBroadcastReceiver);
            mAuthBroadcastReceiver = null;
        }
    }

    /**
     * 监听在线状态
     */
    private void observerLineStatus() {
        mOnlineStatusObserver = new Observer<StatusCode>() {
            public void onEvent(StatusCode status) {
                LogUtils.sf("User status changed to: " + status);
                // 被踢出、账号被禁用、密码错误等情况，自动登录失败，需要返回到登录界面进行重新登录操作
                if (status.wontAutoLogin()) {
                    //发广播通知
                    Intent intent = new Intent();
                    intent.setAction(AuthBroadcastReceiver.ACTION);
                    intent.putExtra("status", status.getValue());
                    getActivity().sendBroadcast(intent);
                }
            }
        };
        NimAccountSDK.onlineStatusListen(
                mOnlineStatusObserver, true);
    }

    /**
     * 监听用户信息更新
     */
    private void observeUserInfoUpdate() {
        NimUserInfoSDK.observeUserInfoUpdate(new Observer<List<NimUserInfo>>() {
            @Override
            public void onEvent(List<NimUserInfo> nimUserInfos) {
//                mMeFragment.initData();
            }
        }, true);
    }

    /**
     * 监听好友关系的变化
     */
    private void observeFriendChangedNotify() {
        NimFriendSDK.observeFriendChangedNotify(new Observer<FriendChangedNotify>() {
            @Override
            public void onEvent(FriendChangedNotify friendChangedNotify) {
//                List<Friend> addedOrUpdatedFriends = friendChangedNotify.getAddedOrUpdatedFriends(); // 新增的好友
//                List<String> deletedFriendAccounts = friendChangedNotify.getDeletedFriends(); // 删除好友或者被解除好友

                //更新通讯录数据
                chatFriendFragment.initData();
            }
        }, true);
    }

    /**
     * 监听群聊关系的变化
     */
    private void observeTeamChangedNotify() {
        NimTeamSDK.observeTeamRemove(new Observer<Team>() {
            @Override
            public void onEvent(Team team) {
                chatMessageFragment.initData();
            }
        }, true);
        NimTeamSDK.observeTeamUpdate(new Observer<List<Team>>() {
            @Override
            public void onEvent(List<Team> teams) {
                chatMessageFragment.initData();
            }
        }, true);
    }

    /**
     * 监听系统消息通知
     */
    private void observeReceiveSystemMsg() {
        NimSystemSDK.observeReceiveSystemMsg(new Observer<SystemMessage>() {
            @Override
            public void onEvent(final SystemMessage systemMessage) {

                items.clear();
                List<SystemMessageType> types = new ArrayList<>();
                types.add(SystemMessageType.AddFriend);
                types.add(SystemMessageType.TeamInvite);
                InvocationFuture<List<SystemMessage>> listInvocationFuture = NimSystemSDK.querySystemMessageByType
                        (types, 0, 100);
                listInvocationFuture.setCallback(new RequestCallback<List<SystemMessage>>() {
                    @Override
                    public void onSuccess(List<SystemMessage> param) {
                        if (!StringUtils.isEmpty(param)) {
                            items.addAll(param);

                            //TODO:查询系统消息后返回数据的顺序问题
                            SystemMessage del = null;
                            for (SystemMessage m : items) {
                                if (m.getMessageId() != systemMessage.getMessageId() &&
                                        m.getFromAccount().equals(systemMessage.getFromAccount()) && m.getType() ==
                                        SystemMessageType.AddFriend) {
                                    del = m;
                                    break;
                                }
                            }
                            if (del != null) {
                                items.remove(del);
                                //删除本地系统消息中旧的一条
                                NimSystemSDK.deleteSystemMessage(del);
                            }

                            //提示通讯录数据更新条目
                            updateContactCount();
                            chatFriendFragment.updateHeaderViewUnreadCount();


                            //更新本地新朋友数据库信息
                            if (systemMessage.getType() == SystemMessageType.AddFriend) {
                                NimUserInfoSDK.getUserInfoFromServer(systemMessage.getFromAccount(), null);
                            }
                        }
                    }

                    @Override
                    public void onFailed(int code) {
                    }

                    @Override
                    public void onException(Throwable exception) {
                    }
                });
            }
        }, true);
    }
    // ======================   end ==================//

    /**
     * 提示通讯录数据更新条目（未读数目）
     */
    public void updateContactCount() {
        //新好友、被邀请入群 未读消息数
        List<SystemMessageType> types = new ArrayList<>();
        types.add(SystemMessageType.AddFriend);
        types.add(SystemMessageType.TeamInvite);
        int unreadCount = NimSystemSDK.querySystemMessageUnreadCountByType(types);
        if (unreadCount > 0) {
            mActivity.tvMessageCount.setVisibility(View.VISIBLE);
            mActivity.tvMessageCount.setText(String.valueOf(unreadCount));
            //
            mTvNewFriendNotify.setVisibility(View.VISIBLE);
            return;
        } else {
            mActivity.tvMessageCount.setVisibility(View.GONE);
            mTvNewFriendNotify.setVisibility(View.GONE);
        }
    }


    @Subscribe(threadMode = ThreadMode.MainThread)
    public void UpdateTeamList(UpdateTeamListEvent updateTeamListEvent) {
        //创建群聊后更新列表
        chatGroupFragment.initData();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateContactCount();
    }

    @Override
    public void onDestroy() {
        unRegisterBroadcastReceiver();
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
