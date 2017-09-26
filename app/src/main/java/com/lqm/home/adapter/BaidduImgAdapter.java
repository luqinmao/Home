package com.lqm.home.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lqm.home.R;
import com.lqm.home.activity.ShowBigImageActivity;
import com.lqm.home.model.BaiduImgModel;

import java.util.List;

/**
 * 百度图片适配器
 */
public class BaidduImgAdapter extends BaseQuickAdapter<BaiduImgModel.ImgsBean> {
    private Context mContext;

    public BaidduImgAdapter(Context context ,List<BaiduImgModel.ImgsBean> data) {
        super(R.layout.item_item_baidu_img, data);
        mContext = context;
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, final BaiduImgModel.ImgsBean bean) {

        ImageView imageView = (ImageView) baseViewHolder.getConvertView().findViewById(R.id.iv_baidu_img);
        if (!TextUtils.isEmpty(bean.getImageUrl())){
            Glide.with(mContext).load(bean.getImageUrl()).into(imageView);
        }
        baseViewHolder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ShowBigImageActivity.class);
                intent.putExtra("url", bean.getImageUrl());
                intent.putExtra("from", "baidu");
                mContext.startActivity(intent);
            }
        });
    }
}