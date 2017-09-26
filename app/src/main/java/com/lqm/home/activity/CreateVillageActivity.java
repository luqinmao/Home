package com.lqm.home.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lljjcoder.citypickerview.widget.CityPicker;
import com.lqm.home.R;
import com.lqm.home.app.App;
import com.lqm.home.app.AppConst;
import com.lqm.home.utils.T;
import com.lqm.home.widget.Topbar;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import java.io.File;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

import static com.lqm.home.activity.SessionActivity.IMAGE_PICKER;


/**
 * 乡吧创建界面
 */

public class CreateVillageActivity extends BaseActivity {

    private static final int SET_REGION = 7;// 设置所在城市
    private static final int SET_SIGN = 8;// 设置个性签名

    @Bind(R.id.regist_topbar)
    Topbar registTopbar;
    @Bind(R.id.et_village_name)
    EditText etVillageName;
    @Bind(R.id.iv_img)
    ImageView ivImg;
    @Bind(R.id.tv_region)
    TextView tvRegion;
    @Bind(R.id.tv_desc)
    TextView tvDesc;
    @Bind(R.id.btn_create)
    Button btnCreate;

    private String province;
    private String city;
    private String district;
    private String villageDesc;
    private File imgFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_village);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        registTopbar.setTopbarOnClickListener(new Topbar.TopbarOnClickListener() {
            @Override
            public void leftOnClick() {
                finish();
            }

            @Override
            public void rightOnClick() {
            }
        });
    }

    @OnClick({R.id.iv_img, R.id.tv_region, R.id.tv_desc, R.id.btn_create})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_img:
                App.imagePicker.setMultiMode(false);
                Intent mIntent = new Intent(this, ImageGridActivity.class);
                startActivityForResult(mIntent, IMAGE_PICKER);

                break;
            case R.id.tv_region:
                getAddressInfo();
                break;
            case R.id.tv_desc:
                startActivityForResult(new Intent(CreateVillageActivity.this,
                        SignActivity.class), SET_SIGN);
                break;
            case R.id.btn_create:
                createVillage();
                break;
        }
    }

    private void getAddressInfo() {
        //地区选择器
        CityPicker cityPicker = new CityPicker.Builder(CreateVillageActivity.this)
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
                 province = citySelected[0]; //省份
                 city = citySelected[1]; //城市
                 district = citySelected[2];  //区县（如果设定了两级联动，那么该项返回空）
                tvRegion.setText(province+city+district);
            }

            @Override
            public void onCancel() {
            }
        });

    }

    private void createVillage() {
        if (imgFile == null || !imgFile.exists()) {
            T.showShort(CreateVillageActivity.this,"请上传乡吧图标");
            return;
        }else if (TextUtils.isEmpty(etVillageName.getText().toString())
                || TextUtils.isEmpty(villageDesc)
                || TextUtils.isEmpty(district)){
            T.showShort(CreateVillageActivity.this,"请填写相关信息");
            return;
        }
        OkGo.post(AppConst.Village.CREATE_VILLAGE)
                .params("villageName",etVillageName.getText().toString())
                .params("villageDesc",villageDesc)
                .params("province",province)
                .params("city",city)
                .params("district",district)
                .params("villageIcon",imgFile)
                .params("attentionNum",0)
                .params("postNum",0)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        T.showShort(CreateVillageActivity.this,"创建成功");
                        finish();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        T.showShort(CreateVillageActivity.this,"创建乡吧失败");
                    }
                });
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case SET_REGION:
                    if (data != null) {
                        String province = data.getStringExtra("province");
                        String city = data.getStringExtra("city");
                        tvRegion.setText(province + city);
                    }
                    break;

                case SET_SIGN:
                    if (!data.equals("")) {
                         villageDesc = data.getStringExtra("sign");
                        tvDesc.setText(villageDesc);
                    }
            }

        }
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {//返回多张照片
            if (data != null) {
                ArrayList<ImageItem> images = (ArrayList<ImageItem>)
                        data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (images != null && images.size() > 0) {
                    imgFile = new File(images.get(0).path);  //取第一张照片
                    Bitmap bt = BitmapFactory.decodeFile(imgFile.getAbsolutePath());//图片地址
                    ivImg.setImageBitmap(bt);
                }
            }
        }
    }

}
