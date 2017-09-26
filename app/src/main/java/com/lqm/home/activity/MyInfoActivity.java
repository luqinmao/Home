package com.lqm.home.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lqm.home.R;
import com.lqm.home.app.App;
import com.lqm.home.imageloader.ImageLoaderManager;
import com.lqm.home.model.UserCache;
import com.lqm.home.nimsdk.NimUserInfoSDK;
import com.lqm.home.utilslqr.UIUtils;
import com.lqm.home.widget.CustomDialog;
import com.lqm.home.widget.OptionItemView;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.uinfo.constant.GenderEnum;
import com.netease.nimlib.sdk.uinfo.constant.UserInfoFieldEnum;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.lqm.home.activity.SessionActivity.IMAGE_PICKER;

/**
 * 自己的用户信息界面
 */
public class MyInfoActivity extends BaseActivity {

    Intent mIntent;
    private NimUserInfo mNimUserInfo;

    private View mGenderDialogView;
    private CustomDialog mDialog;
    private TextView mTvMale;
    private TextView mTvFemale;
    private Drawable mSelectedDrawable;
    private Drawable mUnSelectedDrawable;

    Observer<List<NimUserInfo>> userInfoUpdateObserver = new Observer<List<NimUserInfo>>() {
        @Override
        public void onEvent(List<NimUserInfo> nimUserInfos) {
            initData();
        }
    };

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.llHeader)
    LinearLayout mLlHeader;
    @Bind(R.id.ivHeader)
    ImageView mIvHeader;
    @Bind(R.id.oivName)
    OptionItemView mOivName;
    @Bind(R.id.oivQRCordCard)
    OptionItemView mOivQRCordCard;
    @Bind(R.id.oivAccount)
    OptionItemView mOivAccount;
    @Bind(R.id.oivGender)
    OptionItemView mOivGender;
    @Bind(R.id.ll_oivSignature)
    LinearLayout mllOivSignature;
    @Bind(R.id.tv_sign_text)
    TextView mTvSignText;

    OptionItemView mAddress;
    private int CODE_AREA = 10;

    @OnClick({R.id.llHeader, R.id.ivHeader, R.id.oivName, R.id.oivQRCordCard, R.id.oivGender,
            R.id.ll_oivSignature,R.id.address})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.llHeader:
                App.imagePicker.setMultiMode(false);
                mIntent = new Intent(this, ImageGridActivity.class);
                startActivityForResult(mIntent, IMAGE_PICKER);
                break;
            case R.id.ivHeader:
                if (mNimUserInfo == null)
                    return;
                mIntent = new Intent(this, ShowBigImageActivity.class);
                mIntent.putExtra("url", mNimUserInfo.getAvatar());
                mIntent.putExtra("from","myInfo");
                startActivity(mIntent);
                break;
            case R.id.oivName:
//                mIntent = new Intent(this, ChangeNameActivity.class);
//                mIntent.putExtra("name", mNimUserInfo.getName());
//                startActivity(mIntent);
                break;
            case R.id.oivQRCordCard:
                mIntent = new Intent(this, QRCodeCardActivity.class);
                mIntent.putExtra(QRCodeCardActivity.QRCODE_USER, mNimUserInfo);
                startActivity(mIntent);
                break;
            case R.id.oivGender:
                if (mGenderDialogView == null) {
                    mGenderDialogView = View.inflate(this, R.layout.dialog_gender, null);
                    mTvMale = (TextView) mGenderDialogView.findViewById(R.id.tvMale);
                    mTvFemale = (TextView) mGenderDialogView.findViewById(R.id.tvFemale);
                    mDialog = new CustomDialog(this, mGenderDialogView, R.style.dialog);
                    mTvMale.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            updateGender(GenderEnum.MALE);
                        }
                    });
                    mTvFemale.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            updateGender(GenderEnum.FEMALE);
                        }
                    });
                }
                updateGenderView(mNimUserInfo.getGenderEnum());
                mDialog.show();
                break;
            case R.id.ll_oivSignature:
                mIntent = new Intent(this, ChangeSignatureActivity.class);
                mIntent.putExtra("signature", mNimUserInfo.getSignature());
                startActivity(mIntent);
                break;

            case R.id.address:
//                startActivityForResult(new Intent(MyInfoActivity.this,
//                        RegionActivity.class), CODE_AREA);
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
        // 监听用户信息更新
        NimUserInfoSDK.observeUserInfoUpdate(userInfoUpdateObserver, true);

        mSelectedDrawable = UIUtils.getResource().getDrawable(R.mipmap.list_selected);
        mUnSelectedDrawable = UIUtils.getResource().getDrawable(R.mipmap.list_unselected);
        mSelectedDrawable.setBounds(0, 0, mSelectedDrawable.getMinimumWidth(), mSelectedDrawable.getMinimumHeight());
        mUnSelectedDrawable.setBounds(0, 0, mUnSelectedDrawable.getMinimumWidth(), mUnSelectedDrawable
                .getMinimumHeight());
    }

    @Override
    protected void onPause() {
        super.onPause();
        Intent intent = new Intent();
        setResult(MainActivity.CODE_SIGN, intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 销毁用户信息更新监听
        NimUserInfoSDK.observeUserInfoUpdate(userInfoUpdateObserver, false);
    }

    public void initView() {
        setContentView(R.layout.activity_edit_account);
        ButterKnife.bind(this);
        initToolbar();
    }

    public void initData() {
        mNimUserInfo = NimUserInfoSDK.getUser(UserCache.getAccount());
        if (mNimUserInfo == null) {
            getUserInfoFromRemote();
        } else {
            //头像
            if (!TextUtils.isEmpty(mNimUserInfo.getAvatar())) {
                ImageLoaderManager.LoadNetImage(mNimUserInfo.getAvatar(), mIvHeader);
            }
            //用户名、账号、签名、性别
            mOivName.setRightText(mNimUserInfo.getName());
            mOivAccount.setRightText(mNimUserInfo.getAccount());
            mTvSignText.setText(TextUtils.isEmpty(mNimUserInfo.getSignature()) ? "未填写" : mNimUserInfo
                    .getSignature());
            mOivGender.setRightText(mNimUserInfo.getGenderEnum() == GenderEnum.FEMALE ? "女" : mNimUserInfo
                    .getGenderEnum() == GenderEnum.MALE ? "男" : "");
        }
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {//返回多张照片
            if (data != null) {
                //是否发送原图
//                boolean isOrig = data.getBooleanExtra(ImagePreviewActivity.ISORIGIN, false);
                showWaitingDialog("上传头像...");
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker
                        .EXTRA_RESULT_ITEMS);
                if (images != null && images.size() > 0) {
                    //取第一张照片
                    File file = new File(images.get(0).path);
                    NimUserInfoSDK.uploadFile(file, "image/jpeg", new RequestCallbackWrapper<String>() {
                        @Override
                        public void onResult(int code, String url, Throwable exception) {

                            if (code == ResponseCode.RES_SUCCESS
                                    && !TextUtils.isEmpty(url)) {// 上传成功得到Url
                                Map<UserInfoFieldEnum, Object> fields = new HashMap<UserInfoFieldEnum, Object>(
                                        1);
                                fields.put(UserInfoFieldEnum.AVATAR, url);
                            }

                            Map<UserInfoFieldEnum, Object> fields = new HashMap(1);
                            fields.put(UserInfoFieldEnum.AVATAR, url);
                            NimUserInfoSDK.updateUserInfo(fields, new RequestCallbackWrapper<Void>() {
                                @Override
                                public void onResult(int code, Void result, Throwable exception) {
                                    if (code == ResponseCode.RES_SUCCESS) {// 修改成功
                                        UIUtils.showToast("修改成功");
                                        getUserInfoFromRemote();// 重新加载个人资料
                                    } else {// 修改失败
                                        UIUtils.showToast("修改失败，请重试");
                                    }
                                    hideWaitingDialog();
                                }
                            });
                        }
                    });

                }
            }
        }
        if (requestCode == CODE_AREA){
            if (data != null) {
                String province = data.getStringExtra("province");
                String city = data.getStringExtra("city");
                String address = province + "省" +city + "区";
                //TODO 更改服务器数据
                mAddress.setRightText(address);
            }
        }
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("个人信息");
        mToolbar.setNavigationIcon(R.mipmap.ic_back);
    }

    private void getUserInfoFromRemote() {
        List<String> accountList = new ArrayList<>();
        accountList.add(UserCache.getAccount());
        NimUserInfoSDK.getUserInfosFormServer(accountList, new RequestCallback<List<NimUserInfo>>() {
            @Override
            public void onSuccess(List<NimUserInfo> param) {
                initData();
            }

            @Override
            public void onFailed(int code) {
                UIUtils.showToast("获取用户信息失败" + code);
            }

            @Override
            public void onException(Throwable exception) {
                exception.printStackTrace();
            }
        });
    }

    private void updateGender(final GenderEnum gender) {
        updateGenderView(gender);
        showWaitingDialog("请稍等");
        Map<UserInfoFieldEnum, Object> fields = new HashMap(1);
        fields.put(UserInfoFieldEnum.GENDER, gender.getValue());
        NimUserInfoSDK.updateUserInfo(fields, new RequestCallbackWrapper<Void>() {
            @Override
            public void onResult(int code, Void result, Throwable exception) {
                hideWaitingDialog();
                if (code == ResponseCode.RES_SUCCESS) {
                    UIUtils.showToast("修改成功");
                    mDialog.dismiss();
                } else {
                    UIUtils.showToast("修改失败");
                }
            }
        });
    }

    private void updateGenderView(GenderEnum gender) {
        if (gender == GenderEnum.MALE) {
            mTvMale.setCompoundDrawables(null, null, mSelectedDrawable, null);
            mTvFemale.setCompoundDrawables(null, null, mUnSelectedDrawable, null);
        } else if (gender == GenderEnum.FEMALE) {
            mTvMale.setCompoundDrawables(null, null, mUnSelectedDrawable, null);
            mTvFemale.setCompoundDrawables(null, null, mSelectedDrawable, null);
        } else {
            mTvMale.setCompoundDrawables(null, null, mUnSelectedDrawable, null);
            mTvFemale.setCompoundDrawables(null, null, mUnSelectedDrawable, null);
        }
    }
}
