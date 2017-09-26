package com.lqm.home.imageloader;

import android.net.Uri;
import android.widget.ImageView;

import com.lqm.home.app.App;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * @描述 图片加载管理(对universalimage工具的包装)
 */
public class ImageLoaderManager {
    public static void LoadNetImage( String imgUrl, ImageView imageView) {
              ImageLoader.getInstance()
                .displayImage(imgUrl, imageView, App.options);

//        Glide.with(App.getInstance()).load(imgUrl)
//                .placeholder(R.drawable.ic_default_color)
//                .error(R.drawable.ic_default_color)
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .into(imageView);

    }

    public static void LoadLocalImage(String path, ImageView imageView) {
      ImageLoader.getInstance()
                .displayImage(Uri.parse("file://" + path).toString(), imageView, App.options);

//        Glide.with(App.getInstance()).load(Uri.parse("file://" + path).toString())
//                .placeholder(R.drawable.ic_default_color)//
//                .error(R.drawable.ic_default_color)//
//                .diskCacheStrategy(DiskCacheStrategy.ALL)//
//                .into(imageView);
    }
}