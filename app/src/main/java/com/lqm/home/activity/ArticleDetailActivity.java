package com.lqm.home.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.lqm.home.R;

import butterknife.Bind;

/**
 * 微信精选文章详情页
 */

public class ArticleDetailActivity extends BaseActivity {


    @Bind(R.id.iv_top)
    ImageView ivTop;
    @Bind(R.id.toolbar_article)
    Toolbar toolbarArticle;
    @Bind(R.id.webview)
    WebView webView;
    @Bind(R.id.progress_article)
    ProgressBar progress;

    public static void runActivity(Context context,String url, String title, String imgUrl) {
        Intent intent = new Intent(context, ArticleDetailActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("title", title);
        intent.putExtra("imgUrl", imgUrl);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);
        initView();

        String url = getIntent().getStringExtra("url");
        String imgUrl = getIntent().getStringExtra("imgUrl");

        Glide.with(this).load(imgUrl).into(ivTop);
        progress.setMax(100);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progress.setProgress(newProgress);
                if (newProgress >= 100) {
                    progress.setVisibility(View.GONE);
                }
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        webView.loadUrl(url);
    }

    private void initView(){
        toolbarArticle.setTitle("微信精选文章");
        toolbarArticle.setTitleTextColor(Color.WHITE);
        toolbarArticle.setNavigationIcon(R.mipmap.ic_back);
        setSupportActionBar(toolbarArticle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarArticle.setNavigationIcon(R.mipmap.ic_back);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            if (webView.canGoBack()) {
                webView.goBack();
                return true;
            } else {
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
