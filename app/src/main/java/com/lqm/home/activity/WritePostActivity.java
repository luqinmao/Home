package com.lqm.home.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lqm.home.R;
import com.lqm.home.adapter.ImagePickerAdapter;
import com.lqm.home.app.App;
import com.lqm.home.app.AppConst;
import com.lqm.home.imageloader.ImageLoaderManager;
import com.lqm.home.model.Post;
import com.lqm.home.model.UserCache;
import com.lqm.home.model.UserServer;
import com.lqm.home.nimsdk.NimUserInfoSDK;
import com.lqm.home.parse.JsonCallback;
import com.lqm.home.utils.L;
import com.lqm.home.utils.PrefUtils;
import com.lqm.home.utils.T;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.ui.ImagePreviewDelActivity;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.request.BaseRequest;
import com.nanchen.compresshelper.CompressHelper;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.io.File;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

import static com.lqm.home.R.id.tv_position;

/**
 * 发布帖子界面
 */
public class WritePostActivity extends BaseActivity{

    public static final int IMAGE_ITEM_ADD = -1;
    private static final int REQUEST_CODE_SELECT = 100;
    private static final int REQUEST_CODE_PREVIEW = 101;

    private ArrayList<ImageItem> selImageList; //当前选择的所有图片
    private int maxImgCount = 9;               //允许选择图片最大数


    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.tv_submit)
    TextView tvSubmit;
    @Bind(R.id.et_title)
    EditText etTitle;
    @Bind(R.id.et_content)
    EditText etContent;
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.btn_select_img)
    Button btnSelectImg;
    @Bind(R.id.btn_get_position)
    Button btnGetPosition;
    @Bind(R.id.pb_progress)
    ProgressBar pbProgress;
    @Bind(tv_position)
    TextView tvPosition;
    @Bind(R.id.iv_user_photo)
    ImageView ivUserPhoto;


    private ArrayList<ImageItem> images;
    private UserServer userInfo;
    private NimUserInfo mNimUserInfo;
    private ImagePickerAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_post);

        initView();
        ButterKnife.bind(this);
    }

    private void initView() {
        userInfo = PrefUtils.getUserServerInfo();
        mNimUserInfo = NimUserInfoSDK.getUser(UserCache.getAccount());
        if (!TextUtils.isEmpty(mNimUserInfo.getAvatar()) && ivUserPhoto != null) {
            ImageLoaderManager.LoadNetImage(mNimUserInfo.getAvatar(), ivUserPhoto);
        }

        selImageList = new ArrayList<>();
        adapter = new ImagePickerAdapter(this, selImageList, maxImgCount);
        adapter.setOnItemClickListener(new ImagePickerAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position == IMAGE_ITEM_ADD){
                    selectImg();
                }else{
                    //打开预览
                    Intent intentPreview = new Intent(WritePostActivity.this, ImagePreviewDelActivity.class);
                    intentPreview.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, (ArrayList<ImageItem>) adapter.getImages());
                    intentPreview.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, position);
                    intentPreview.putExtra(ImagePicker.EXTRA_FROM_ITEMS, true);
                    startActivityForResult(intentPreview, REQUEST_CODE_PREVIEW);
                }
            }
        });

        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

    }

    @OnClick({R.id.iv_back, R.id.tv_submit, R.id.btn_select_img, R.id.btn_get_position})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_submit:
                submitPost();
                break;
            case R.id.btn_select_img:
                selectImg();
                break;
            case R.id.btn_get_position:
                getPosition();
                break;

        }
    }

    private void selectImg() {
        App.imagePicker.setMultiMode(true);
        //打开选择,本次允许选择的数量
        App.imagePicker.setSelectLimit(maxImgCount - selImageList.size());
        Intent intent1 = new Intent(WritePostActivity.this, ImageGridActivity.class);
        startActivityForResult(intent1, REQUEST_CODE_SELECT);
    }

    //获取当前位置
    private void getPosition() {
        tvPosition.setVisibility(View.VISIBLE);
        tvPosition.setText("获取的位置大运中心");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
                //添加图片返回
                if (data != null && requestCode == REQUEST_CODE_SELECT) {
                    images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                    if (images != null) {
                        selImageList.addAll(images);
                        adapter.setImages(selImageList);
                    }
                }
            } else if (resultCode == ImagePicker.RESULT_CODE_BACK) {
                //预览图片返回
                if (data != null && requestCode == REQUEST_CODE_PREVIEW) {
                    images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_IMAGE_ITEMS);
                    if (images != null) {
                        selImageList.clear();
                        selImageList.addAll(images);
                        adapter.setImages(selImageList);
                    }
                }
            }
    }

    private void submitPost() {
        if (TextUtils.isEmpty(etContent.getText()) || TextUtils.isEmpty(etTitle.getText())){
            T.showShort(WritePostActivity.this,"请先填写内容");
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在发布帖子，请稍等...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        ArrayList<File> files = new ArrayList<>();
        if (images != null && images.size() > 0) {
            for (int i = 0; i < images.size(); i++) {
                File formerFile = new File(images.get(i).path);
                File compressionFile = CompressHelper.getDefault(this).compressToFile(formerFile);
                files.add(compressionFile);
            }
        }

        String title = etTitle.getText().toString().trim();
        String content = etContent.getText().toString().trim();
        OkGo.post(AppConst.Post.CREATE_POST)
                .tag(this)
                .addFileParams("img", files)       // 这种方式为同一个key，上传多个文件
                .params("useraccount", mNimUserInfo.getAccount())
                .params("username", mNimUserInfo.getName())
                .params("title", title)
                .params("userphoto",mNimUserInfo.getAvatar())
                .params("content", content)
                .params("contentImg", "")
                .params("currentPosition", tvPosition.getText().toString())
                .params("createTime", "没传")
                .params("villageId", userInfo.getHomeid())
                .execute(new JsonCallback<Post>() {
                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        L.e("正在上传中...");
                    }

                    @Override
                    public void upProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                        System.out.println("upProgress -- " + totalSize + "  " + currentSize + "  " + progress + "  " + networkSpeed);

                        pbProgress.setMax(100);
                        pbProgress.setProgress((int) (progress * 100));
                    }

                    @Override
                    public void onSuccess(Post post, Call call, Response response) {
                        Intent intent = new Intent();
                        setResult(10, intent);
                        T.showShort(WritePostActivity.this, "发表成功");
                        progressDialog.hide();
                        finish();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        T.showShort(WritePostActivity.this, "发布失败" + e);
                        progressDialog.hide();
                    }

                });

    }

}
