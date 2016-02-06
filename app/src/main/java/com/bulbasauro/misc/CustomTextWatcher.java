package com.bulbasauro.misc;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

/**
 * Created on 06/02/2016.
 */
public class CustomTextWatcher implements TextWatcher {

    private TextView textView;

    private int limite;

    public CustomTextWatcher(TextView textView, int limite) {
        this.textView = textView;
        this.limite = limite;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        int charLeft = limite - s.length();
        textView.setText("(" + Integer.toString(charLeft) + ")");
    }
}
