package com.lqm.home.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lqm.home.R;
import com.lqm.home.app.AppConst;
import com.lqm.home.model.Funny;
import com.lqm.home.parse.JsonCallback;
import com.lqm.home.widget.Topbar;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;

import static com.lqm.home.utilslqr.UIUtils.showToast;

/**
 * 笑话界面
 */

public class FunnyActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener,
        BaseQuickAdapter.RequestLoadMoreListener {


    @Bind(R.id.topbar_funny)
    Topbar topbarFunny;
    @Bind(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @Bind(R.id.refresh_layout)
    SwipeRefreshLayout mRefreshLayout;

    private FunnyAdapter mAdapter;
    private int mCurrentPage;
    private static final int PAGELIMIT = 10;
    private boolean isInitCache = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_funny);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new FunnyAdapter(null);
        mAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        mAdapter.isFirstOnly(false);
        mRecyclerView.setAdapter(mAdapter);

        mRefreshLayout.setColorSchemeColors(Color.RED, Color.BLUE, Color.GREEN);
        mRefreshLayout.setOnRefreshListener(this);
        mAdapter.setOnLoadMoreListener(this);

        mRefreshLayout.setRefreshing(true);
        onRefresh();

        topbarFunny.setTopbarOnClickListener(new Topbar.TopbarOnClickListener() {
            @Override
            public void leftOnClick() {
                finish();
            }

            @Override
            public void rightOnClick() {
            }
        });
    }

    @Override
    public void onRefresh() {
        mCurrentPage = 1;
        OkGo.get(AppConst.TIANXIN_FUNNY)
                .params("key", AppConst.TIANXINKEY)
                .params("page",mCurrentPage)
                .params("num",PAGELIMIT)
                .cacheKey("TabFragment_" + this)       //由于该fragment会被复用,必须保证key唯一,否则数据会发生覆盖
                .cacheMode(CacheMode.FIRST_CACHE_THEN_REQUEST)  //缓存模式先使用缓存,然后使用网络数据
                .execute(new JsonCallback<Funny>() {
                    @Override
                    public void onSuccess(Funny newsResponse, Call call, Response response) {
                        List<Funny.NewslistBean> datas =  newsResponse.getNewslist();
                        if (datas.size() != 0){
                            mAdapter.setNewData(datas);
                        }
                    }

                    @Override
                    public void onCacheSuccess(Funny newsResponse, Call call) {
                        if (!isInitCache) {
                            onSuccess(newsResponse, call, null);
                            isInitCache = true;
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        //网络请求失败的回调,一般会弹个Toast
                        showToast(e.getMessage());
                    }

                    @Override
                    public void onAfter(@Nullable Funny newsResponse, @Nullable Exception e) {
                        super.onAfter(newsResponse, e);
                        mAdapter.removeAllFooterView();
                        mRefreshLayout.setRefreshing(false);
                    }
                });
    }

    @Override
    public void onLoadMoreRequested() {
        mCurrentPage = mCurrentPage +1;
        OkGo.get(AppConst.TIANXIN_FUNNY)
                .params("key", AppConst.TIANXINKEY)
                .params("page",mCurrentPage)
                .params("num",PAGELIMIT)
                .cacheMode(CacheMode.NO_CACHE)       //上拉不需要缓存
                .execute(new JsonCallback<Funny>() {
                    @Override
                    public void onSuccess(Funny newsResponse, Call call, Response response) {
                        if (newsResponse.getNewslist().size() != 0){   //防止崩溃
                            mAdapter.addData(newsResponse.getNewslist());

                            //显示没有更多数据
                            if (newsResponse.getNewslist().size() == 0) {
                                mAdapter.loadComplete();         //加载完成
                                View noDataView = View.inflate(FunnyActivity.this,R.layout.item_no_data,
                                        (ViewGroup)
                                                mRecyclerView.getParent());
                                mAdapter.addFooterView(noDataView);
                            }
                        }else{
                            mAdapter.loadComplete();         //加载完成
//                            T.showShort(FunnyActivity.this,"返回结果为null");
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        //显示数据加载失败,点击重试
                        mAdapter.showLoadMoreFailedView();
                        //网络请求失败的回调,一般会弹个Toast
                        showToast(e.getMessage());
                    }
                });


    }

    class FunnyAdapter extends BaseQuickAdapter<Funny.NewslistBean>{

        public FunnyAdapter(List<Funny.NewslistBean> datas) {
            super(R.layout.item_funny, datas);
        }

        @Override
        protected void convert(final BaseViewHolder holder, final Funny.NewslistBean bean) {
            holder.setText(R.id.tv_funny_title, bean.getTitle());
            String contentString = bean.getContent();
            if (contentString.contains("<br/>")){
                contentString =  contentString.replaceAll("<br/>"," ");
            }
            holder.setText(R.id.tv_funny_content,contentString);
        }
    }
}
