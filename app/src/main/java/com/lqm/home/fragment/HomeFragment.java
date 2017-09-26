package com.lqm.home.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lqm.home.R;
import com.lqm.home.activity.LoginOrRegistActivity;
import com.lqm.home.activity.MainActivity;
import com.lqm.home.activity.PostDetailActivity;
import com.lqm.home.activity.WritePostActivity;
import com.lqm.home.app.AppConst;
import com.lqm.home.model.PostVO;
import com.lqm.home.model.UserServer;
import com.lqm.home.model.VillageVO;
import com.lqm.home.parse.JsonCallback;
import com.lqm.home.utils.L;
import com.lqm.home.utils.PrefUtils;
import com.lqm.home.utils.T;
import com.lqm.home.widget.Topbar;
import com.lzy.ninegrid.ImageInfo;
import com.lzy.ninegrid.NineGridView;
import com.lzy.ninegrid.preview.NineGridViewClickAdapter;
import com.lzy.okgo.OkGo;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;


/**
 * Created by luqinmao on 2016/11/9.
 * 乡吧主页页面
 */
public class HomeFragment extends BaseFragment implements
        SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener, View.OnClickListener {

    private Context mContext;
    private Topbar tb_home;
    private RecyclerView rv_home;
    private View headerView;
    private SwipeRefreshLayout refresh_layout;

    private List<PostVO.DataBean> mdatas;
    private HomeListAdapter homeListAdapter;

    private ImageView iv_head_img;
    private TextView tv_head_title;
    private TextView tv_head_tiezi;
    private TextView tv_head_guanzhu;
    private Button btn_guanzhu;
    private Button btn_qiangdao;
    private int currenPage;
    private UserServer userInfo;
    private TextView tvVillageDesc;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onAttach(Context context) {
        this.mContext = context;
        userInfo = PrefUtils.getUserServerInfo();
        if (userInfo == null){
            startActivity(new Intent(getActivity(), LoginOrRegistActivity.class));
            getActivity().finish();
        }
        super.onAttach(context);
    }


    @Override
    public void init() {
        mdatas = new ArrayList();
    }

    @Override
    public View initView() {
        View view = View.inflate(getActivity(),R.layout.frag_home, null);
        headerView = LinearLayout.inflate(mContext, R.layout.layout_home_head, null);
        findView(view);
        initHeadView(headerView);
        return view;
    }

    private void initHeadView(View headerView) {
        iv_head_img = (ImageView) headerView.findViewById(R.id.iv_head_img);
        tv_head_title = (TextView) headerView.findViewById(R.id.tv_head_title);
        tv_head_guanzhu = (TextView) headerView.findViewById(R.id.tv_head_guanzhu);
        tv_head_tiezi = (TextView) headerView.findViewById(R.id.tv_head_tiezi);
        btn_guanzhu = (Button) headerView.findViewById(R.id.btn_guanzhu);
        btn_qiangdao = (Button) headerView.findViewById(R.id.btn_qiangdao);
        tvVillageDesc = (TextView)headerView.findViewById(R.id.tv_village_desc);

    }

    private void findView(View view) {
        tb_home = (Topbar) view.findViewById(R.id.topbar_home);
        rv_home = (RecyclerView) view.findViewById(R.id.rv_home);
        refresh_layout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
        tb_home.setTopbarOnClickListener(new Topbar.TopbarOnClickListener() {
            @Override
            public void leftOnClick() {
                MainActivity.sm_menu.openDrawer(GravityCompat.START);
            }

            @Override
            public void rightOnClick() {
                Intent intent = new Intent(mContext, WritePostActivity.class);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    public void initData() {
        userInfo = PrefUtils.getUserServerInfo();
        getHeadVillageInfo();
        rv_home.setLayoutManager(new LinearLayoutManager(getContext()));
        if (rv_home.getAdapter() == null){
            homeListAdapter = new HomeListAdapter(R.layout.layout_home_list_item, mdatas);
            rv_home.setAdapter(homeListAdapter);
            homeListAdapter.addHeaderView(headerView);
        }
        refresh_layout.setColorSchemeColors(Color.RED, Color.BLUE, Color.GREEN);
        refresh_layout.setOnRefreshListener(this);
        homeListAdapter.setOnLoadMoreListener(this);
        //开启loading,获取数据
        setRefreshing(false);
        onRefresh();
    }


    //获取头部信息
    private void getHeadVillageInfo() {
        OkGo.get(AppConst.Village.VILLAGE_INFO)
                .params("homeid", userInfo.getHomeid())
                .execute(new JsonCallback<VillageVO>() {
                    @Override
                    public void onSuccess(VillageVO villageVO, Call call, Response response) {
                        if (villageVO.getData() != null) {
                            VillageVO.DataBean headData = villageVO.getData();
                            setHeadVillageInfo(headData);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        T.showShort(mContext, "获取头部信息错误");
                    }
                });
    }

    //设置头部控件数据
    private void setHeadVillageInfo(VillageVO.DataBean headdata) {
        ImageLoader.getInstance().displayImage(AppConst.SERVER_ADDRESS_IMG + headdata.getVillageIcon(), iv_head_img);
        tv_head_title.setText(headdata.getTitle());
        tvVillageDesc.setText(headdata.getVillageDesc());
        tv_head_guanzhu.setText("关注：" + headdata.getAttentionNum());
        tv_head_tiezi.setText("帖子：" + headdata.getPostNum());
    }

    public void setRefreshing(final boolean refreshing) {
        refresh_layout.post(new Runnable() {
            @Override
            public void run() {
                refresh_layout.setRefreshing(refreshing);
            }
        });
    }


    @Override
    public void onRefresh() {

        //头部内容一起刷新
        getHeadVillageInfo();

        currenPage = 1;
        OkGo.get(AppConst.Post.GET_POSTS)
                .params("page", currenPage)
                .params("homeid", userInfo.getHomeid())
                .params("num", 10)
                .execute(new JsonCallback<PostVO>() {
                    @Override
                    public void onSuccess(PostVO postVO, Call call, Response response) {
                        if (postVO.getData() != null){
                        mdatas.clear();
                        mdatas.addAll(postVO.getData());
                        homeListAdapter.setNewData(mdatas);
                        }
                        T.showShort(mContext, "刷新成功");
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        T.showShort(mContext, "onError" + e);
                    }

                    @Override
                    public void onAfter(PostVO postVO, Exception e) {
                        super.onAfter(postVO, e);
                        //可能需要移除之前添加的布局
                        homeListAdapter.removeAllFooterView();
                        setRefreshing(false);
                    }
                });
    }

    @Override
    public void onLoadMoreRequested() {
        currenPage = currenPage + 1;
        T.showShort(mContext, "加载更多");
        OkGo.get(AppConst.Post.GET_POSTS)
                .params("page", currenPage)
                .params("homeid", userInfo.getHomeid())
                .params("num", 10)
                .execute(new JsonCallback<PostVO>() {
                    @Override
                    public void onSuccess(PostVO postVO, Call call, Response response) {

                        if (postVO.getData() != null) {
                            homeListAdapter.addData(postVO.getData());

                            //显示没有更多数据
                            if (postVO.getData().size() == 0) {
                                homeListAdapter.loadComplete();         //加载完成
                                View noDataView = View.inflate(getActivity(), R.layout.item_no_data,
                                        (ViewGroup) rv_home.getParent());
                                try {
                                    homeListAdapter.addFooterView(noDataView);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        //显示数据加载失败,点击重试
                        homeListAdapter.showLoadMoreFailedView();
                        T.showShort(mContext, "error" + e);
                    }

                });

    }

    @OnClick({R.id.btn_guanzhu, R.id.btn_qiangdao})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_guanzhu:
                T.showShort(getContext(), "关注");
                break;
            case R.id.btn_qiangdao:
                T.showShort(getContext(), "签到");
                break;
        }
    }

    private class HomeListAdapter extends BaseQuickAdapter<PostVO.DataBean> {

        public HomeListAdapter(int layoutResId, List<PostVO.DataBean> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder baseViewHolder, final PostVO.DataBean data) {

            if (data.getUserphoto() != null) {
                ImageView userPhoto = baseViewHolder.getView(R.id.iv_user_photo);
                ImageLoader.getInstance().displayImage(data.getUserphoto().toString(), userPhoto);
            }
            baseViewHolder.setText(R.id.tv_user_name, data.getUsername())
                    .setText(R.id.id_title, data.getTitle())
                    .setText(R.id.id_desc, data.getContent())
                    .setText(R.id.tv_time, data.getCreateTime())
                    .setText(R.id.id_comment, data.getCommentNum());

            baseViewHolder.getView(R.id.item_home__root).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), PostDetailActivity.class);
                    intent.putExtra("postid", data.getId()+"");
                    startActivity(intent);
                }
            });

            NineGridView nineGrid = baseViewHolder.getView(R.id.home_nineGrid);
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
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ///////////
            } else {
                L.e("无图");
                nineGrid.setVisibility(View.GONE);
            }
        }
    }
}
