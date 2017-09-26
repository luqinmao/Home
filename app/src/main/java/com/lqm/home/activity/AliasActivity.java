package com.lqm.home.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.lqm.home.R;
import com.lqm.home.model.Contact;
import com.lqm.home.nimsdk.NimFriendSDK;
import com.lqm.home.utilslqr.UIUtils;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.friend.constant.FriendFieldEnum;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @创建者 CSDN_LQR
 * @描述 修改备注信息
 */
public class AliasActivity extends BaseActivity {

    private String alias;
    private Contact mContact;

    public static final int REQ_CHANGE_ALIAS = 100;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.btnOk)
    Button mBtnOk;
    @Bind(R.id.etAlias)
    EditText mEtAlias;
    @Bind(R.id.ibClearAlias)
    ImageButton mIbClearAlias;
    @Bind(R.id.etTag)
    EditText mEtTag;
    @Bind(R.id.ibClearTag)
    ImageButton mIbClearTag;
    @Bind(R.id.etPhone)
    EditText mEtPhone;
    @Bind(R.id.ibClearPhone)
    ImageButton mIbClearPhone;
    @Bind(R.id.etDesc)
    EditText mEtDesc;
    @Bind(R.id.ibClearDesc)
    ImageButton mIbClearDesc;
    @Bind(R.id.etPicture)
    EditText mEtPicture;
    @Bind(R.id.ibClearPicture)
    ImageButton mIbClearPicture;

    @OnClick({R.id.btnOk})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.btnOk:
                saveAliasChange();
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
        mContact = (Contact) getIntent().getSerializableExtra("contact");
        if (mContact == null) {
//            interrupt();
            return;
        }
    }

    public void initView() {
        setContentView(R.layout.activity_alias);
        ButterKnife.bind(this);
        initToolbar();

        String alias = mContact.getFriend().getAlias();
        if (!TextUtils.isEmpty(alias)) {
            mEtAlias.setText(alias);
            mEtAlias.setSelection(alias.length());
        }
    }

    public void initData() {
        alias = mContact.getFriend().getAlias();
    }

    public void initListener() {
        mEtAlias.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mIbClearAlias.setVisibility(View.VISIBLE);
                } else {
                    mIbClearAlias.setVisibility(View.GONE);
                }
            }
        });
        mIbClearAlias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEtAlias.setText("");
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (!alias.equals(mEtAlias.getText().toString().trim())) {
            showMaterialDialog("", "保存本次编辑?", "保存", "不保存", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveAliasChange();
                    hideMaterialDialog();
                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideMaterialDialog();
                }
            });
            return;
        }
        super.onBackPressed();
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
        getSupportActionBar().setTitle("备注信息");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationIcon(R.mipmap.ic_back);
        mBtnOk.setVisibility(View.VISIBLE);
    }

    private void saveAliasChange() {
        String alias = mEtAlias.getText().toString().trim();
        showWaitingDialog("请稍等");
        Map<FriendFieldEnum, Object> map = new HashMap<>(1);
        map.put(FriendFieldEnum.ALIAS, alias);
        NimFriendSDK.updateFriendFields(mContact.getAccount(), map, new RequestCallback<Void>() {
            @Override
            public void onSuccess(Void param) {
                UIUtils.showToast("修改备注信息成功");
                hideWaitingDialog();
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onFailed(int code) {
                UIUtils.showToast("修改备注信息失败" + code);
                hideWaitingDialog();
            }

            @Override
            public void onException(Throwable exception) {
                exception.printStackTrace();
                hideWaitingDialog();
            }
        });
    }
}
