package com.bulbasauro.misc;

import android.content.Intent;
import android.webkit.JavascriptInterface;

import com.bulbasauro.vtmobile.TopicoActivity;

/**
 * Created on 03/02/2016.
 */
public class JavaScript {

    private TopicoActivity activity;

    public JavaScript(TopicoActivity activity) {
        this.activity = activity;
    }

    @JavascriptInterface
    public void abrirTopico(String enderecoURL) {
        Intent intent = new Intent(activity, TopicoActivity.class);
        intent.putExtra("topicoURL", "");
        activity.startActivity(intent);
    }
}
