package com.lqm.home.widget;

/**
 * Created by luqinmao on 2016/9/22.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import static com.lqm.home.widget.NetworkImageView.defaultOptions;

/**
 * 自定义的圆角矩形ImageView
 *
 */
public class RoundRectImageView extends ImageView {

    private final Paint paint;
    private String mUrl;

    public RoundRectImageView(Context context) {
        this(context,null);
    }

    public RoundRectImageView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RoundRectImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        paint  = new Paint();
    }

    /**
     * 绘制圆角矩形图片
     * @author caizhiming
     */
    @Override
    protected void onDraw(Canvas canvas) {

        try {
            Drawable drawable = getDrawable();
            if (null != drawable) {
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                Bitmap b = getRoundBitmap(bitmap, 20);
                final Rect rectSrc = new Rect(0, 0, b.getWidth(), b.getHeight());
                final Rect rectDest = new Rect(0,0,getWidth(),getHeight());
                paint.reset();
                canvas.drawBitmap(b, rectSrc, rectDest, paint);

            } else {
                super.onDraw(canvas);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * 获取圆角矩形图片方法
     * @param bitmap
     * @param roundPx,一般设置成14
     * @return Bitmap
     * @author caizhiming
     */
    private Bitmap getRoundBitmap(Bitmap bitmap, int roundPx) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;

        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        int x = bitmap.getWidth();

        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;


    }

    public void setImageUrl(String url) {
        mUrl = url;
        ImageLoader.getInstance().displayImage(mUrl, this, defaultOptions);
        invalidate();
    }

    public void setImageUrl(String url, DisplayImageOptions options) {
        mUrl = url;
        if (options == null)
            ImageLoader.getInstance().displayImage(mUrl, this, defaultOptions);
        else
            ImageLoader.getInstance().displayImage(mUrl, this, options);
        invalidate();
    }
}  