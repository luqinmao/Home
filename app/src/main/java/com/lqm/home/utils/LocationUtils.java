package com.lqm.home.utils;

import android.content.Context;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

/**
 * @作者  luqinmao
 * @描述  百度定位
 */

public class LocationUtils {

    private Context mContext;
    private LocationInterface locationInterface;
    private LocationClient mLocClient;


    public LocationUtils(Context mContext) {
        this.mContext = mContext;
        initLocation();
    }

    /**
     * 初始化定位
     */
    private void initLocation() {
        mLocClient = new LocationClient(mContext);
        mLocClient.registerLocationListener(new MyLocationListenner());
        mLocClient.setLocOption(getDefaultOption());
        mLocClient.start();
    }

    /**
     * 默认的定位参数
     */
    private static LocationClientOption getDefaultOption() {
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        return option;
    }


    /**
     * 定位监听
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null || location.getLongitude() == 0.0) {
                return;
            }else{
                locationInterface.addressCallBack(
                        location.getCity(),
                        location.getLatitude(),
                        location.getLongitude()
                );
                mLocClient.stop();
            }
        }
    }


    public interface LocationInterface {
        /**
         * @param city 城市
         * @param lon 经度
         * @param lat 纬度
         */
        void addressCallBack(String city,double lon, double lat);
    }

    public void setCallBack(LocationInterface locationCallBack) {
        this.locationInterface = locationCallBack;
    }

}


