package com.bulbasauro.misc;

import android.content.Intent;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.bulbasauro.vtmobile.TopicoActivity;

/**
 * Created on 03/02/2016.
 */
public class JavaScript {

    private TopicoActivity activity;
    private WebView webView;

    public JavaScript(TopicoActivity activity, WebView webView) {
        this.activity = activity;
        this.webView = webView;
    }

    @JavascriptInterface
    public void abrirTopico(String enderecoURL) {
        Intent intent = new Intent(activity, TopicoActivity.class);
        intent.putExtra("topicoURL", "");
        activity.startActivity(intent);
    }
}
