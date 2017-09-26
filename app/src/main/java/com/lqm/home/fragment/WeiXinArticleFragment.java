package com.lqm.home.fragment;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lqm.home.R;
import com.lqm.home.adapter.WeiXinArcticleAdapter;
import com.lqm.home.app.AppConst;
import com.lqm.home.model.WeiXinArticle;
import com.lqm.home.parse.JsonCallback;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;

import static com.lqm.home.R.id.recyclerView;
import static com.lqm.home.utilslqr.UIUtils.showToast;

/**
 * Created by luqinmao on 2016/11/9.
 * 微信精选文章
 */
public class WeiXinArticleFragment extends BaseFragment implements
        SwipeRefreshLayout.OnRefreshListener,
        BaseQuickAdapter.RequestLoadMoreListener {

    @Bind(recyclerView)
    RecyclerView mRecyclerView;
    @Bind(R.id.refresh_layout)
    SwipeRefreshLayout mRefreshLayout;

    private WeiXinArcticleAdapter mAdapter;
    private int mCurrentPage;
    private static final int PAGELIMIT = 10;
    private boolean isInitCache = false;


    @Override
    public View initView() {
        View view = View.inflate(getActivity(), R.layout.frag_weixin_article, null);
        ButterKnife.bind(this, view);

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new WeiXinArcticleAdapter(null);
//        mAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        mAdapter.isFirstOnly(false);
        mRecyclerView.setAdapter(mAdapter);

        mRefreshLayout.setColorSchemeColors(Color.RED, Color.BLUE, Color.GREEN);
        mRefreshLayout.setOnRefreshListener(this);
        mAdapter.setOnLoadMoreListener(this);

        mRefreshLayout.setRefreshing(true);
        onRefresh();

        return view;
    }

    @Override
    public void onRefresh() {
        mCurrentPage = 1;
        OkGo.get(AppConst.TIANXIN_WEIXIN_ARTICLE)
                .params("key", AppConst.TIANXINKEY)
                .params("page", mCurrentPage)
                .params("num", PAGELIMIT)
                .params("rand", 1) //1:随机获取
                .params("word", "精选")
                .cacheKey("TabFragment_" + this)       //由于该fragment会被复用,必须保证key唯一,否则数据会发生覆盖
                .cacheMode(CacheMode.FIRST_CACHE_THEN_REQUEST)  //缓存模式先使用缓存,然后使用网络数据
                .execute(new JsonCallback<WeiXinArticle>() {
                    @Override
                    public void onSuccess(WeiXinArticle newsResponse, Call call, Response response) {
                        List<WeiXinArticle.NewslistBean> datas = newsResponse.getNewslist();
                        if (datas.size() != 0) {
                            mAdapter.setNewData(datas);
                        }
                    }

                    @Override
                    public void onCacheSuccess(WeiXinArticle newsResponse, Call call) {
                        if (!isInitCache) {
                            onSuccess(newsResponse, call, null);
                            isInitCache = true;
                        }
                    }

                    @Override
                    public void onCacheError(Call call, Exception e) {
                        //获取缓存失败的回调方法,一般很少用到,需要就复写,不需要不用关心
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        //网络请求失败的回调,一般会弹个Toast
                        showToast(e.getMessage());
                    }

                    @Override
                    public void onAfter(@Nullable WeiXinArticle newsResponse, @Nullable Exception e) {
                        super.onAfter(newsResponse, e);
                        mAdapter.removeAllFooterView();
                        setRefreshing(false);
                    }
                });
    }

    @Override
    public void onLoadMoreRequested() {
        mCurrentPage = mCurrentPage + 1;
        OkGo.get(AppConst.TIANXIN_WEIXIN_ARTICLE)
                .params("key", AppConst.TIANXINKEY)
                .params("page", mCurrentPage)
                .params("num", PAGELIMIT)
                .params("word", "精选")
                .params("rand", 1) //1:随机获取
                .cacheMode(CacheMode.NO_CACHE)       //上拉不需要缓存
                .execute(new JsonCallback<WeiXinArticle>() {
                    @Override
                    public void onSuccess(WeiXinArticle newsResponse, Call call, Response response) {
                        if (newsResponse.getNewslist().size() != 0) {   //防止崩溃
                            mAdapter.addData(newsResponse.getNewslist());

                            //显示没有更多数据
                            if (newsResponse.getNewslist().size() == 0) {
                                mAdapter.loadComplete();         //加载完成
                                View noDataView = View.inflate(getContext(), R.layout.item_no_data,
                                        (ViewGroup)
                                                mRecyclerView.getParent());
                                mAdapter.addFooterView(noDataView);
                            }
                        } else {
                            mAdapter.loadComplete();         //加载完成
//                            T.showShort(getContext(), "返回结果为null");
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

    public void setRefreshing(final boolean refreshing) {
        mRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.setRefreshing(refreshing);
            }
        });
    }
}
