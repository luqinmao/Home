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

/**
 * CSDN_LQR
 * 自定义的顶部标题
 */
public class OptionItemView extends View {

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
     * 左图bitmap
     */
    private Bitmap leftImage;
    /**
     * 右图bitmap
     */
    private Bitmap rightImage;

    private boolean isShowLeftImg = true;
    private boolean isShowLeftText = true;
    private boolean isShowRightImg = true;
    private boolean isShowRightText = true;

    //拆分模式(默认是false，也就是一个整体)
    private boolean mSpliteMode = false;
    /**
     * 判断按下开始的位置是否在左
     */
    private boolean leftStartTouchDown = false;
    /**
     * 判断按下开始的位置是否在中间
     */
    private boolean centerStartTouchDown = false;
    /**
     * 判断按下开始的位置是否在右
     */
    private boolean rightStartTouchDown = false;
    /**
     * 标题
     */
    private String title = "";
    /**
     * 标题字体大小
     */
    private float titleTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
            16, getResources().getDisplayMetrics());
    /**
     * 标题颜色
     */
    private int titleTextColor = Color.BLACK;
    /**
     * 左边文字
     */
    private String leftText = "";
    /**
     * 左边文字大小
     */
    private float leftTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
            16, getResources().getDisplayMetrics());
    /**
     * 左字左边距
     */
    private int leftTextMarginLeft = -1;
    /**
     * 左图左边距
     */
    private int leftImageMarginLeft = -1;
    /**
     * 左图右边距
     */
    private int leftImageMarginRight = -1;
    /**
     * 左边文字颜色
     */
    private int leftTextColor = Color.BLACK;
    /**
     * 右边文字
     */
    private String rightText = "";
    /**
     * 右边文字大小
     */
    private float rightTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
            16, getResources().getDisplayMetrics());
    /**
     * 右边文字颜色
     */
    private int rightTextColor = Color.BLACK;
    /**
     * 右字右边距
     */
    private int rightTextMarginRight = -1;
    /**
     * 右图左边距
     */
    private int rightImageMarginLeft = -1;
    /**
     * 右图右边距
     */
    private int rightImageMarginRight = -1;

    private Paint mPaint;
    /**
     * 对文本的约束
     */
    private Rect mTextBound;
    /**
     * 控制整体布局
     */
    private Rect rect;

    public OptionItemView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.mContext = context;

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.OptionItemView);

        for (int i = 0; i < typedArray.getIndexCount(); i++) {
            int attr = typedArray.getIndex(i);
            if (attr == R.styleable.OptionItemView_left_src) {
                leftImage = BitmapFactory.decodeResource(getResources(), typedArray.getResourceId(attr, 0));

            } else if (attr == R.styleable.OptionItemView_right_src) {
                rightImage = BitmapFactory.decodeResource(getResources(), typedArray.getResourceId(attr, 0));

            } else if (attr == R.styleable.OptionItemView_title_size) {
                titleTextSize = typedArray.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                        16, getResources().getDisplayMetrics()));

            } else if (attr == R.styleable.OptionItemView_title_color) {
                titleTextColor = typedArray.getColor(attr, Color.BLACK);

            } else if (attr == R.styleable.OptionItemView_title) {
                title = typedArray.getString(attr);

            } else if (attr == R.styleable.OptionItemView_left_text) {
                leftText = typedArray.getString(attr);

            } else if (attr == R.styleable.OptionItemView_left_text_size) {
                leftTextSize = typedArray.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                        16, getResources().getDisplayMetrics()));

            } else if (attr == R.styleable.OptionItemView_left_text_margin_left) {
                leftTextMarginLeft = typedArray.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        -1, getResources().getDisplayMetrics()));
            } else if (attr == R.styleable.OptionItemView_left_image_margin_left) {
                leftImageMarginLeft = typedArray.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        -1, getResources().getDisplayMetrics()));
            } else if (attr == R.styleable.OptionItemView_left_image_margin_right) {
                leftImageMarginRight = typedArray.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        -1, getResources().getDisplayMetrics()));
            } else if (attr == R.styleable.OptionItemView_left_text_color) {
                leftTextColor = typedArray.getColor(attr, Color.BLACK);

            } else if (attr == R.styleable.OptionItemView_right_text) {
                rightText = typedArray.getString(attr);
            } else if (attr == R.styleable.OptionItemView_right_text_size) {
                rightTextSize = typedArray.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                        16, getResources().getDisplayMetrics()));
            } else if (attr == R.styleable.OptionItemView_right_text_margin_right) {
                rightTextMarginRight = typedArray.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        -1, getResources().getDisplayMetrics()));
            } else if (attr == R.styleable.OptionItemView_right_image_margin_left) {
                rightImageMarginLeft = typedArray.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        -1, getResources().getDisplayMetrics()));
            } else if (attr == R.styleable.OptionItemView_right_image_margin_right) {
                rightImageMarginRight = typedArray.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        -1, getResources().getDisplayMetrics()));
            } else if (attr == R.styleable.OptionItemView_right_text_color) {
                rightTextColor = typedArray.getColor(attr, Color.BLACK);
            } else if (attr == R.styleable.OptionItemView_splite_mode) {
                mSpliteMode = typedArray.getBoolean(attr, false);
            }
        }
        typedArray.recycle();    //回收typeArray

        rect = new Rect();
        mPaint = new Paint();
        mTextBound = new Rect();
        // 计算了描绘字体需要的范围
        mPaint.getTextBounds(title, 0, title.length(), mTextBound);
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
        mPaint.setTextSize(titleTextSize > leftTextSize ? titleTextSize > rightTextSize ? titleTextSize : rightTextSize : leftTextSize > rightTextSize ? leftTextSize : rightTextSize);
//        mPaint.setTextSize(titleTextSize);
        mPaint.setStyle(Paint.Style.FILL);
        //文字水平居中
        mPaint.setTextAlign(Paint.Align.CENTER);

        //计算垂直居中baseline
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        int baseLine = (int) ((rect.bottom + rect.top - fontMetrics.bottom - fontMetrics.top) / 2);

        if (!title.trim().equals("")) {
            // 正常情况，将字体居中
            mPaint.setColor(titleTextColor);
            canvas.drawText(title, rect.centerX(), baseLine, mPaint);
            // 取消使用掉的快
            rect.bottom -= mTextBound.height();
        }


        if (leftImage != null && isShowLeftImg) {
            // 计算左图范围
            rect.left = leftImageMarginLeft >= 0 ? leftImageMarginLeft : mWidth / 32;
            rect.right = rect.left + mHeight * 1 / 2;
            rect.top = mHeight / 4;
            rect.bottom = mHeight * 3 / 4;
            canvas.drawBitmap(leftImage, null, rect, mPaint);
        }
        if (rightImage != null && isShowRightImg) {
            // 计算右图范围
            rect.right = mWidth - (rightImageMarginRight >= 0 ? rightImageMarginRight : mWidth / 32);
            rect.left = rect.right - mHeight * 1 / 2;
            rect.top = mHeight / 4;
            rect.bottom = mHeight * 3 / 4;
            canvas.drawBitmap(rightImage, null, rect, mPaint);
        }
        if (leftText != null && !leftText.equals("") && isShowLeftText) {
            mPaint.setTextSize(leftTextSize);
            mPaint.setColor(leftTextColor);
            int w = 0;
            if (leftImage != null) {
                w += leftImageMarginLeft >= 0 ? leftImageMarginLeft : (mHeight / 8);//增加左图左间距
                w += mHeight * 1 / 2;//图宽
                w += leftImageMarginRight >= 0 ? leftImageMarginRight : (mWidth / 32);// 增加左图右间距
                w += leftTextMarginLeft > 0 ? leftTextMarginLeft : 0;//增加左字左间距
            } else {
                w += leftTextMarginLeft >= 0 ? leftTextMarginLeft : (mWidth / 32);//增加左字左间距
            }

            mPaint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText(leftText, w, baseLine, mPaint);
        }
        if (rightText != null && !rightText.equals("") && isShowRightText) {
            mPaint.setTextSize(rightTextSize);
            mPaint.setColor(rightTextColor);

            int w = mWidth;
            if (rightImage != null) {
                w -= rightImageMarginRight >= 0 ? rightImageMarginRight : (mHeight / 8);//增加右图右间距
                w -= mHeight * 1 / 2;//增加图宽
                w -= rightImageMarginLeft >= 0 ? rightImageMarginLeft : (mWidth / 32);//增加右图左间距
                w -= rightTextMarginRight > 0 ? rightTextMarginRight : 0;//增加右字右间距
            } else {
                w -= rightTextMarginRight >= 0 ? rightTextMarginRight : (mWidth / 32);//增加右字右间距
            }

            // 计算了描绘字体需要的范围
            mPaint.getTextBounds(rightText, 0, rightText.length(), mTextBound);
            canvas.drawText(rightText, w - mTextBound.width(), baseLine, mPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //是一个整体，则不拆分各区域的点击
        if (!mSpliteMode)
            return super.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                int _x = (int) event.getX();
                if (_x < mWidth / 8) {
                    leftStartTouchDown = true;
                } else if (_x > mWidth * 7 / 8) {
                    rightStartTouchDown = true;
                } else {
                    centerStartTouchDown = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                int x = (int) event.getX();
                if (leftStartTouchDown && x < mWidth / 8 && listener != null) {
                    listener.leftOnClick();
                } else if (rightStartTouchDown && x > mWidth * 7 / 8 && listener != null) {
                    listener.rightOnClick();
                } else if (centerStartTouchDown && listener != null) {
                    listener.centerOnClick();
                }
                leftStartTouchDown = false;
                centerStartTouchDown = false;
                rightStartTouchDown = false;
                break;
        }
        return true;
    }

    public void setTitleText(String text) {
        title = text;
        invalidate();
    }

    public void setTitleText(int stringId) {
        title = mContext.getString(stringId);
        invalidate();
    }

    public void setTitleColor(int color) {
        titleTextColor = color;
        invalidate();
    }

    public void setTitleSize(int sp) {
        titleTextSize = sp2px(mContext, sp);
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

    public void setLeftImageMarginRight(int dp) {
        leftImageMarginRight = dp2px(mContext, dp);
        invalidate();
    }

    public void setLeftImageMarginLeft(int dp) {
        this.leftImageMarginLeft = dp2px(mContext, dp);
        invalidate();
    }

    public void setLeftTextMarginLeft(int dp) {
        this.leftTextMarginLeft = dp2px(mContext, dp);
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
        leftTextSize = sp2px(mContext, sp);
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
        leftTextSize = sp2px(mContext, sp);
        invalidate();
    }

    public void setRightImageMarginLeft(int dp) {
        rightImageMarginLeft = dp2px(mContext, dp);
        invalidate();
    }

    public void setRightImageMarginRight(int dp) {
        this.rightImageMarginRight = dp2px(mContext, dp);
        invalidate();
    }

    public void setRightTextMarginRight(int dp) {
        this.rightTextMarginRight = dp2px(mContext, dp);
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

    public void setSpliteMode(boolean spliteMode) {
        mSpliteMode = spliteMode;
    }

    public boolean getSpliteMode() {
        return mSpliteMode;
    }

    private OnOptionItemClickListener listener;

    public interface OnOptionItemClickListener {
        void leftOnClick();

        void centerOnClick();

        void rightOnClick();
    }

    public void setOnOptionItemClickListener(OnOptionItemClickListener listener) {
        this.listener = listener;
    }

    private int sp2px(Context context, float spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, context.getResources().getDisplayMetrics());
    }

    private int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }
}
