package com.lqm.home.fragment;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lqm.home.R;
import com.lqm.home.activity.ArticleDetailActivity;
import com.lqm.home.app.AppConst;
import com.lqm.home.model.PublicModel;
import com.lqm.home.model.ResponseData;
import com.lqm.home.model.WeiXinArticle;
import com.lqm.home.parse.JsonCallback;
import com.lqm.home.utils.T;
import com.lqm.home.widget.DividerItemDecoration;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;

import static com.lqm.home.utilslqr.UIUtils.showToast;

/**
 * Created by luqinmao on 2016/11/9.
 * 微信公众平台文章
 */
public class WeiXinPublicFragment extends BaseFragment implements
        SwipeRefreshLayout.OnRefreshListener,
        BaseQuickAdapter.RequestLoadMoreListener {

    @Bind(R.id.recyclerView_public)
    RecyclerView mRvPublic;
    @Bind(R.id.recyclerView_article)
    RecyclerView mRvArticle;
    @Bind(R.id.refresh_layout)
    SwipeRefreshLayout mRefreshLayout;

    private ArcticleAdapter mAdapter;
    private int mCurrentPage;
    private static final int PAGELIMIT = 10;
    private List<PublicModel> mPublicDatas;
    private String mClickPublicKey;


    @Override
    public View initView() {
        View view = View.inflate(getActivity(), R.layout.frag_weixin_public, null);
        ButterKnife.bind(this, view);

        setPublicList();

        setArticleList();

        return view;
    }

    private void setPublicList() {
        mPublicDatas = new ArrayList<>();
        mRvPublic.setLayoutManager(new LinearLayoutManager(getContext()));
        mRvPublic.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayout.VERTICAL));
        OkGo.get(AppConst.PUBLIC_LIST)
            .execute(new JsonCallback<ResponseData<List<PublicModel>>>() {
                @Override
                public void onSuccess(ResponseData<List<PublicModel>> responseData,
                                      Call call, Response response) {
                    if (responseData.getData() != null){
                        mPublicDatas = responseData.getData();
                        mRvPublic.setAdapter(new PublicAdapter(mPublicDatas));
                        mClickPublicKey = mPublicDatas.get(0).getPublickey();
                        onRefresh();
                    }
                }
                @Override
                public void onError(Call call, Response response, Exception e) {
                    T.showShort(getContext(),"获取数据出错");
                }
            });

    }

    private void setArticleList() {
        mRvArticle.setItemAnimator(new DefaultItemAnimator());
        mRvArticle.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new ArcticleAdapter(null);
        mAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        mAdapter.isFirstOnly(false);
        mRvArticle.setAdapter(mAdapter);

        mRefreshLayout.setColorSchemeColors(Color.RED, Color.BLUE, Color.GREEN);
        mRefreshLayout.setOnRefreshListener(this);
        mAdapter.setOnLoadMoreListener(this);

    }

    @Override
    public void onRefresh() {
        mCurrentPage = 1;
        OkGo.get(AppConst.TIANXIN_WEIXIN_HOME)
            .params("key", AppConst.TIANXINKEY)
            .params("page", mCurrentPage)
            .params("num", PAGELIMIT)
            .params("src", mClickPublicKey)
            .cacheKey("TabFragment_" + this)
            .cacheMode(CacheMode.FIRST_CACHE_THEN_REQUEST)
            .execute(new JsonCallback<WeiXinArticle>() {
                @Override
                public void onSuccess(WeiXinArticle newsResponse, Call call, Response response) {
                    if (newsResponse.getNewslist()!= null) {
                        List<WeiXinArticle.NewslistBean> datas = newsResponse.getNewslist();
                        mAdapter.setNewData(datas);
                    }
                }

                @Override
                public void onError(Call call, Response response, Exception e) {
                    super.onError(call, response, e);
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
        OkGo.get(AppConst.TIANXIN_WEIXIN_HOME)
            .params("key", AppConst.TIANXINKEY)
            .params("page", mCurrentPage)
            .params("num", PAGELIMIT)
            .params("src", mClickPublicKey)
            .cacheKey("TabFragment_" + this)
            .cacheMode(CacheMode.NO_CACHE)       //上拉不需要缓存
            .execute(new JsonCallback<WeiXinArticle>() {
                @Override
                public void onSuccess(WeiXinArticle newsResponse, Call call, Response response) {
                    if ( newsResponse.getNewslist()!= null ) {
                        mAdapter.addData(newsResponse.getNewslist());
                        if (newsResponse.getNewslist().size() == 0) {
                            mAdapter.loadComplete();         //加载完成
                            View noDataView = View.inflate(getContext(), R.layout.item_no_data,
                                    (ViewGroup) mRvArticle.getParent());
                            mAdapter.addFooterView(noDataView);
                        }
                    } else {
                        mAdapter.loadComplete();         //加载完成
//                        T.showShort(getContext(), "返回结果为null");
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


    public void setRefreshing(final boolean refreshing) {
        mRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.setRefreshing(refreshing);
            }
        });
    }

    class PublicAdapter extends BaseQuickAdapter<PublicModel>{

        public PublicAdapter(List<PublicModel> data) {
            super(R.layout.item_weixin_public, data);
        }

        @Override
        protected void convert(BaseViewHolder holder, final PublicModel model) {
            holder.setText(R.id.tv_public_name,model.getName())
                    .setText(R.id.tv_public_key,model.getPublickey());
            ImageView imageView = holder.getView(R.id.iv_public_icon);
            Glide.with(getContext()).load(AppConst.SERVER_ADDRESS_IMG+model.getIcon()).into(imageView);

            holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mClickPublicKey = model.getPublickey();
                    onRefresh();
                }
            });
        }
    }

     class ArcticleAdapter extends BaseQuickAdapter<WeiXinArticle.NewslistBean> {

        public ArcticleAdapter(List<WeiXinArticle.NewslistBean> datas) {
            super(R.layout.item_weixin_public_article, datas);
        }

        @Override
        protected void convert(final BaseViewHolder holder, final WeiXinArticle.NewslistBean bean) {
            holder.setText(R.id.tv_article_title,bean.getTitle())
                    .setText(R.id.tv_article_time,bean.getCtime())
                    .setText(R.id.tv_article_content,bean.getDescription());
            ImageView imgView = holder.getView(R.id.iv_img);
            ImageLoader.getInstance().displayImage(bean.getPicUrl(),imgView);
            holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ArticleDetailActivity.runActivity(mContext, bean.getUrl(), bean.getTitle(),bean.getPicUrl());
                }
            });
        }
    }
}
