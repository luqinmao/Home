package com.lqm.home.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.lqm.home.R;
import com.lqm.home.app.App;
import com.lqm.home.app.AppConst;
import com.lqm.home.model.ResultData;
import com.lqm.home.model.UpdateApkModel;
import com.lqm.home.model.UserCache;
import com.lqm.home.model.UserServer;
import com.lqm.home.nimsdk.NimAccountSDK;
import com.lqm.home.parse.JsonCallback;
import com.lqm.home.utils.VersionManageUtil;
import com.lqm.home.utils.DataCleanManager;
import com.lqm.home.utils.L;
import com.lqm.home.utils.PrefUtils;
import com.lqm.home.utils.T;
import com.lqm.home.widget.CustomDialog;
import com.lqm.home.widget.Topbar;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.drakeet.materialdialog.MaterialDialog;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 设置
 */
public class SettingActivity extends BaseActivity {

    @Bind(R.id.topbar)
    Topbar topbar;
    @Bind(R.id.tv_push)
    TextView tvPush;
    @Bind(R.id.tv_clear)
    TextView tvClear;
    @Bind(R.id.tv_version)
    TextView tvVersion;
    @Bind(R.id.tv_version_code)
    TextView tvVersionCode;
    @Bind(R.id.tv_cache_data)
    TextView tvCacheData;
    @Bind(R.id.tv_exit)
    TextView tvExit;
    @Bind(R.id.tv_temp1)
    TextView tvTemp1;

    private View mExitDialogView;
    private CustomDialog mDialog;
    private Intent intent;
    private UpdateApkModel mUpdateApkModel;
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 100;
    private ProgressDialog m_progressDlg;
    private Drawable close;
    private Drawable open;
    private UserServer mUser = PrefUtils.getUserServerInfo();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        tvVersionCode.setText("v"+VersionManageUtil.getVerName(getApplicationContext()));
        tvCacheData.setText(DataCleanManager.getTotalCacheSize(SettingActivity.this));

        topbar.setTopbarOnClickListener(new Topbar.TopbarOnClickListener() {
            @Override
            public void leftOnClick() {
                finish();
            }

            @Override
            public void rightOnClick() {
            }
        });

        close = getResources().getDrawable(R.mipmap.close_sel);
        open = getResources().getDrawable(R.mipmap.open_sel);
        close.setBounds(0, 0, close.getMinimumWidth(), close.getMinimumHeight());
        open.setBounds(0, 0, open.getMinimumWidth(), open.getMinimumHeight());
        if (PrefUtils.getBoolean(SettingActivity.this, "isOpenNotify", true)) {
            tvPush.setCompoundDrawables(null, null, open, null);
        } else {
            tvPush.setCompoundDrawables(null, null, close, null);
        }
    }

    @OnClick({R.id.tv_push, R.id.tv_clear, R.id.tv_version, R.id.tv_exit, R.id.tv_temp1})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_push:
                if (!PrefUtils.getBoolean(SettingActivity.this, "isOpenNotify", true)) {
                    PrefUtils.setBoolean(SettingActivity.this, "isOpenNotify", true);
                    tvPush.setCompoundDrawables(null, null, open, null);
                    XGPushConfig.enableDebug(this, true);
                    XGPushManager.registerPush(this, mUser != null ? mUser.getPhone() : "123456789");
                } else {
                    PrefUtils.setBoolean(SettingActivity.this, "isOpenNotify", false);
                    tvPush.setCompoundDrawables(null, null, close, null);
                    XGPushManager.unregisterPush(this);
                }

                break;
            case R.id.tv_clear:
                final MaterialDialog dialog = new MaterialDialog(SettingActivity.this);
                dialog.setTitle("清除缓存？");
                dialog.setMessage("");
                dialog.setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DataCleanManager.getTotalCacheSize(SettingActivity.this);
                        DataCleanManager.clearAllCache(SettingActivity.this);
                        tvCacheData.setText(DataCleanManager.getTotalCacheSize(SettingActivity.this));
                        T.showShort(SettingActivity.this, "清理完成");
                        dialog.dismiss();
                    }
                });
                dialog.setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                break;
            case R.id.tv_version:
                updateApp();
                break;
            case R.id.tv_exit:
                if (mExitDialogView == null) {
                    mExitDialogView = View.inflate(this, R.layout.dialog_exit, null);
                    mDialog = new CustomDialog(this, mExitDialogView, R.style.dialog);
                    mDialog.show();
                    mExitDialogView.findViewById(R.id.tvExitAccount).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //退出当前账号
                            NimAccountSDK.logout();
                            NimAccountSDK.removeUserInfo();
                            UserCache.clear();
                            intent = new Intent(SettingActivity.this, LoginOrRegistActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                            mDialog.dismiss();
                        }
                    });

                    mExitDialogView.findViewById(R.id.tvExitApp).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //退出app
                            App.exit();
                            mDialog.dismiss();
                        }
                    });

                } else {
                    mDialog.show();
                }

                break;
            case R.id.tv_temp1:
                PrefUtils.clearData(SettingActivity.this, "is_user_guide_showed");
                T.showShort(this, "清除成功");
                break;
        }
    }

    // ===================   版本升级 start ==========//

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                downFile(mUpdateApkModel.getUrl());  //开始下载
            } else {
                T.showShort(SettingActivity.this, "您拒绝了访问文件权限，升级失败");
            }
        }
    }

    private void updateApp() {
        if (mUpdateApkModel == null) {
            mUpdateApkModel = new UpdateApkModel();
        }
        m_progressDlg = new ProgressDialog(SettingActivity.this);
        m_progressDlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        GetPrepayIdTask();
    }

    /**
     * 异步任务,获取数据
     */
    public void GetPrepayIdTask() {
        final ProgressDialog progress = new ProgressDialog(SettingActivity.this);
        progress.setMessage("更新...");
        OkGo.get(AppConst.UPDATE_ANDROID)
                .execute(new JsonCallback<ResultData<UpdateApkModel>>() {
                    @Override
                    public void onSuccess(ResultData<UpdateApkModel> resultData, Call call, Response response) {
                        progress.hide();
                        mUpdateApkModel = resultData.getData();
                        if (mUpdateApkModel.getVersioncode() > VersionManageUtil.getVerCode(getApplicationContext())) {
                            doNewVersionUpdate();
                        } else {
                            notNewVersionDlgShow();
                        }
                    }
                });
    }

    /**
     * 提示更新新版本
     */
    private void doNewVersionUpdate() {
        String verName = VersionManageUtil.getVerName(getApplicationContext());
        String str = "当前版本：" + verName + "\n发现新版本：" + mUpdateApkModel.getVersionname() +
                "\n" + mUpdateApkModel.getUpdateinfo();
        final MaterialDialog dialog = new MaterialDialog(SettingActivity.this);
        dialog.setTitle("软件更新");
        dialog.setMessage(str);
        dialog.setPositiveButton("更新", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                m_progressDlg.setTitle("正在下载");
                m_progressDlg.setMessage("请稍候...");
                if (ContextCompat.checkSelfPermission(SettingActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    //申请WRITE_EXTERNAL_STORAGE权限
                    ActivityCompat.requestPermissions(SettingActivity.this, new String[]{Manifest.permission
                            .WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
                } else {
                    downFile(mUpdateApkModel.getUrl());  //开始下载
                }
            }
        });
        dialog.setNegativeButton("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     * 没有新版本
     */
    private void notNewVersionDlgShow() {
        int verCode = VersionManageUtil.getVerCode(this);
        String verName = VersionManageUtil.getVerName(this);
        String str = "当前版本： " + verName + ",\n已是最新版,无需更新!";
        final MaterialDialog dialog = new MaterialDialog(SettingActivity.this);
        dialog.setTitle("软件更新");
        dialog.setMessage(str);
        dialog.setPositiveButton("确定", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    private void downFile(String url) {
        m_progressDlg.show();
        OkGo.get(url)
            .tag(this)
            .execute(new FileCallback(Environment.getExternalStorageDirectory().getPath(), "home.apk") {
                //文件下载时，可以指定下载的文件目录和文件名
                @Override
                public void onSuccess(File file, Call call, Response response) {
                    // file 即为文件数据，文件保存在指定目录
                    m_progressDlg.cancel();
                    update();
                }

                @Override
                public void downloadProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                    //这里回调下载进度(该回调在主线程,可以直接更新ui)
                    m_progressDlg.setMax((int) totalSize);//设置进度条的最大值
                    m_progressDlg.setProgress((int) currentSize);
                    L.e("totalSize:"+totalSize+"currentSize"+currentSize);
                }
            });
    }

    /**
     * 安装程序
     */
    private void update() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory().getPath(), "home.apk")),
                "application/vnd.android.package-archive");
        startActivity(intent);
    }

    // ===================   版本升级 end ==========//
}
