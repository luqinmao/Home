package com.lqm.home.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lqm.home.R;
import com.lqm.home.activity.SessionActivity;
import com.lqm.home.imageloader.ImageLoaderManager;
import com.lqm.home.nimsdk.NimTeamSDK;
import com.lqm.home.nimsdk.NimUserInfoSDK;
import com.lqm.home.utilslqr.UIUtils;
import com.lqr.adapter.LQRAdapterForRecyclerView;
import com.lqr.adapter.LQRViewHolderForRecyclerView;
import com.lqr.ninegridimageview.LQRNineGridImageView;
import com.lqr.ninegridimageview.LQRNineGridImageViewAdapter;
import com.lqr.recyclerview.LQRRecyclerView;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.team.model.Team;
import com.netease.nimlib.sdk.team.model.TeamMember;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 群
 */

public class ChatGroupFragment extends BaseFragment {

    private LinearLayout mLlContent;
    private TextView mTvTip;
    private LQRRecyclerView mRvTeamList;

    private List<Team> mMyTeamList = new ArrayList<>();
    private LQRAdapterForRecyclerView<Team> mAdapter;
    private TextView mHeaderView;
    private TextView mFooterTv;
    private LQRNineGridImageViewAdapter<NimUserInfo> mNineGridAdapter;

    @Override
    public View initView() {
        View view = View.inflate(getActivity(), R.layout.activity_team_cheat_list, null);
        mLlContent = (LinearLayout) view.findViewById(R.id.llContent);
        mTvTip = (TextView) view.findViewById(R.id.tvTip);
        mRvTeamList = (LQRRecyclerView) view.findViewById(R.id.rvTeamList);


        initHeaderViewAndFooterView();
        mNineGridAdapter = new LQRNineGridImageViewAdapter<NimUserInfo>() {
            @Override
            protected void onDisplayImage(Context context, ImageView imageView, NimUserInfo userInfo) {
                if (!TextUtils.isEmpty(userInfo.getAvatar())) {
                    ImageLoaderManager.LoadNetImage(userInfo.getAvatar(), imageView);
                } else {
                    imageView.setImageResource(R.mipmap.default_header);
                }
            }
        };
        return view;
    }

    @Override
    public void initData() {
        NimTeamSDK.queryTeamList(new RequestCallbackWrapper<List<Team>>() {
            @Override
            public void onResult(int code, List<Team> result, Throwable exception) {
                if (code == ResponseCode.RES_SUCCESS && result != null && exception == null) {
                    mLlContent.setVisibility(View.VISIBLE);
                    mTvTip.setVisibility(View.GONE);

                    mMyTeamList.clear();
                    mMyTeamList.addAll(result);

                    setAdapter();

                } else {
                    mLlContent.setVisibility(View.GONE);
                    mTvTip.setVisibility(View.VISIBLE);
                    exception.printStackTrace();
                }
            }
        });
    }

    private void initHeaderViewAndFooterView() {
        mHeaderView = new TextView(getActivity());
        ViewGroup.LayoutParams params1 = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, UIUtils
                .dip2Px(23));
        mHeaderView.setBackgroundColor(Color.parseColor("#E5E5E5"));
        mHeaderView.setGravity(Gravity.CENTER_VERTICAL);
        mHeaderView.setPadding(UIUtils.dip2Px(15), 0, 0, 0);
        mHeaderView.setText("群聊");
        mHeaderView.setTextColor(Color.parseColor("#989898"));
        mHeaderView.setTextSize(13);
        mHeaderView.setLayoutParams(params1);

        mFooterTv = new TextView(getActivity());
        ViewGroup.LayoutParams params2 = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, UIUtils
                .dip2Px(50));
        mFooterTv.setLayoutParams(params2);
        mFooterTv.setGravity(Gravity.CENTER);
    }

    private void setAdapter() {
        if (mAdapter == null) {
            mAdapter = new LQRAdapterForRecyclerView<Team>(getActivity(), R.layout.item_contact_cv, mMyTeamList) {
                @Override
                public void convert(final LQRViewHolderForRecyclerView helper, final Team item, int position) {
                    helper.setViewVisibility(R.id.ivHeader, View.GONE)
                            .setViewVisibility(R.id.ngiv, View.GONE);
                    final LQRNineGridImageView ngivHeader = helper.getView(R.id.ngiv);
                    NimTeamSDK.queryMemberList(item.getId(), new RequestCallback<List<TeamMember>>() {
                                @Override
                                public void onSuccess(List<TeamMember> memberList) {
                                    //设置群聊名称
                                    if (!TextUtils.isEmpty(item.getName()))
                                        helper.setText(R.id.tvName, item.getName());
                                    else {
                                        StringBuilder sb = new StringBuilder();
                                        for (int i = 0; i < memberList.size(); i++) {
                                            TeamMember member = memberList.get(i);
                                            sb.append(NimTeamSDK.getTeamMemberDisplayNameWithYou(item.getId(), member
                                                    .getAccount()));
                                            if (i != memberList.size() - 1) {
                                                sb.append("、");
                                            }
                                        }
                                        helper.setText(R.id.tvName, sb.toString());
                                    }

                                    //设置群聊的头像
                                    if (memberList != null && memberList.size() > 0) {
                                        List<String> accounts = new ArrayList<>();
                                        int count = memberList.size() > 9 ? 9 : memberList.size();
                                        for (int i = 0; i < count; i++) {
                                            accounts.add(memberList.get(i).getAccount());
                                        }
                                        NimUserInfoSDK.getUserInfosFormServer(accounts, new
                                                RequestCallback<List<NimUserInfo>>() {
                                                    @Override
                                                    public void onSuccess(List<NimUserInfo> result) {
                                                        ngivHeader.setAdapter(mNineGridAdapter);
                                                        ngivHeader.setImagesData(result);
                                                    }

                                                    @Override
                                                    public void onFailed(int code) {

                                                    }

                                                    @Override
                                                    public void onException(Throwable exception) {

                                                    }
                                                });
                                    }
                                }

                                @Override
                                public void onFailed(int code) {

                                }

                                @Override
                                public void onException(Throwable exception) {
                                    exception.printStackTrace();
                                }
                            }

                    );


                    //条目点击事件
                    helper.getView(R.id.root).
                            setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View v) {
                                       //跳转到SessionActivity
                                       Intent intent = new Intent(getActivity(), SessionActivity.class);
                                       intent.putExtra(SessionActivity.SESSION_ACCOUNT, item.getId());
                                       intent.putExtra(SessionActivity.SESSION_TYPE, SessionTypeEnum
                                               .Team);
                                       startActivity(intent);
//                                                       finish();
                                   }
                                }

                            );
                }
            }
            ;

            mAdapter.addHeaderView(mHeaderView);
            mFooterTv.setText(mMyTeamList.size() + "个群聊");
            mAdapter.addFooterView(mFooterTv);
            mRvTeamList.setAdapter(mAdapter.getHeaderAndFooterAdapter());
        } else {
            mAdapter.notifyDataSetChanged();
            mFooterTv.setText(mMyTeamList.size() + "个群聊");
            mAdapter.getHeaderAndFooterAdapter().notifyDataSetChanged();
        }
    }
}
