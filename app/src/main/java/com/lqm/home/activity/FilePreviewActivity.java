package com.lqm.home.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lqm.home.R;
import com.lqm.home.utilslqr.FileIconUtils;
import com.lqm.home.utilslqr.FileOpenUtils;
import com.lqm.home.utilslqr.FileUtils;
import com.lqm.home.utilslqr.MimeTypeUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.request.BaseRequest;
import com.netease.nimlib.sdk.msg.attachment.FileAttachment;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;

/**
 * @创建者 CSDN_LQR
 * @描述 文件预览
 */
public class FilePreviewActivity extends BaseActivity {

    private Intent mIntent;
    private IMMessage mMessage;
    private FileAttachment mFa;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.ivPic)
    ImageView mIvPic;
    @Bind(R.id.tvName)
    TextView mTvName;
    @Bind(R.id.pbFile)
    ProgressBar mPbFile;
    @Bind(R.id.btnOpen)
    Button mBtnOpen;//其他应用打开 下载

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();
        initView();
        initData();
        initListener();
    }

    public void init() {
        mIntent = getIntent();
        mMessage = (IMMessage) mIntent.getSerializableExtra("message");
        if (mMessage == null) {
//            interrupt();
            return;
        }
        mFa = (FileAttachment) mMessage.getAttachment();
    }

    public void initView() {
        setContentView(R.layout.activity_file_preview);
        ButterKnife.bind(this);
//        initToolbar();

        setFileInfo();
    }

    public void initData() {
        //判断文件是否已经下载到本地
//        if (TextUtils.isEmpty(mFa.getPath())) {
//            downloadFile();
//        }
    }

    public void initListener() {
        mBtnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBtnOpen.getText().equals("下载")) {
                    downloadFile();
                } else {
                    //打开文件
                    FileOpenUtils.openFile(FilePreviewActivity.this, mFa.getPath(), MimeTypeUtils.getMimeType(mFa.getDisplayName()));
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

//    private void initToolbar() {
//        setSupportActionBar(mToolbar);
//        getSupportActionBar().setTitle("文件预览");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        mToolbar.setNavigationIcon(R.mipmap.ic_back);
//    }

    private void setFileInfo() {
        mIvPic.setImageResource(FileIconUtils.getFileIconResId(mFa.getExtension()));
        mTvName.setText(mFa.getDisplayName());
        mPbFile.setVisibility(View.GONE);
        mBtnOpen.setVisibility(View.VISIBLE);
        if (TextUtils.isEmpty(mFa.getPath())) {
            mBtnOpen.setText("下载");
        } else {
            mBtnOpen.setText("其他应用打开");
        }
    }

    //下载文件
    private void downloadFile() {
        OkGo.get(mFa.getUrl()).execute(new FileCallback(FileUtils.getDirFromPath(mFa.getPathForSave()),
                FileUtils.getFileNameFromPath(mFa.getPathForSave())) {
            @Override
            public void onSuccess(File file, Call call, Response response) {
                setFileInfo();
            }

            @Override
            public void onBefore(BaseRequest request) {
                mPbFile.setVisibility(View.VISIBLE);
                mBtnOpen.setVisibility(View.GONE);
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                mIvPic.setImageResource(R.mipmap.ic_launcher);
                mTvName.setText("文件已过期或已被清理");
                mPbFile.setVisibility(View.GONE);
                mBtnOpen.setVisibility(View.GONE);
            }

            @Override
            public void downloadProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                mPbFile.setMax((int) totalSize);
                mPbFile.setProgress((int) (progress * 100));
            }
        });
    }
}
