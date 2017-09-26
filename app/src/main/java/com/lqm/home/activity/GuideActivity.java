package com.lqm.home.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.lqm.home.R;
import com.lqm.home.utils.DensityUtils;
import com.lqm.home.utils.PrefUtils;

import java.util.ArrayList;


/**
 * 新手引导
 */
public class GuideActivity extends Activity {

    /**
     * viewPager
     */
    private ViewPager vp_guide;
    /**
     * 开始体验按钮
     */
    private Button btn_start;
    /**
     * 小圆点组
     */
    private LinearLayout ll_point_group;
    /**
     * 小红点
     */
    private View view_red_point;
    /**
     * 圆点间的距离
     */
    private int mPointWidth;
    private ArrayList<ImageView> mImageViewList; // 用于存放viewpager的图片

    private static final int[] mImageIds = new int[]{R.mipmap.guide_1,
            R.mipmap.guide_2, R.mipmap.guide_3,R.mipmap.guide_4};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //无bar
        setContentView(R.layout.activity_guide);



        //状态栏
        View decorView = getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(option);

        vp_guide = (ViewPager) findViewById(R.id.vp_guide);
        btn_start = (Button) findViewById(R.id.btn_start);
        ll_point_group = (LinearLayout) findViewById(R.id.ll_point_group);
        view_red_point = findViewById(R.id.view_red_point);

        initView();  //初始化界面
        vp_guide.setAdapter(new GuideAdapter());
        vp_guide.setOnPageChangeListener(new GuidePageListener());

        /**
         * 开始体验的按钮监听
         */
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 更新sp, 表示已经展示了新手引导
                PrefUtils.setBoolean(GuideActivity.this, "is_user_guide_showed", true);
                // 跳转主页面
                startActivity(new Intent(GuideActivity.this, LoginOrRegistActivity.class));
                finish();
            }
        });

    }


    /**
     * 初始化界面
     */
    private void initView() {
        //初始化引导页的3个界面
        mImageViewList = new ArrayList<ImageView>();
        for (int i = 0; i < mImageIds.length; i++) {
            ImageView image = new ImageView(this);
            image.setBackgroundResource(mImageIds[i]);
            mImageViewList.add(image);
        }

        //初始化引导页的小圆点
        for (int i = 0; i < mImageIds.length; i++) {
            View point = new View(this);
            point.setBackgroundResource(R.drawable.shape_point_gray); // 设置引导页默认圆点
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    DensityUtils.dp2px(getApplicationContext(), 10),
                    DensityUtils.dp2px(getApplicationContext(), 10)); //圆点宽高，将单位px转为dp
            if (i > 0) {
                params.leftMargin = 10;  //设置圆点间隔
            }

            point.setLayoutParams(params);// 设置圆点的大小
            ll_point_group.addView(point);// 将圆点添加给线性布局

        }

        /**
         * 获取视图树，对layout()的结束事件进行监听
         */
        ll_point_group.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

            /**
             * 当layout执行结束后会回调此方法，
             */
            @Override
            public void onGlobalLayout() {
                //获取圆点之间的距离
                ll_point_group.getViewTreeObserver()
                        .removeGlobalOnLayoutListener(this);
                mPointWidth = ll_point_group.getChildAt(1).getLeft()
                        - ll_point_group.getChildAt(0).getLeft();
                System.out.println("圆点距离:" + mPointWidth);
            }
        });

    }

    /**
     * ViewPager的适配器
     * 实现四个方法
     */
    class GuideAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mImageIds.length;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mImageViewList.get(position));
            return mImageViewList.get(position);
        }


    }

    /**
     * viewpager的滑动监听
     */
    class GuidePageListener implements OnPageChangeListener {

        /**
         * 滑动状态发生变化
         */
        @Override
        public void onPageScrollStateChanged(int arg0) {

        }

        /**
         * 滑动
         */
        @Override
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {

            //红点距离变化
            int len = (int) ((mPointWidth * positionOffset) + (position * mPointWidth));
            //获取当前红点的布局参数
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view_red_point.getLayoutParams();
            params.leftMargin = len; //设置左边距
            view_red_point.setLayoutParams(params); //重新给小红点设置布局参数

        }

        /**
         * 某个页面被选中
         */
        @Override
        public void onPageSelected(int position) {
            if (position == mImageIds.length - 1) {
                btn_start.setVisibility(View.VISIBLE);
            } else {
                btn_start.setVisibility(View.INVISIBLE);
            }

        }
    }
}
