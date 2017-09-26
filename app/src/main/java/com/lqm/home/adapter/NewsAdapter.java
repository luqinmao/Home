package com.lqm.home.adapter;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lqm.home.R;
import com.lqm.home.activity.WebViewActivity;
import com.lqm.home.model.NewsModel;
import com.lzy.ninegrid.ImageInfo;
import com.lzy.ninegrid.NineGridView;
import com.lzy.ninegrid.preview.NineGridViewClickAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 新闻适配器
 */
public class NewsAdapter extends BaseQuickAdapter<NewsModel.NewslistBean> {

    public NewsAdapter(List<NewsModel.NewslistBean> data) {
        super(R.layout.item_news, data);
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, final NewsModel.NewslistBean contentList) {
        baseViewHolder.setText(R.id.title, contentList.getTitle())//
//                .setText(R.id.desc, ())//
                .setText(R.id.pubDate, contentList.getCtime())//
                .setText(R.id.source, contentList.getDescription());

        View view = baseViewHolder.getConvertView();
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebViewActivity.runActivity(mContext, contentList.getTitle(), contentList.getUrl(),"news");
            }
        });

        NineGridView nineGrid = baseViewHolder.getView(R.id.nineGrid);
        ArrayList<ImageInfo> imageInfo = new ArrayList<>();
        List<String> images =  new ArrayList<>();
        if(contentList.getPicUrl() != null){
            images.add(contentList.getPicUrl());
        }
        if (images != null) {
            for (String image : images) {
                ImageInfo info = new ImageInfo();
                info.setThumbnailUrl(image);
                info.setBigImageUrl(image);
                imageInfo.add(info);
            }
        }
        nineGrid.setAdapter(new NineGridViewClickAdapter(mContext, imageInfo));

//        if (images != null && images.size() == 1) {
//            nineGrid.setSingleImageRatio(images.get(0).width * 1.0f / images.get(0).height);
//        }
    }
}