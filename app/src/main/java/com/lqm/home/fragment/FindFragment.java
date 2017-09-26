package com.lqm.home.fragment;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lqm.home.R;
import com.lqm.home.activity.BaiDuImageActivity;
import com.lqm.home.activity.FunnyActivity;
import com.lqm.home.activity.NearbyMapActivity;
import com.lqm.home.activity.WeiXinArticleActivity;
import com.lqm.home.app.AppConst;
import com.lqm.home.model.Ad;
import com.lqm.home.model.FindItemModel;
import com.lqm.home.model.ResponseData;
import com.lqm.home.parse.JsonCallback;
import com.lqm.home.utils.T;
import com.lzy.okgo.OkGo;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.bgabanner.BGABanner;
import me.drakeet.materialdialog.MaterialDialog;
import okhttp3.Call;
import okhttp3.Response;

import static com.lqm.home.R.id.vp_ad;

/**
 * 发现模块
 */
public class FindFragment extends BaseFragment {
    private View view;
    private RecyclerView mRvFind;
    private List<FindItemModel> items;
    private BGABanner mVpAd;

    private List<Ad> mAdDatas;
    private View mAdLayout;

    public static FindFragment newInstance() {
        return new FindFragment();
    }

    @Override
    public View initView() {
         view = View.inflate(getActivity(), R.layout.frag_find, null);
         mAdLayout = LinearLayout.inflate(getContext(),R.layout.layout_ad_head,null);
         mVpAd = (BGABanner) mAdLayout.findViewById(vp_ad);

         setViewPager();
         return view;
    }

    @Override
    public void initData() {
        items = new ArrayList<>();
        int[] itembgDrawables = {R.mipmap.find_1, R.mipmap.find_2, R.mipmap.find_3, R.mipmap.find_4};
        int[] itemIcons = {R.mipmap.find_icon_2, R.mipmap.find_icon_1, R.mipmap.find_icon_3, R.mipmap.find_icon_4};
        String[] itemTitles = getContext().getResources().getStringArray(R.array.find_item_title);
        String[] itemContents = getContext().getResources().getStringArray(R.array.find_item_content);
        for (int i = 0; i < itembgDrawables.length; i++) {
            FindItemModel item = new FindItemModel();
            item.setIcon(itemIcons[i]);
            item.setDrawable(itembgDrawables[i]);
            item.setTitle(itemTitles[i]);
            item.setContent(itemContents[i]);
            items.add(item);
        }

        mRvFind = (RecyclerView) view.findViewById(R.id.rv_find);
        mRvFind.setLayoutManager(new LinearLayoutManager(getContext()));
        FindAdapter findAdapter =  new FindAdapter(R.layout.item_find, items);
        mRvFind.setAdapter(findAdapter);
        findAdapter.addHeaderView(mAdLayout);

    }

    private void setViewPager() {
        mVpAd.setDelegate(new BGABanner.Delegate() {
            @Override
            public void onBannerItemClick(BGABanner banner, View itemView, Object model, final int position) {
                final MaterialDialog dialog = new MaterialDialog(getContext());
                dialog.setTitle("是否启动浏览器访问?");
                dialog.setMessage(mAdDatas.get(position).getUrl());
                dialog.setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uri = Uri.parse(mAdDatas.get(position).getUrl());
                        Intent it = new Intent(Intent.ACTION_VIEW, uri);
                        getContext().startActivity(it);
                        dialog.dismiss();
                    }
                });
                dialog.setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        OkGo.get(AppConst.GETAD)
            .execute(new JsonCallback<ResponseData<List<Ad>>>() {
                @Override
                public void onSuccess(ResponseData<List<Ad>> responseData, Call call, Response response) {
                    if (responseData.getData().size() != 0) {
                        mAdDatas = responseData.getData();
                        List<String> tips = new ArrayList<>();
                        for (Ad model:mAdDatas) {
                           String content =  model.getContent();
                            tips.add(content);
                        }
                        mVpAd.setData(mAdDatas,tips);
                        mVpAd.setAdapter(new BGABanner.Adapter<ImageView, Ad>() {
                            @Override
                            public void fillBannerItem(BGABanner banner, ImageView itemView,
                                                       Ad model, int position) {
                                Glide.with(getContext()).load(AppConst.SERVER_ADDRESS_IMG+
                                        model.getImg()).into(itemView);
                            }
                        });

                    }
                }

                @Override
                public void onError(Call call, Response response, Exception e) {
                    mVpAd.setVisibility(View.GONE);
                    T.showShort(getContext(), "请求广告数据错误" + e.toString());
                }
            });
    }

    class FindAdapter extends BaseQuickAdapter<FindItemModel> {

        public FindAdapter(int layoutResId, List<FindItemModel> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder baseViewHolder, final FindItemModel item) {
            baseViewHolder.setText(R.id.tv_find_title, item.getTitle());
            baseViewHolder.setText(R.id.tv_find_content, item.getContent());
            baseViewHolder.setImageResource(R.id.iv_find_icon, item.getIcon());
            baseViewHolder.setBackgroundRes(R.id.rl_bg, item.getDrawable());

            baseViewHolder.getConvertView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (item.getTitle()) {
                        case "微信文章":
                            startActivity(new Intent(getActivity(), WeiXinArticleActivity.class));
                            break;
                        case "开心笑话":
                            startActivity(new Intent(getActivity(), FunnyActivity.class));
                            break;
                        case "美图美景":
                            startActivity(new Intent(getActivity(), BaiDuImageActivity.class));
                            break;
                        case "附近的人":
                            startActivity(new Intent(getActivity(), NearbyMapActivity.class));
                            break;
                    }
                }
            });
        }
    }

}
