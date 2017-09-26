package com.lqm.home.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lqm.home.R;
import com.lqm.home.app.AppConst;
import com.lqm.home.factory.PopupWindowFactory;
import com.lqm.home.factory.ThreadPoolFactory;
import com.lqm.home.model.UserCache;
import com.lqm.home.nimsdk.NimFriendSDK;
import com.lqm.home.nimsdk.NimTeamSDK;
import com.lqm.home.utilslqr.LogUtils;
import com.lqm.home.utilslqr.UIUtils;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.team.model.Team;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.QRCodeDecoder;
import cn.bingoogolapple.qrcode.zxing.ZXingView;

import static com.lqm.home.activity.SessionActivity.IMAGE_PICKER;


/**
 * @创建者 CSDN_LQR
 * @描述 扫一扫
 */
public class ScanActivity extends BaseActivity implements QRCodeView.Delegate {

    private FrameLayout mView;
    private PopupWindow mPopupWindow;

    @Bind(R.id.zxingview)
    ZXingView mZxingview;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initListener();
    }

    public void initView() {
        setContentView(R.layout.activity_scan);
        ButterKnife.bind(this);
        initToolbar();
    }

    public void initListener() {
        mZxingview.setDelegate(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mZxingview.startCamera();
        mZxingview.startSpotAndShowRect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mZxingview.stopCamera();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mZxingview.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.menu_more, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.itemMore:
                showPopupMenu();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("二维码/条码");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationIcon(R.mipmap.ic_back);
    }

    private void showPopupMenu() {
        if (mView == null) {
            mView = new FrameLayout(this);
            mView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mView.setBackgroundColor(UIUtils.getColor(R.color.white));

            TextView tv = new TextView(this);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, UIUtils.dip2Px(45));
            tv.setLayoutParams(params);
            tv.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            tv.setPadding(UIUtils.dip2Px(20), 0, 0, 0);
            tv.setTextColor(UIUtils.getColor(R.color.gray0));
            tv.setTextSize(14);
            tv.setText("从相册选取二维码");
            mView.addView(tv);

            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPopupWindow.dismiss();
                    Intent intent = new Intent(ScanActivity.this, ImageGridActivity.class);
                    startActivityForResult(intent, IMAGE_PICKER);
                }
            });
        }
        mPopupWindow = PopupWindowFactory.getPopupWindowAtLocation(mView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, getWindow().getDecorView().getRootView(), Gravity.BOTTOM, 0, 0);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                PopupWindowFactory.makeWindowLight(ScanActivity.this);
            }
        });
        PopupWindowFactory.makeWindowDark(this);
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        LogUtils.sf(result);
        handleResult(result);
    }

    private void handleResult(String result) {
        vibrate();
        mZxingview.startSpot();
        //添加用户
        if (result.startsWith(AppConst.QRCodeCommend.ACCOUNT)) {
            String account = result.substring(AppConst.QRCodeCommend.ACCOUNT.length());
//            UIUtils.showToast("微信号：" + account);
            if (NimFriendSDK.isMyFriend(account)) {
                UIUtils.showToast("该用户已经是您的好友");
                return;
            }
            Intent intent = new Intent(ScanActivity.this, PostscriptActivity.class);
            intent.putExtra("account", account);
            startActivity(intent);
        }
        // 进群
        else if (result.startsWith(AppConst.QRCodeCommend.TEAMID)) {
            final String teamId = result.substring(AppConst.QRCodeCommend.TEAMID.length());
            NimTeamSDK.searchTeam(teamId, new RequestCallback<Team>() {
                @Override
                public void onSuccess(Team team) {
                    if (team.isMyTeam()) {
                        UIUtils.showToast("您已经在群聊中");
                    } else {
                        List<String> accounts = new ArrayList<String>(1);
                        accounts.add(UserCache.getAccount());
                        NimTeamSDK.addMembers(teamId, accounts, new RequestCallback<Void>() {
                            @Override
                            public void onSuccess(Void param) {
                                //跳转到群聊
                                Intent intent = new Intent(ScanActivity.this, SessionActivity.class);
                                intent.putExtra(SessionActivity.SESSION_ACCOUNT, teamId);
                                intent.putExtra(SessionActivity.SESSION_TYPE, SessionTypeEnum.Team);
                                startActivity(intent);
                                finish();
                            }

                            @Override
                            public void onFailed(int code) {
                                UIUtils.showToast("加群失败" + code);
                            }

                            @Override
                            public void onException(Throwable exception) {
                                UIUtils.showToast("加群失败");
                                exception.printStackTrace();
                            }
                        });
                    }
                }

                @Override
                public void onFailed(int code) {
                    UIUtils.showToast("查不到群" + code);
                }

                @Override
                public void onException(Throwable exception) {
                    UIUtils.showToast("查不到群");
                    exception.printStackTrace();
                }
            });
        }
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        UIUtils.showToast("打开相机出错");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {//返回多张照片
            if (data != null) {
                //是否发送原图
//                boolean isOrig = data.getBooleanExtra(ImagePreviewActivity.ISORIGIN, false);
                final ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (images != null && images.size() > 0) {
                    //取第一张照片
                    ThreadPoolFactory.getNormalPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            String result = QRCodeDecoder.syncDecodeQRCode(images.get(0).path);
                            if (TextUtils.isEmpty(result)) {
                                UIUtils.showToast("扫描失败");
                            } else {
                                handleResult(result);
                            }
                        }
                    });
                }
            }
        }
    }
}
