package com.lqm.home.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lqm.home.R;
import com.lqm.home.adapter.ImagePickerAdapter;
import com.lqm.home.adapter.MyNineGridViewClickAdapter;
import com.lqm.home.app.App;
import com.lqm.home.app.AppConst;
import com.lqm.home.imageloader.GlideImageLoader;
import com.lqm.home.imageloader.ImageLoaderManager;
import com.lqm.home.model.Comment;
import com.lqm.home.model.Post;
import com.lqm.home.model.ResponseData;
import com.lqm.home.model.UserCache;
import com.lqm.home.model.UserServer;
import com.lqm.home.nimsdk.NimUserInfoSDK;
import com.lqm.home.parse.DialogCallback;
import com.lqm.home.parse.JsonCallback;
import com.lqm.home.utils.PrefUtils;
import com.lqm.home.utils.T;
import com.lqm.home.utilslqr.KeyBoardUtils;
import com.lqm.home.widget.Topbar;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.ui.ImagePreviewDelActivity;
import com.lzy.ninegrid.ImageInfo;
import com.lzy.ninegrid.NineGridView;
import com.lzy.ninegrid.preview.NineGridViewClickAdapter;
import com.lzy.okgo.OkGo;
import com.nanchen.compresshelper.CompressHelper;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 帖子详情页
 */

public class PostDetailActivity extends BaseActivity
        implements BaseQuickAdapter.RequestLoadMoreListener {


    @Bind(R.id.topbar)
    Topbar mTopbar;
    @Bind(R.id.rv_post_detail)
    RecyclerView rvPostDetail;
    @Bind(R.id.et_comment)
    EditText etComment;
    @Bind(R.id.btn_comment)
    Button btnComment;
    @Bind(R.id.ib_add_img)
    ImageButton ibAddImg;
    @Bind(R.id.ll_comment_img)
    LinearLayout llCommentImg;
    @Bind(R.id.rv_add_img)
    RecyclerView rvAddImg;

    private View headView;
    private Integer postid;
    private List<Comment> mdatas;
    private ImageView ivPhoto;
    private TextView tvTime;
    private TextView tvTitle;
    private TextView tvDesc;
    private NineGridView ivPostImg;
    private TextView tvName;
    private RvCommentAdapter rvCommentAdapter;
    private int currenPage;

    private UserServer userInfo;
    private int mCommentFloor;
    private NimUserInfo mNimUserInfo;

    private ArrayList<ImageItem> selImageList;  //当前选择的所有图片
    private int maxImgCount = 9;               //允许选择图片最大数
    private ImagePickerAdapter commentImgAdapter;
    public static final int IMAGE_ITEM_ADD = -1;
    private static final int REQUEST_CODE_SELECT = 100;
    private static final int REQUEST_CODE_PREVIEW = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        ButterKnife.bind(this);

        postid = Integer.parseInt(getIntent().getStringExtra("postid"));
        initView();
        initData();
    }

    private void initView() {
        headView = View.inflate(getApplicationContext(), R.layout.activity_post_detail_head, null);
        ivPhoto = (ImageView) headView.findViewById(R.id.iv_photo);
        tvName = (TextView) headView.findViewById(R.id.tv_name);
        tvTime = (TextView) headView.findViewById(R.id.tv_time);
        tvTitle = (TextView) headView.findViewById(R.id.tv_title);
        tvDesc = (TextView) headView.findViewById(R.id.tv_desc);
        ivPostImg = (NineGridView) headView.findViewById(R.id.iv_post_img);

        mTopbar.setTopbarOnClickListener(new Topbar.TopbarOnClickListener() {
            @Override
            public void leftOnClick() {
                finish();
            }

            @Override
            public void rightOnClick() {
            }
        });

    }

    private void initData() {
        mdatas = new ArrayList();

        rvCommentAdapter = new RvCommentAdapter(R.layout.item_comment, mdatas);
        rvCommentAdapter.addHeaderView(headView);
        rvPostDetail.setLayoutManager(new LinearLayoutManager(PostDetailActivity.this));
        rvPostDetail.setAdapter(rvCommentAdapter);
        rvCommentAdapter.setOnLoadMoreListener(this);

        userInfo = PrefUtils.getUserServerInfo();
        mNimUserInfo = NimUserInfoSDK.getUser(UserCache.getAccount());
        NineGridView.setImageLoader(new GlideImageLoader());

        getPostInfo();  //帖子头部信息
        getCommentInfo();

        //评论图片
        selImageList = new ArrayList<>();
        commentImgAdapter = new ImagePickerAdapter(this, selImageList, maxImgCount);
        commentImgAdapter.setOnItemClickListener(new ImagePickerAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position == IMAGE_ITEM_ADD){
                    selectImg();
                }else{
                    //打开预览
                    Intent intentPreview = new Intent(PostDetailActivity.this, ImagePreviewDelActivity.class);
                    intentPreview.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, (ArrayList<ImageItem>) commentImgAdapter.getImages());
                    intentPreview.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, position);
                    intentPreview.putExtra(ImagePicker.EXTRA_FROM_ITEMS, true);
                    startActivityForResult(intentPreview, REQUEST_CODE_PREVIEW);
                }
            }
        });

        rvAddImg.setLayoutManager(new GridLayoutManager(this, 4));
        rvAddImg.setHasFixedSize(true);
        rvAddImg.setAdapter(commentImgAdapter);
    }

    private void getPostInfo() {
        OkGo.get(AppConst.Post.GET_POST_INFO)
                .params("postid", postid)
                .execute(new JsonCallback<Post>() {
                    @Override
                    public void onSuccess(Post post, Call call, Response response) {
                        if (post.getCode() == 200) {
                            Post.DataBean postData = post.getData();
                            setHeadPostInfo(postData);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        T.showShort(PostDetailActivity.this, "获取头部信息错误");
                    }
                });
    }

    private void setHeadPostInfo(final Post.DataBean postData) {
        tvName.setText(postData.getUsername());
        tvTime.setText(postData.getCreateTime());
        tvTitle.setText(postData.getTitle());
        tvDesc.setText(postData.getContent());

        ImageLoaderManager.LoadNetImage(postData.getUserphoto(), ivPhoto);
        ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PostDetailActivity.this, UserInfoActivity.class);
                intent.putExtra("account", postData.getUseraccount());
                startActivity(intent);
            }
        });

        if (!TextUtils.isEmpty(postData.getContentImg())) {  //有图片时
            ArrayList<ImageInfo> imageInfos = new ArrayList<>();
            List<String> images = new ArrayList();
            try {
                JSONArray jSONArray = new JSONArray(postData.getContentImg().toString());
                for (int i = 0; i < jSONArray.length(); i++) {
                    JSONObject temp = new JSONObject(jSONArray.getString(i));
                    images.add(AppConst.SERVER_ADDRESS_IMG + temp.getString("path"));
                }

                for (String image : images) {
                    ImageInfo info = new ImageInfo();
                    info.setThumbnailUrl(image);
                    info.setBigImageUrl(image);
                    imageInfos.add(info);
                }
                ivPostImg.setAdapter(new MyNineGridViewClickAdapter(PostDetailActivity.this, imageInfos));

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }


    @OnClick({R.id.btn_comment, R.id.ib_add_img})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_comment:
                submitComment();
                break;
            case R.id.ib_add_img:
                if (llCommentImg.getVisibility() == View.GONE){
                    llCommentImg.setVisibility(View.VISIBLE);
                }else{
                    llCommentImg.setVisibility(View.GONE);
                }
                break;
        }
    }

    private void selectImg() {
        App.imagePicker.setMultiMode(true);
        //打开选择,本次允许选择的数量
        App.imagePicker.setSelectLimit(maxImgCount - selImageList.size());
        Intent intent1 = new Intent(PostDetailActivity.this, ImageGridActivity.class);
        startActivityForResult(intent1, REQUEST_CODE_SELECT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            //添加图片返回
            if (data != null && requestCode == REQUEST_CODE_SELECT) {
                 ArrayList<ImageItem>  images = (ArrayList<ImageItem>) data.getSerializableExtra
                        (ImagePicker.EXTRA_RESULT_ITEMS);
                if (images != null) {
                    selImageList.addAll(images);
                    commentImgAdapter.setImages(selImageList);
                }
            }
        } else if (resultCode == ImagePicker.RESULT_CODE_BACK) {
            //预览图片返回
            if (data != null && requestCode == REQUEST_CODE_PREVIEW) {
                 ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra
                        (ImagePicker.EXTRA_IMAGE_ITEMS);
                if (images != null) {
                    selImageList.clear();
                    selImageList.addAll(images);
                    commentImgAdapter.setImages(selImageList);
                }
            }
        }
    }


    private void submitComment() {
        String content = etComment.getText().toString().trim();
        if (TextUtils.isEmpty(content)){
            T.showShort(PostDetailActivity.this,"请评论内容不能为空");
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在评论中...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        final ArrayList<File> files = new ArrayList<>();
        if (selImageList != null && selImageList.size() > 0) {
            for (int i = 0; i < selImageList.size(); i++) {
                File formerFile = new File(selImageList.get(i).path);
                File compressionFile = CompressHelper.getDefault(this).compressToFile(formerFile);
                files.add(compressionFile);
            }
        }

        OkGo.post(AppConst.Comment.CREATE_COMMENT)
                .tag(this)
                .addFileParams("img", files)
                .params("userid", userInfo.getId())
                .params("useraccount", mNimUserInfo.getAccount())
                .params("username", mNimUserInfo.getName())
                .params("userphoto",mNimUserInfo.getAvatar())
                .params("content", content)
                .params("postid", postid)
                .params("floor", mCommentFloor+1)
                .execute(new DialogCallback<ResponseData<Comment>>(this) {
                    @Override
                    public void onSuccess(ResponseData<Comment> comment, Call call, Response response) {
                        T.showShort(PostDetailActivity.this, "评论成功");
                        KeyBoardUtils.closeKeybord(etComment,PostDetailActivity.this);
                        etComment.setText("");
                        selImageList.clear();
//                        selImageList.addAll(null);
                        commentImgAdapter.setImages(selImageList);
                        llCommentImg.setVisibility(View.GONE);

                        mdatas.add(comment.getData());
                        rvCommentAdapter.notifyDataSetChanged();
                        rvPostDetail.scrollToPosition(rvCommentAdapter.getItemCount() - 1);  //滑动最后
                        progressDialog.hide();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        T.showShort(PostDetailActivity.this, "评论失败" + e);
                        progressDialog.hide();
                    }

                });

    }


    private void getCommentInfo() {
        currenPage = 1;
        OkGo.get(AppConst.Comment.GET_COMMENTS)
                .params("page", currenPage)
                .params("postid", postid)
                .params("num", 10)
                .execute(new JsonCallback<ResponseData<List<Comment>>>() {
                    @Override
                    public void onSuccess(ResponseData<List<Comment>> responseData, Call call, Response response) {

                        if(responseData.getData() != null){
                            mCommentFloor = responseData.getData().size();
                            mdatas.clear();
                            mdatas.addAll(responseData.getData());
                            rvCommentAdapter.setNewData(mdatas);
                        }

                        T.showShort(PostDetailActivity.this, "刷新成功");
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        T.showShort(PostDetailActivity.this, "onError" + e);
                    }
                });
    }

    @Override
    public void onLoadMoreRequested() {
        currenPage = currenPage + 1;
        T.showShort(PostDetailActivity.this, "加载评论");
        OkGo.get(AppConst.Comment.GET_COMMENTS)
                .params("page", currenPage)
                .params("postid", postid)
                .params("num", 10)
                .execute(new JsonCallback<ResponseData<List<Comment>>>() {
                    @Override
                    public void onSuccess(ResponseData<List<Comment>> returnData, Call call, Response response) {
                        if (returnData.data != null) {
                            rvCommentAdapter.addData(returnData.getData());
                            //显示没有更多数据
                            if (returnData.getData().size() == 0) {
                                rvCommentAdapter.loadComplete();         //加载完成
                                View noDataView = View.inflate(PostDetailActivity.this, R.layout.item_no_data, null);
                                rvCommentAdapter.addFooterView(noDataView);
                            }
                        }

                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        //显示数据加载失败,点击重试
                        rvCommentAdapter.showLoadMoreFailedView();
                        T.showShort(PostDetailActivity.this, "error" + e);
                    }

                });
    }

    private class RvCommentAdapter extends BaseQuickAdapter<Comment> {

        public RvCommentAdapter(int layoutResId, List<Comment> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder baseViewHolder, final Comment data) {
            if (data.getUserphoto() != null) {
                ImageLoaderManager.LoadNetImage(data.getUserphoto().toString(),
                        (ImageView) baseViewHolder.getView(R.id.iv_user_photo));
            }
            baseViewHolder.setText(R.id.tv_user_name, data.getUsername())
                    .setText(R.id.tv_time, data.getCreateTime())
                    .setText(R.id.tv_comment_desc, data.getContent())
                    .setText(R.id.tv_floor, data.getFloor() + "楼");


            NineGridView nineGrid = baseViewHolder.getView(R.id.nineGrid);
            ArrayList<ImageInfo> imageInfos = new ArrayList<>();
            List<String> images = new ArrayList();

            if (!TextUtils.isEmpty(data.getContentImg())) {  //有图片时
                try {
                    JSONArray jSONArray = new JSONArray(data.getContentImg().toString());
                    for (int i = 0; i < jSONArray.length(); i++) {
                        JSONObject temp = new JSONObject(jSONArray.getString(i));
                        images.add(AppConst.SERVER_ADDRESS_IMG + temp.getString("path"));
                    }
                    for (String image : images) {
                        ImageInfo info = new ImageInfo();
                        info.setThumbnailUrl(image);
                        info.setBigImageUrl(image);
                        imageInfos.add(info);
                    }
                    nineGrid.setAdapter(new NineGridViewClickAdapter(mContext, imageInfos));

//            if (images != null && images.size() == 1) {
//                nineGrid.setSingleImageRatio(images.get(0).width * 1.0f / images.get(0).height);
//            }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                nineGrid.setVisibility(View.GONE);
            }

            baseViewHolder.getView(R.id.iv_user_photo).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(PostDetailActivity.this, UserInfoActivity.class);
                    intent.putExtra("account", data.getUseraccount());
                    startActivity(intent);
                }
            });
        }
    }

}