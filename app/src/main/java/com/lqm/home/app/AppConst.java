package com.lqm.home.app;

import com.lqm.home.utilslqr.LogUtils;


public class AppConst {

//    public static final String SERVER_ADDRESS = "http://10.0.2.2:8080"; //模拟器访问本地服务器地址
//    public static final String SERVER_ADDRESS_IMG = "http://10.0.2.2:8080";
//	public static final String SERVER_ADDRESS = "http://192.168.191.1:8080"; //手机访问本地服务器地址
//    public static final String SERVER_ADDRESS_IMG = "http://192.168.191.1:8080";
    public static final String SERVER_ADDRESS = "http://119.29.224.39:8080/HomeServerDemo2"; //外网
    public static final String SERVER_ADDRESS_IMG = "http://119.29.224.39:8080";

    //版本升级
//    public static final String UPDATE_ANDROID = SERVER_ADDRESS + "/feedback/updataApk";
    public static final String UPDATE_ANDROID = SERVER_ADDRESS + "/updateapp/updataApk";

    //网易新闻资讯
    public static final String WANGYINEWS = "http://wangyi.butterfly.mopaasapp.com/news/api";
//    public static final String JUHENEWS = "http://v.juhe.cn/toutiao/index";
//    public static final String JUHEAPIKEY = "d050ce77241cf978f252dbd4db9ba00f";


    //百度分类图片
    public static final String BAIDU_IMG_SORT = "http://image.baidu.com/data/imgs";
     //param:col=美女&tag=小清新&sort=0&pn=3&rn=10&p=channel&from=1  //pn=开始条数&rn=显示数量
    //百度搜索图片
    public static final String BAIDU_IMG_SEARCH = "http://image.baidu.com/search/index";
    //param:tn=resultjson&ie=utf-8&word=周杰伦&pn=1&rn=10


    //天行数据Key
    public static final String TIANXINKEY = "d54d8c95b85b8c4fe651976a731538e0";
    //微信文章
    public static final String TIANXIN_WEIXIN_ARTICLE = "https://api.tianapi.com/wxnew";
    //微信公众号文章
    public static final String TIANXIN_WEIXIN_HOME = "https://api.tianapi.com/weixin/home";
    //params:src=lufengdatang&num=10&page=1
    //笑话
    public static final String TIANXIN_FUNNY = "https://api.tianapi.com/txapi/joke";
    //微信公众号列表
    public static final String PUBLIC_LIST = SERVER_ADDRESS + "/public/getPublics";
    //天行数据新闻
    public static final String TIANXIN_NEWS = "https://api.tianapi.com/";


    //Mob短信验证码
    public static final String MOBAPPKEY = "1995071e72fef";
    public static final String MOBAPPSECRET = "4bda9c959292cca1d96f6f6099c07b8e";

    //轮播广告
    public static final String GETAD = SERVER_ADDRESS + "/ad/getAds";

    public static final class User {
        public static final String LOGIN = SERVER_ADDRESS + "/user/login";
        public static final String REGISTER = SERVER_ADDRESS + "/user/register";
        public static final String REPLACE_VILLAGE = SERVER_ADDRESS + "/user/replaceVillage";
    }

    public static final String FEEDBACK = SERVER_ADDRESS + "/feedback/createFeedBack";

    public static final class Post {
        public static final String CREATE_POST = SERVER_ADDRESS + "/post/createPost";
        public static final String GET_POSTS = SERVER_ADDRESS + "/post/getPosts";
        public static final String GET_POST_INFO = SERVER_ADDRESS + "/post/getPostById";

    }

    public static final class Comment {
        public static final String CREATE_COMMENT = SERVER_ADDRESS + "/comment/createComment";
        public static final String GET_COMMENTS = SERVER_ADDRESS + "/comment/getComments";

    }


    public static final class Village {
        public static final String CREATE_VILLAGE = SERVER_ADDRESS + "/village/createVillage";
        public static final String VILLAGE_INFO = SERVER_ADDRESS + "/village/getVillageInfo";
        public static final String GET_VILLAGES_BY_DISTRICT = SERVER_ADDRESS + "/village/getVillages";
        public static final String GET_VILLAGES_BY_NAME = SERVER_ADDRESS + "/village/searchVillageByName";

    }

    //图片上传
    public static final String UPLOAD_IMG = SERVER_ADDRESS + "/post/upload/img";

    /**
     * SCDN_SQR 中添加
     */
    public static final String TAG = "CSDN_LQR";
    public static final int DEBUGLEVEL = LogUtils.LEVEL_ALL;//日志输出级别

    public static final class Account {
        public static final String KEY_USER_ACCOUNT = "account";
        public static final String KEY_USER_TOKEN = "token";
    }

    //二维码扫码指令前缀
    public static final class QRCodeCommend {
        public static final String ACCOUNT = "account:";
        public static final String TEAMID = "teamId:";
    }


    //用户拓展信息字段
    public static final class UserInfoExt {
        public static final String AREA = "area";
        public static final String PHONE = "phone";
    }

    //我的群成员信息拓展字段
    public static final class MyTeamMemberExt {
        public static final String SHOULD_SHOW_NICK_NAME = "shouldShowNickName";
    }
}
