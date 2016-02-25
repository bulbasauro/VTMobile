package com.bulbasauro.misc;

import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.bulbasauro.vtmobile.TopicoActivity;

/**
 * Created on 25/02/2016.
 */
public class CustomWebViewClient extends WebViewClient {

    private TopicoActivity activity;
    private WebView webView;
    private RelativeLayout.LayoutParams params;

    public CustomWebViewClient(TopicoActivity activity, WebView webView, RelativeLayout.LayoutParams params) {
        this.activity = activity;
        this.webView = webView;
        this.params = params;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        webView.loadUrl("javascript:MyApp.resize(document.body.getBoundingClientRect().height)");
        webView.setVisibility(View.VISIBLE);
    }


    @JavascriptInterface
    public void resize(final float height) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                webView.setLayoutParams(params);
            }
        });
    }
}
