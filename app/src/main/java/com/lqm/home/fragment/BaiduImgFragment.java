package com.lqm.home.fragment;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lqm.home.R;
import com.lqm.home.adapter.BaidduImgAdapter;
import com.lqm.home.app.AppConst;
import com.lqm.home.imageloader.GlideImageLoader;
import com.lqm.home.model.BaiduImgModel;
import com.lqm.home.parse.JsonCallback;
import com.lzy.ninegrid.NineGridView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 百度图片
 */
public class BaiduImgFragment extends BaseFragment implements
        SwipeRefreshLayout.OnRefreshListener,
        BaseQuickAdapter.RequestLoadMoreListener {

    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.refresh_layout)
    SwipeRefreshLayout refreshLayout;
    @Bind(R.id.etSearchImg)
    EditText etSearchImg;

    private Context context;
    private BaidduImgAdapter mAdapter;
    private boolean isInitCache = false;
    private int mStartNumber;
    private static final int PAGELIMIT = 10;
    private String mCol;
    private String mTag;


    public BaiduImgFragment(){
    }

    public BaiduImgFragment(String col, String tag) {
        this.mCol = col;
        this.mTag = tag;
    }

    @Override
    public void onAttach(Context context) {
        this.context = context;
        NineGridView.setImageLoader(new GlideImageLoader());
        super.onAttach(context);
    }

    @Override
    public View initView() {
        View view = View.inflate(getActivity(),R.layout.frag_baidu_img, null);
        ButterKnife.bind(this, view);
        etSearchImg.setVisibility(View.GONE);
        return view;
    }

    @Override
    public void initData() {
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new GridLayoutManager(context,2));
        mAdapter = new BaidduImgAdapter(getContext(),null);
        mAdapter.isFirstOnly(false);
        recyclerView.setAdapter(mAdapter);
        refreshLayout.setColorSchemeColors(Color.RED, Color.BLUE, Color.GREEN);
        refreshLayout.setOnRefreshListener(this);
        mAdapter.setOnLoadMoreListener(this);

        //开启loading,获取数据
        setRefreshing(true);
        onRefresh();
    }

    /** 下拉刷新 */
    @Override
    public void onRefresh() {
        mStartNumber = 1;
        OkGo.get(AppConst.BAIDU_IMG_SORT)
                .params("col", mCol)
                .params("tag",mTag)
                .params("pn",mStartNumber)
                .params("rn",PAGELIMIT)
                .params("p","channel")
                .params("from",1)
                .params("sort",0)
                .cacheKey("TabFragment_" + this)
                .cacheMode(CacheMode.FIRST_CACHE_THEN_REQUEST)  //缓存模式先使用缓存,然后使用网络数据
                .execute(new JsonCallback<BaiduImgModel>() {
                    @Override
                    public void onSuccess(BaiduImgModel newsResponse, Call call, Response response) {
                        List<BaiduImgModel.ImgsBean> datas =  newsResponse.getImgs();
                        if (datas.size() != 0){
                            mAdapter.setNewData(datas);
                        }
                    }

                    @Override
                    public void onCacheSuccess(BaiduImgModel newsResponse, Call call) {
                        if (!isInitCache) {
                            onSuccess(newsResponse, call, null);
                            isInitCache = true;
                        }
                    }

                    @Override
                    public void onCacheError(Call call, Exception e) {
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        showToast(e.getMessage());
                    }

                    @Override
                    public void onAfter(@Nullable BaiduImgModel newsResponse, @Nullable Exception e) {
                        super.onAfter(newsResponse, e);
                        mAdapter.removeAllFooterView();
                        setRefreshing(false);
                    }
                });
    }

    @Override
    public void onLoadMoreRequested() {
        mStartNumber = mStartNumber +10;
        OkGo.get(AppConst.BAIDU_IMG_SORT)
                .params("col", mCol)
                .params("tag",mTag)
                .params("pn",mStartNumber)
                .params("rn",PAGELIMIT)
                .params("p","channel")
                .params("from",1)
                .params("sort",0)
                .cacheMode(CacheMode.NO_CACHE)       //上拉不需要缓存
                .execute(new JsonCallback<BaiduImgModel>() {
                    @Override
                    public void onSuccess(BaiduImgModel newsResponse, Call call, Response response) {
                        if (newsResponse.getImgs().size() != 0){   //防止崩溃
                            mAdapter.addData(newsResponse.getImgs());

                            //显示没有更多数据
                            if (newsResponse.getImgs().size() == 0) {
                                mAdapter.loadComplete();         //加载完成
                                View noDataView = View.inflate(getActivity(),R.layout.item_no_data, (ViewGroup)
                                        recyclerView.getParent());
                                mAdapter.addFooterView(noDataView);
                            }
                        }else{
                            mAdapter.loadComplete();         //加载完成
//                            T.showShort(getActivity(),"返回结果为null");
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        mAdapter.showLoadMoreFailedView();
                        showToast(e.getMessage());
                    }
                });
    }

    public void showToast(String msg) {
        Snackbar.make(recyclerView, msg, Snackbar.LENGTH_SHORT).show();
    }

    public void setRefreshing(final boolean refreshing) {
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(refreshing);
            }
        });
    }
}