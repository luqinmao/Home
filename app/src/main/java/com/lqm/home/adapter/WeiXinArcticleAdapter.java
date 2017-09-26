package com.lqm.home.adapter;

import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lqm.home.R;
import com.lqm.home.activity.ArticleDetailActivity;
import com.lqm.home.model.WeiXinArticle;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by luqinmao on 2017/6/6.
 */

public class WeiXinArcticleAdapter extends BaseQuickAdapter<WeiXinArticle.NewslistBean> {

    public WeiXinArcticleAdapter(List<WeiXinArticle.NewslistBean> datas) {
        super(R.layout.item_weixin_article, datas);
    }

    @Override
    protected void convert(final BaseViewHolder holder, final WeiXinArticle.NewslistBean bean) {
        holder.setText(R.id.tv_article_title,bean.getTitle())
                .setText(R.id.tv_article_time,bean.getCtime())
                .setText(R.id.tv_article_form,bean.getDescription());
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