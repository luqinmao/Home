package com.lqm.home.activity;

import android.os.Bundle;
import android.os.Environment;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bm.library.PhotoView;
import com.lqm.home.R;
import com.lqm.home.factory.PopupWindowFactory;
import com.lqm.home.imageloader.ImageLoaderManager;
import com.lqm.home.utilslqr.UIUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;

/**
 * @创建者 CSDN_LQR
 * @描述 查看头像
 */
public class ShowBigImageActivity extends BaseActivity {

    private String mUrl;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.pv)
    PhotoView mPv;
    @Bind(R.id.pb)
    ProgressBar mPb;
    private FrameLayout mView;
    private PopupWindow mPopupWindow;
    private String mFrom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        initView();
    }

    public void init() {
        mUrl = getIntent().getStringExtra("url");
        mFrom = getIntent().getStringExtra("from");
    }

    public void initView() {
        setContentView(R.layout.activity_show_big_image);
        ButterKnife.bind(this);
        initToolbar();
        mPv.enable();// 启用图片缩放功能

        ImageLoaderManager.LoadNetImage(mUrl, mPv);
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
        if (!TextUtils.isEmpty(mFrom)&&mFrom.equals("baidu")){
            getSupportActionBar().setTitle("");
        }else{
            getSupportActionBar().setTitle("头像");
        }
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
            tv.setText("保存到手机");
            mView.addView(tv);

            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPopupWindow.dismiss();
                    //下载图片
                    final String dirPath = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), getPackageName()).getAbsolutePath();
                    final String fileName = System.currentTimeMillis()+".jpg";
                    OkGo.get(mUrl).execute(new FileCallback(dirPath, fileName) {
                        @Override
                        public void onSuccess(File file, Call call, Response response) {
                            UIUtils.showToast("图片保存在" + dirPath + "/" + fileName);
                        }

                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            UIUtils.showToast("头像保存失败");
                        }
                    });
                }
            });
        }
        mPopupWindow = PopupWindowFactory.getPopupWindowAtLocation(mView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, getWindow().getDecorView().getRootView(), Gravity.BOTTOM, 0, 0);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                PopupWindowFactory.makeWindowLight(ShowBigImageActivity.this);
            }
        });
        PopupWindowFactory.makeWindowDark(this);
    }
}
