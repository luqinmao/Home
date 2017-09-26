package com.lqm.home.app;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Environment;
import android.os.Handler;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lqm.home.R;
import com.lqm.home.activity.SplashActivity;
import com.lqm.home.imageloader.GlideImageLoader;
import com.lqm.home.model.UserCache;
import com.lqm.home.nimsdk.NimAccountSDK;
import com.lqm.home.nimsdk.utils.ScreenUtil;
import com.lqm.home.nimsdk.utils.StorageUtils;
import com.lqr.emoji.IImageLoader;
import com.lqr.emoji.LQREmotionKit;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.view.CropImageView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.cookie.store.PersistentCookieStore;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.SDKOptions;
import com.netease.nimlib.sdk.StatusBarNotificationConfig;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.uinfo.UserInfoProvider;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;

import org.litepal.LitePal;

import java.io.File;
import java.util.LinkedList;
import java.util.List;


/**
 * Created by luqinmao on 2016/11/30.
 */
public class App extends MultiDexApplication {
    public static App instance;
    public static List<Activity> activities = new LinkedList<Activity>();
    public static ImagePicker imagePicker;

    @Override
    public void onCreate() {
        super.onCreate();
        //对全局属性赋值
        mContext = getApplicationContext();
        mMainThreadId = android.os.Process.myTid();
        mHandler = new Handler();
        instance = this;

        initNim();
        //百度地图
        SDKInitializer.initialize(this);
        SDKInitializer.setCoordType(CoordType.BD09LL); //百度坐标类型

        initImagePicker();
        initOkGo();
        LitePal.initialize(this); //litepal数据库
        initPush(); //推送
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(getContext()));
        LQREmotionKit.init(this, Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "sticker", new IImageLoader() {
            @Override
            public void displayImage(Context context, String path, ImageView imageView) {
                Glide.with(context).load(path).centerCrop().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(imageView);
            }
        });
    }

    //信鸽推送
    private void initPush() {
        // 开启logcat输出，方便debug，发布时请关闭
         XGPushConfig.enableDebug(this, true);
        // 如果需要知道注册是否成功，请使用registerPush(getApplicationContext(), XGIOperateCallback)带callback版本
        // 如果需要绑定账号，请使用registerPush(getApplicationContext(),account)版本
        // 具体可参考详细的开发指南
        // 传递的参数为ApplicationContext
//        XGPushManager.registerPush(mContext);
//        L.e("信鸽token:"+XGPushConfig.getToken(mContext));

        XGPushManager.registerPush(this, new XGIOperateCallback() {
            @Override
            public void onSuccess(Object data, int flag) {
                Log.d("TPush", "注册成功，设备token为：" + data);
            }
            @Override
            public void onFail(Object data, int errCode, String msg) {
                Log.d("TPush", "注册失败，错误码：" + errCode + ",错误信息：" + msg);
            }
        });
    }

    private void initNim() {
        // SDK初始化（启动后台服务，若已经存在用户登录信息， SDK 将完成自动登录，只是后台登录而已，跳转逻辑需要自己处理）
        NIMClient.init(this, loginInfo(), options());
        StorageUtils.init(mContext, null);
        ScreenUtil.init(mContext);
    }

    /**
     * 完全退出
     */
    public static void exit() {
        for (Activity activity : activities) {
            activity.finish();
        }
    }
    /**
     * 初始化仿微信控件ImagePicker
     */
    private void initImagePicker() {
        imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());   //设置图片加载器
        imagePicker.setShowCamera(true);  //显示拍照按钮
        imagePicker.setCrop(true);        //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(true); //是否按矩形区域保存
        imagePicker.setSelectLimit(9);    //选中数量限制
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
        imagePicker.setFocusWidth(800);   //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(800);  //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(1000);//保存文件的宽度。单位像素
        imagePicker.setOutPutY(1000);//保存文件的高度。单位像素
    }

    //以下属性应用于整个应用程序，合理利用资源，减少资源浪费
    private static Context mContext;//上下文
    private static long mMainThreadId;//主线程id
    private static Handler mHandler;//主线程Handler

    public static Context getContext() {
        return mContext;
    }

    public static void setContext(Context mContext) {
        App.mContext = mContext;
    }


    public static long getMainThreadId() {
        return mMainThreadId;
    }

    public static void setMainThreadId(long mMainThreadId) {
        App.mMainThreadId = mMainThreadId;
    }


    public static Handler getMainHandler() {
        return mHandler;
    }

    public static void setMainHandler(Handler mHandler) {
        App.mHandler = mHandler;
    }


    public static DisplayImageOptions options = new DisplayImageOptions.Builder()//
            .showImageOnLoading(R.mipmap.default_image)         //设置图片在下载期间显示的图片
            .showImageForEmptyUri(R.mipmap.default_image)       //设置图片Uri为空或是错误的时候显示的图片
            .showImageOnFail(R.mipmap.default_image)            //设置图片加载/解码过程中错误时候显示的图片
            .cacheInMemory(true)                                //设置下载的图片是否缓存在内存中
            .cacheOnDisk(true)                                  //设置下载的图片是否缓存在SD卡中
            .build();                                           //构建完成

    private void initOkGo() {
        //必须调用初始化
        OkGo.init(this);
        try {
            OkGo.getInstance()
                    .debug("OkGo")
                    .setCacheMode(CacheMode.NO_CACHE)
                    .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)
                    .setRetryCount(3)
                    .setCookieStore(new PersistentCookieStore());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static App getInstance() {
        return instance;
    }

    /*================== 网易云信 Begin ==================*/
    // 如果返回值为 null，则全部使用默认参数。
    private SDKOptions options() {
        SDKOptions options = new SDKOptions();

        // 如果将新消息通知提醒托管给 SDK 完成，需要添加以下配置。否则无需设置。
        StatusBarNotificationConfig config = new StatusBarNotificationConfig();
        config.notificationEntrance = SplashActivity.class; // 点击通知栏跳转到该Activity
        config.notificationSmallIconId = R.mipmap.ic_launcher;
        // 呼吸灯配置
        config.ledARGB = Color.GREEN;
        config.ledOnMs = 1000;
        config.ledOffMs = 1500;
        // 通知铃声的uri字符串
        config.notificationSound = "android.resource://com.lqr.wechat/raw/msg";
        options.statusBarNotificationConfig = config;

        // 配置保存图片，文件，log 等数据的目录
        // 如果 options 中没有设置这个值，SDK 会使用下面代码示例中的位置作为 SDK 的数据目录。
        // 该目录目前包含 log, file, image, audio, video, thumb 这6个目录。
        // 如果第三方 APP 需要缓存清理功能， 清理这个目录下面个子目录的内容即可。
        String sdkPath = Environment.getExternalStorageDirectory() + "/" + getPackageName() + "/nim";
        options.sdkStorageRootPath = sdkPath;

        // 配置是否需要预下载附件缩略图，默认为 true
        options.preloadAttach = true;

        // 配置附件缩略图的尺寸大小。表示向服务器请求缩略图文件的大小
        // 该值一般应根据屏幕尺寸来确定， 默认值为 Screen.width / 2
        options.thumbnailSize = 720 / 2;

        // 用户资料提供者, 目前主要用于提供用户资料，用于新消息通知栏中显示消息来源的头像和昵称
        options.userInfoProvider = new UserInfoProvider() {
            @Override
            public UserInfo getUserInfo(String account) {
                return null;
            }

            @Override
            public int getDefaultIconResId() {
                return R.mipmap.default_photo;
            }

            @Override
            public Bitmap getTeamIcon(String tid) {
                return null;
            }

            @Override
            public Bitmap getAvatarForMessageNotifier(String account) {
                return null;
            }

            @Override
            public String getDisplayNameForMessageNotifier(String account, String sessionId,
                                                           SessionTypeEnum sessionType) {
                return null;
            }
        };
        return options;
    }

    // 如果已经存在用户登录信息，返回LoginInfo，否则返回null即可
    private LoginInfo loginInfo() {
        // 从本地读取上次登录成功时保存的用户登录信息
        String account = NimAccountSDK.getUserAccount();
        String token = NimAccountSDK.getUserToken();

        if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(token)) {
            //加载用户信息
            UserCache.setAccount(account);
            return new LoginInfo(account, token);
        } else {
            return null;
        }
    }
    /*================== 网易云信 End ==================*/
}
