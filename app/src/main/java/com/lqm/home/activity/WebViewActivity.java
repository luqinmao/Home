package com.lqm.home.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lqm.home.R;

import butterknife.Bind;

/**
 * 新闻详情页,疑问页面等WebViewActivity
 */

public class WebViewActivity extends BaseActivity {

    public final static String URL = "url";
    public final static String TITLE = "title";
    public final static String FROM = "from";

    @Bind(R.id.iv_back)
    ImageView iv_back;
    @Bind(R.id.tv_title)
    TextView tv_title;
    @Bind(R.id.pb) ProgressBar pb;
    @Bind(R.id.webView) WebView webView;
    private String mFrom;

    public static void runActivity(Context context, String title, String url,String from) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(URL, url);
        intent.putExtra(TITLE, title);
        intent.putExtra(FROM,from);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        String url = getIntent().getStringExtra(URL);
        String title = getIntent().getStringExtra(TITLE);
        mFrom = getIntent().getStringExtra(FROM);
        tv_title.setText(title);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        pb.setMax(100);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                pb.setProgress(newProgress);
                if (newProgress >= 100) {
                    pb.setVisibility(View.GONE);
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            if (webView.canGoBack()) {
                if (mFrom.equals("news")){ ////新闻界面按返回直接finish
                    finish();
                }else {
                    webView.goBack();
                    return true;
                }
            } else {
                finish();//退出程序
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}
