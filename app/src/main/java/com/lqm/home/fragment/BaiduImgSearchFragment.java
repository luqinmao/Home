package com.lqm.home.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lqm.home.R;
import com.lqm.home.activity.ShowBigImageActivity;
import com.lqm.home.app.AppConst;
import com.lqm.home.imageloader.GlideImageLoader;
import com.lqm.home.model.BaiduSearchImgModel;
import com.lqm.home.parse.JsonCallback;
import com.lqm.home.utils.T;
import com.lzy.ninegrid.NineGridView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 百度图片搜索
 */
public class BaiduImgSearchFragment extends BaseFragment implements
        SwipeRefreshLayout.OnRefreshListener,
        BaseQuickAdapter.RequestLoadMoreListener {

    
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.refresh_layout)
    SwipeRefreshLayout refreshLayout;
    @Bind(R.id.etSearchImg)
    EditText mEtSearchImg;

    private Context context;
    private BaidduImgSearchAdapter mAdapter;
    private boolean isInitCache = false;
    private int mStartNumber; //开始条数
    private static final int PAGELIMIT = 10;
    private Context mContext;
    private String mSearchName;

    public BaiduImgSearchFragment(Context context) {
        this.mContext = context;
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

        mEtSearchImg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                mSearchName = mEtSearchImg.getText().toString();
                if (!mSearchName.equals("")){
                    setRefreshing(true);
                    onRefresh();
                }

            }
        });
        return view;
    }

    @Override
    public void initData() {
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new GridLayoutManager(context,2));
        mAdapter = new BaidduImgSearchAdapter(null);
        mAdapter.isFirstOnly(false);
        recyclerView.setAdapter(mAdapter);
        refreshLayout.setColorSchemeColors(Color.RED, Color.BLUE, Color.GREEN);
        refreshLayout.setOnRefreshListener(this);
        mAdapter.setOnLoadMoreListener(this);
    }

    /** 下拉刷新 */
    @Override
    public void onRefresh() {
        mStartNumber = 1;
        OkGo.get(AppConst.BAIDU_IMG_SEARCH)
                .params("tn", "resultjson")
                .params("ie","utf-8")
                .params("pn",mStartNumber)
                .params("rn",PAGELIMIT)
                .params("word",mSearchName)
                .cacheKey("TabFragment_" + this)
                .cacheMode(CacheMode.FIRST_CACHE_THEN_REQUEST)  //缓存模式先使用缓存,然后使用网络数据
                .execute(new JsonCallback<BaiduSearchImgModel>() {
                    @Override
                    public void onSuccess(BaiduSearchImgModel newsResponse, Call call, Response response) {
                        List<BaiduSearchImgModel.DataBean> datas =  newsResponse.getData();
                        if (datas.size() != 0){
                            mAdapter.setNewData(datas);
                        }
                    }

                    @Override
                    public void onCacheSuccess(BaiduSearchImgModel newsResponse, Call call) {
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
                        T.showShort(getContext(),e.getMessage());
                    }

                    @Override
                    public void onAfter(@Nullable BaiduSearchImgModel newsResponse, @Nullable Exception e) {
                        super.onAfter(newsResponse, e);
                        mAdapter.removeAllFooterView();
                        setRefreshing(false);
                    }
                });
    }

    @Override
    public void onLoadMoreRequested() {
        mStartNumber = mStartNumber +10;
        OkGo.get(AppConst.BAIDU_IMG_SEARCH)
                .params("tn", "resultjson")
                .params("ie","utf-8")
                .params("pn",mStartNumber)
                .params("rn",PAGELIMIT)
                .params("word",mSearchName)
                .cacheMode(CacheMode.NO_CACHE)       //上拉不需要缓存
                .execute(new JsonCallback<BaiduSearchImgModel>() {
                    @Override
                    public void onSuccess(BaiduSearchImgModel newsResponse, Call call, Response response) {
                        if (newsResponse.getData().size() != 0){   //防止崩溃
                            mAdapter.addData(newsResponse.getData());

                            //显示没有更多数据
                            if (newsResponse.getData().size() == 0) {
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
                        T.showShort(getContext(),e.getMessage());
                    }
                });
    }


    public void setRefreshing(final boolean refreshing) {
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(refreshing);
            }
        });
    }

    public class BaidduImgSearchAdapter extends BaseQuickAdapter<BaiduSearchImgModel.DataBean> {
        private Context mContext;

        public BaidduImgSearchAdapter(List<BaiduSearchImgModel.DataBean> data) {
            super(R.layout.item_item_baidu_img, data);
            mContext = context;
        }

        @Override
        protected void convert(BaseViewHolder baseViewHolder, final BaiduSearchImgModel.DataBean bean) {

            ImageView imageView = (ImageView) baseViewHolder.getConvertView().findViewById(R.id.iv_baidu_img);
            if (!TextUtils.isEmpty(bean.getMiddleURL())){
                Glide.with(mContext).load(bean.getMiddleURL()).into(imageView);
            }

            baseViewHolder.getConvertView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, ShowBigImageActivity.class);
                    intent.putExtra("url", bean.getMiddleURL());
                    intent.putExtra("from", "baidu");
                    mContext.startActivity(intent);
                }
            });
        }
    }
}