package com.lqm.home.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lljjcoder.citypickerview.widget.CityPicker;
import com.lqm.home.R;
import com.lqm.home.app.AppConst;
import com.lqm.home.broadcast.UpdateVillageEvent;
import com.lqm.home.model.ResponseData;
import com.lqm.home.model.UserServer;
import com.lqm.home.model.VillagesVO;
import com.lqm.home.parse.JsonCallback;
import com.lqm.home.utils.PrefUtils;
import com.lqm.home.utils.T;
import com.lqm.home.utilslqr.KeyBoardUtils;
import com.lqm.home.utilslqr.UIUtils;
import com.lzy.okgo.OkGo;
import com.nostra13.universalimageloader.core.ImageLoader;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 切换乡吧
 */
public class SelectHomeActivity extends BaseActivity {


    @Bind(R.id.tv_select_region)
    TextView tvSelectRegion;
    @Bind(R.id.etSearch)
    EditText mEtSearch;
    @Bind(R.id.rv_village)
    RecyclerView rvVillage;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.btnOk)
    Button btnOk;


    private BaseQuickAdapter<VillagesVO.DataBean> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_home);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        initToolbar();
        rvVillage.setLayoutManager(new LinearLayoutManager(SelectHomeActivity.this));

        //监听键盘回车或搜索
        mEtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (TextUtils.isEmpty(mEtSearch.getText().toString().trim())) {
                    KeyBoardUtils.closeKeybord(mEtSearch, SelectHomeActivity.this);
                } else {
                    searchHomesByName(mEtSearch.getText().toString().trim());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @OnClick({R.id.tv_select_region, R.id.btnOk})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_select_region:
                KeyBoardUtils.closeKeybord(mEtSearch, SelectHomeActivity.this);
                //地区选择器
                CityPicker cityPicker = new CityPicker.Builder(SelectHomeActivity.this)
                        .textSize(20)
                        .title("地址选择")
                        .backgroundPop(0xa0000000)
                        .titleBackgroundColor("#DDDDDD")
                        .titleTextColor("#464646")
                        .backgroundPop(0xa0000000)
                        .confirTextColor("#09A8F6")
                        .cancelTextColor("#09A8F6")
                        .province("广东省")
                        .city("汕尾市")
                        .district("陆丰市")
                        .textColor(Color.parseColor("#000000"))
                        .provinceCyclic(true)
                        .cityCyclic(false)
                        .districtCyclic(false)
                        .visibleItemsCount(7)
                        .itemPadding(10)
                        .onlyShowProvinceAndCity(false)
                        .build();
                cityPicker.show();

                //监听方法，获取选择结果
                cityPicker.setOnCityItemClickListener(new CityPicker.OnCityItemClickListener() {
                    @Override
                    public void onSelected(String... citySelected) {
                        //省份
                        String province = citySelected[0];
                        //城市
                        String city = citySelected[1];
                        //区县（如果设定了两级联动，那么该项返回空）
                        String district = citySelected[2];
                        tvSelectRegion.setText(province + city + district);
                        searchHomesByDistrict(district);
                    }

                    @Override
                    public void onCancel() {
                    }
                });
                break;

            case R.id.btnOk:
                startActivity(new Intent(this, CreateVillageActivity.class));
                break;
        }
    }

    private void searchHomesByDistrict(String district) {
        OkGo.post(AppConst.Village.GET_VILLAGES_BY_DISTRICT)
                .params("district", district)
                .execute(new JsonCallback<VillagesVO>() {
                    @Override
                    public void onSuccess(VillagesVO villagesVO, Call call, Response response) {
                        if (villagesVO.getData() != null){
                            setAdapter(villagesVO);
                        }
                    }
                });
    }

    private void searchHomesByName(String name) {
        OkGo.post(AppConst.Village.GET_VILLAGES_BY_NAME)
                .params("villageName", name)
                .execute(new JsonCallback<VillagesVO>() {
                    @Override
                    public void onSuccess(VillagesVO villagesVO, Call call, Response response) {
                        if (villagesVO.getData() != null){
                            setAdapter(villagesVO);
                        }
                    }
                });
    }

    private void setAdapter(VillagesVO villagesVO) {
        if (mAdapter == null) {
            mAdapter = new BaseQuickAdapter<VillagesVO.DataBean>(R.layout.item_village_search, villagesVO.getData()) {
                @Override
                protected void convert(BaseViewHolder baseViewHolder, final VillagesVO.DataBean dataBean) {
                    if (dataBean.getVillageIcon() != null) {
                        ImageView villageView = baseViewHolder.getView(R.id.iv_village_icon);
                        ImageLoader.getInstance().displayImage(AppConst.SERVER_ADDRESS_IMG + dataBean.getVillageIcon(), villageView);
                    }
                    baseViewHolder
                            .setText(R.id.tv_title, dataBean.getTitle())
                            .setText(R.id.tv_desc, dataBean.getVillageDesc())
                            .setText(R.id.tv_address, dataBean.getProvince() + dataBean.getCity() + dataBean
                                    .getDistrict());
//                            .setText(R.id.tv_attentionNum,"关注人数："+dataBean.getAttentionNum())
//                            .setText(R.id.tv_postNum,"帖子数："+dataBean.getPostNum());
                    baseViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
//                            T.showShort(SelectHomeActivity.this, "item点击");
                        }
                    });
                    baseViewHolder.itemView.findViewById(R.id.btn_replace).setOnClickListener(new View
                            .OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            replaceVillage(dataBean.getId());
                        }
                    });

                }
            };
            rvVillage.setAdapter(mAdapter);
        } else {
            mAdapter.setNewData(villagesVO.getData());
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 切换乡吧
     */
    private void replaceVillage(int villageId) {
        OkGo.post(AppConst.User.REPLACE_VILLAGE)
                .params("userid", PrefUtils.getUserServerInfo().getId())
                .params("villageid", villageId)
        .execute(new JsonCallback<ResponseData<UserServer>>() {
            @Override
            public void onSuccess(ResponseData<UserServer> responseData, Call call, Response response) {
                PrefUtils.saveUserServerInfo(responseData.getData());
                EventBus.getDefault().post(new UpdateVillageEvent());
                T.showShort(SelectHomeActivity.this,"已切换关注的乡吧");
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationIcon(R.mipmap.ic_back);
        mEtSearch.setVisibility(View.VISIBLE);
        mEtSearch.setHintTextColor(UIUtils.getColor(R.color.gray2));
        mEtSearch.setTextColor(UIUtils.getColor(R.color.white));
        btnOk.setVisibility(View.VISIBLE);
        btnOk.setText("创建");
    }

}
