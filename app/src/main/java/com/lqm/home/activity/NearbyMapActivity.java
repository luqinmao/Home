package com.lqm.home.activity;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.lqm.home.R;
import com.lqm.home.imageloader.ImageLoaderManager;
import com.lqm.home.nimsdk.NimUserInfoSDK;
import com.lqm.home.widget.Topbar;
import com.netease.nimlib.sdk.uinfo.constant.GenderEnum;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.util.List;


/**
 * @作者 luqinmao
 * @描述 附近的人，百度地图
 */
public class NearbyMapActivity extends BaseActivity implements SensorEventListener {

    // 定位相关
    LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    BitmapDescriptor mCurrentMarker;
    private SensorManager mSensorManager;
    private Double lastX = 0.0;
    private int mCurrentDirection = 0;
    private double mCurrentLat = 0.0;
    private double mCurrentLon = 0.0;
    private float mCurrentAccracy;

    MapView mMapView;
    BaiduMap mBaiduMap;

    // UI相关
    boolean isFirstLoc = true; // 是否首次定位
    private MyLocationData locData;
    private ImageView mIvCurrentPosition;
    private Topbar mTopbarNearby;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_nearby);
        mIvCurrentPosition = (ImageView) findViewById(R.id.btn_current_position);
        mTopbarNearby = (Topbar)findViewById(R.id.topbar_nearby);
        mMapView = (MapView) findViewById(R.id.bmapView);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);//获取传感器管理服务
        mIvCurrentPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MyLocationConfiguration.LocationMode mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
                mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
                        mCurrentMode, true, mCurrentMarker));
                MapStatus.Builder builder = new MapStatus.Builder();
                LatLng currentPosition = new LatLng(mCurrentLat, mCurrentLon);
                builder.target(currentPosition).zoom(18.0f).overlook(0);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

            }
        });

        // 地图初始化
        mBaiduMap = mMapView.getMap();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();

        mTopbarNearby.setTopbarOnClickListener(new Topbar.TopbarOnClickListener() {
            @Override
            public void leftOnClick() {
                finish();
            }

            @Override
            public void rightOnClick() {

            }
        });

    }

    private void setCustomMaker() {

       List<NimUserInfo> users =  NimUserInfoSDK.getContacts();

        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getExtension().equals("")){
                continue;
            }

            String[] locationInfo = users.get(i).getExtension().split(",");
            double  userLatitude = Double.valueOf(locationInfo[0]);
            double  userLongitude = Double.valueOf(locationInfo[1]);

            View mMarkerView = LayoutInflater.from(this).inflate(R.layout.layout_marker, null);
            ImageView ivPhoto = (ImageView) mMarkerView.findViewById(R.id.iv_photo);
            ImageView ivSex = (ImageView) mMarkerView.findViewById(R.id.iv_sex);
            TextView tvName = (TextView) mMarkerView.findViewById(R.id.tv_name);
            TextView tvDistance = (TextView) mMarkerView.findViewById(R.id.tv_distance);

            LatLng currentposition = new LatLng(userLatitude,userLongitude);
            LatLng position = new LatLng(mCurrentLat, mCurrentLon);
            int distance = (int) DistanceUtil.getDistance(currentposition, position);

            ImageLoaderManager.LoadNetImage(users.get(i).getAvatar(),ivPhoto);
            tvName.setText(users.get(i).getAccount());
            tvDistance.setText(distance+"m");
            if (users.get(i).getGenderEnum() == GenderEnum.FEMALE) {
                ivSex.setImageResource(R.mipmap.ic_gender_female);
            } else if (users.get(i).getGenderEnum() == GenderEnum.MALE) {
                ivSex.setImageResource(R.mipmap.ic_gender_male);
            } else {
                ivSex.setVisibility(View.GONE);
            }

            //将View转换为BitmapDescriptor
            BitmapDescriptor descriptor = BitmapDescriptorFactory.fromView(mMarkerView);
            //设置覆盖物属性，位置、标题、图标。。。new LatLng(double latitude, double longitude)
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(new LatLng(userLatitude, userLongitude))
                    .icon(descriptor).title("titleInfo")
                    .zIndex(9).draggable(true);
            //在地图上添加覆盖物
            mBaiduMap.addOverlay(markerOptions);
        }


        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                startActivity(new Intent(NearbyMapActivity.this,UserInfoActivity.class));
                return false;
            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        double x = sensorEvent.values[SensorManager.DATA_X];
        if (Math.abs(x - lastX) > 1.0) {
            mCurrentDirection = (int) x;
            locData = new MyLocationData.Builder()
                    .accuracy(mCurrentAccracy)
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection).latitude(mCurrentLat)
                    .longitude(mCurrentLon).build();
            mBaiduMap.setMyLocationData(locData);
        }
        lastX = x;

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            mCurrentLat = location.getLatitude();
            mCurrentLon = location.getLongitude();
            mCurrentAccracy = location.getRadius();
            locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

                //自定义Maker
                setCustomMaker();
            }
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
        //为系统的方向传感器注册监听器
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onStop() {
        //取消注册传感器监听
        mSensorManager.unregisterListener(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        // 退出时销毁定位
        mLocClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }

}