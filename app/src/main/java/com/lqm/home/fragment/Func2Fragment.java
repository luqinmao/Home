package com.lqm.home.fragment;

import android.view.View;
import android.widget.LinearLayout;

import com.lqm.home.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @创建者 CSDN_LQR
 * @描述 聊天界面功能页面2
 */
public class Func2Fragment extends BaseFragment {

    @Bind(R.id.llVoice)
    LinearLayout mLlVoice;

    @Override
    public View initView() {
        View view = View.inflate(getActivity(), R.layout.frag_func_page2, null);
        ButterKnife.bind(this, view);
        return view;
    }
}
