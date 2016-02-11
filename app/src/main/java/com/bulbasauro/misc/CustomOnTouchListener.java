package com.bulbasauro.misc;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.bulbasauro.abstracts.AbstractActivity;
import com.bulbasauro.vtmobile.MainActivity;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created on 01/02/2016.
 */
public class CustomOnTouchListener implements View.OnTouchListener {

    private GestureDetector gestureDetector;

    private HashMap<String, String> resultp = new HashMap<String, String>();

    public CustomOnTouchListener(AbstractActivity activity, ArrayList<HashMap<String, String>> data, int position) {
        resultp = data.get(position);
        gestureDetector = new GestureDetector(activity, new CustomGestureListener(activity, resultp));
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }
}
