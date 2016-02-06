package com.bulbasauro.misc;

import android.content.SharedPreferences;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * Created on 31/01/2016.
 */
public class CustomOnItemSelectedListener implements OnItemSelectedListener {

    private SharedPreferences.Editor editor;
    private String spKey;

    public CustomOnItemSelectedListener(SharedPreferences.Editor editor, String spKey) {
        this.editor = editor;
        this.spKey = spKey;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        editor.putInt(spKey, pos);
        editor.commit();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
