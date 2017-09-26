package com.lqm.home.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.lqm.home.R;
import com.lqm.home.utils.DensityUtils;

/**
 * Created by H on 2015/9/16.
 */
public class Topbar extends View {

    /**
     * 控件的宽
     */
    private int mWidth;
    /**
     * 控件的高
     */
    private int mHeight;

    private Context mContext;

    /**
     * 标题bitmap
     */
    private Bitmap titleImage;
    /**
     * 左图bitmap
     */
    private Bitmap leftImage;
    /**
     * 右图bitmap
     */
    private Bitmap rightImage;

    private boolean isShowTitleImg = true;
    private boolean isShowTitleText = true;
    private boolean isShowLeftImg = true;
    private boolean isShowLeftText = true;
    private boolean isShowRightImg = true;
    private boolean isShowRightText = true;
    /**
     * 判断按下开始的位置是否在左
     */
    private boolean leftStartTouchDown = false;
    /**
     * 判断按下开始的位置是否在右
     */
    private boolean rightStartTouchDown = false;
    /**
     * 标题
     */
    private String titleText;
    /**
     * 标题字体大小
     */
    private float titleTextSize;
    /**
     * 标题颜色
     */
    private int titleTextColor = Color.WHITE;
    /**
     * 左边文字
     */
    private String leftText;
    /**
     * 左边文字大小
     */
    private float leftTextSize;
    /**
     * 左边文字颜色
     */
    private int leftTextColor = Color.WHITE;
    /**
     * 右边文字
     */
    private String rightText;
    /**
     * 右边文字大小
     */
    private float rightTextSize;
    /**
     * 右边文字颜色
     */
    private int rightTextColor = Color.WHITE;

    private Paint mPaint;
    /**
     * 对文本的约束
     */
    private Rect mTextBound;
    /**
     * 控制整体布局
     */
    private Rect rect;

    public Topbar(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.mContext = context;

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.Topbar);

        for (int i = 0; i < typedArray.getIndexCount(); i++) {
            int attr = typedArray.getIndex(i);
            switch (attr) {
                case R.styleable.Topbar_title_src:
                    titleImage = BitmapFactory.decodeResource(getResources(), typedArray.getResourceId(attr, 0));
                    break;
                case R.styleable.Topbar_title_text:
                    titleText = typedArray.getString(attr);
                    break;
                case R.styleable.Topbar_title_text_size:
                    titleTextSize = typedArray.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                            16, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.Topbar_title_text_color:
                    titleTextColor = typedArray.getColor(attr, Color.WHITE);
                    break;
                case R.styleable.Topbar_left_src:
                    leftImage = BitmapFactory.decodeResource(getResources(), typedArray.getResourceId(attr, 0));
                    break;
                case R.styleable.Topbar_left_text:
                    leftText = typedArray.getString(attr);
                    break;
                case R.styleable.Topbar_left_text_size:
                    leftTextSize = typedArray.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                            16, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.Topbar_left_text_color:
                    leftTextColor = typedArray.getColor(attr, Color.WHITE);
                    break;
                case R.styleable.Topbar_right_src:
                    rightImage = BitmapFactory.decodeResource(getResources(), typedArray.getResourceId(attr, 0));
                    break;
                case R.styleable.Topbar_right_text:
                    rightText = typedArray.getString(attr);
                    break;
                case R.styleable.Topbar_right_text_size:
                    rightTextSize = typedArray.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                            16, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.Topbar_right_text_color:
                    rightTextColor = typedArray.getColor(attr, Color.WHITE);
                    break;
            }
        }
        typedArray.recycle();    //回收typeArray

        rect = new Rect();
        mPaint = new Paint();
        mTextBound = new Rect();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mWidth = getWidth();
        mHeight = getHeight();

        //抗锯齿处理
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

        rect.left = getPaddingLeft();
        rect.right = mWidth - getPaddingRight();
        rect.top = getPaddingTop();
        rect.bottom = mHeight - getPaddingBottom();

        //抗锯齿
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(titleTextSize);
        mPaint.setColor(titleTextColor);
        mPaint.setStyle(Paint.Style.FILL);
        //文字水平居中
        mPaint.setTextAlign(Paint.Align.CENTER);

        if (titleText != null && titleText.length() > 0) {//计算垂直居中baseline
            // 计算了描绘字体需要的范围
            mPaint.getTextBounds(titleText, 0, titleText.length(), mTextBound);
            Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
            int baseLine = (int) ((rect.bottom + rect.top - fontMetrics.bottom - fontMetrics.top) / 2);
            // 正常情况，将字体居中
            canvas.drawText(titleText, rect.centerX(), baseLine, mPaint);
        }
        if (titleImage != null && isShowTitleImg) {
            rect.top = mHeight / 3;
            rect.bottom = mHeight * 2 / 3;
            rect.left = mWidth / 2 - (rect.bottom - rect.top) / 2 * titleImage.getWidth() / titleImage.getHeight();
            rect.right = mWidth / 2 + (rect.bottom - rect.top) / 2 * titleImage.getWidth() / titleImage.getHeight();
            canvas.drawBitmap(titleImage, null, rect, mPaint);
        }
        if (leftImage != null && isShowLeftImg) {
            // 计算左图范围
            rect.top = mHeight * 2/7;
            rect.bottom = mHeight * 5/7;
            rect.left = mWidth * 2 / 32;
            //根据图片比例设置宽度
            rect.right = rect.left + (rect.bottom - rect.top) * leftImage.getWidth() / leftImage.getHeight();
            canvas.drawBitmap(leftImage, null, rect, mPaint);
        }
        if (rightImage != null && isShowRightImg) {
            // 计算右图范围
            rect.top = mHeight * 2/7;
            rect.bottom = mHeight * 5 / 7;
            rect.right = mWidth * 30 / 32;
            //根据图片比例设置宽度
            rect.left = rect.right - (rect.bottom - rect.top) * rightImage.getWidth() / rightImage.getHeight();
            canvas.drawBitmap(rightImage, null, rect, mPaint);
        }
        if (leftText != null && leftText.length() > 0 && isShowLeftText) {
            mPaint.setTextSize(leftTextSize);
            mPaint.setColor(leftTextColor);
            int w = mWidth * 2 / 32;
            if (leftImage != null) {
                w = mWidth * 3 / 32 + leftImage.getWidth();
            }
            mPaint.setTextAlign(Paint.Align.LEFT);
            // 计算了描绘字体需要的范围
            mPaint.getTextBounds(leftText, 0, leftText.length(), mTextBound);
            Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
            int baseLine = (int) ((rect.bottom + rect.top - fontMetrics.bottom - fontMetrics.top) / 2);
            canvas.drawText(leftText, w, baseLine, mPaint);
        }
        if (rightText != null && rightText.length() > 0 && isShowRightText) {
            mPaint.setTextSize(rightTextSize);
            mPaint.setColor(rightTextColor);
            // 计算了描绘字体需要的范围
            mPaint.getTextBounds(rightText, 0, rightText.length(), mTextBound);
            Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
            int baseLine = (int) ((rect.bottom + rect.top - fontMetrics.bottom - fontMetrics.top) / 2);
            canvas.drawText(rightText, mWidth * 29 / 32, baseLine, mPaint);
        }

        // 取消使用掉的快
        rect.bottom -= mTextBound.height();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                int _x = (int) event.getX();
                if (_x < mWidth / 7) {
                    leftStartTouchDown = true;
                } else if (_x > mWidth * 6 / 7) {
                    rightStartTouchDown = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                int x = (int) event.getX();
                if (leftStartTouchDown && x < mWidth / 7 && listener != null) {
                    listener.leftOnClick();
                } else if (rightStartTouchDown && x > mWidth * 6 / 7 && listener != null) {
                    listener.rightOnClick();
                }
                leftStartTouchDown = false;
                rightStartTouchDown = false;
                break;
        }
        return true;
    }

    public void setTitleText(String text) {
        titleText = text;
        invalidate();
    }

    public void setTitleText(int stringId) {
        titleText = mContext.getString(stringId);
        invalidate();
    }

    public void setTitleColor(int color) {
        titleTextColor = color;
        invalidate();
    }

    public void setTitleSize(int sp) {
        titleTextSize = com.lqm.home.utils.DensityUtils.sp2px(mContext, sp);
        invalidate();
    }

    public void setLeftText(String text) {
        leftText = text;
        invalidate();
    }

    public void setLeftText(int stringId) {
        leftText = mContext.getString(stringId);
        invalidate();
    }

    public void setLeftTextColor(int color) {
        leftTextColor = color;
        invalidate();
    }

    public void setLeftImage(Bitmap bitmap) {
        leftImage = bitmap;
        invalidate();
    }

    public void setRightImage(Bitmap bitmap) {
        rightImage = bitmap;
        invalidate();
    }

    public void setLeftTextSize(int sp) {
        leftTextSize = DensityUtils.sp2px(mContext, sp);
        invalidate();
    }

    public void setRightText(String text) {
        rightText = text;
        invalidate();
    }

    public void setRightText(int stringId) {
        rightText = mContext.getString(stringId);
        invalidate();
    }

    public void setRightTextColor(int color) {
        rightTextColor = color;
        invalidate();
    }

    public void setRightTextSize(int sp) {
        leftTextSize = DensityUtils.sp2px(mContext, sp);
        invalidate();
    }

    public void showLeftImg(boolean flag) {
        isShowLeftImg = flag;
        invalidate();
    }

    public void showLeftText(boolean flag) {
        isShowLeftText = flag;
        invalidate();
    }

    public void showRightImg(boolean flag) {
        isShowRightImg = flag;
        invalidate();
    }

    public void showRightText(boolean flag) {
        isShowRightText = flag;
        invalidate();
    }

    private TopbarOnClickListener listener;

    public interface TopbarOnClickListener {
        void leftOnClick();

        void rightOnClick();
    }

    public void setTopbarOnClickListener(TopbarOnClickListener listener) {
        this.listener = listener;
    }

    public String getTitleText() {
        return titleText;
    }

    public String getLeftText() {
        return leftText;
    }

    public String getRightText() {
        return rightText;
    }

}
