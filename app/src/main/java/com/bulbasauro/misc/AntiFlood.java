package com.bulbasauro.misc;

import android.os.CountDownTimer;
import android.widget.Toast;

import com.bulbasauro.abstracts.AbstractActivity;
import com.bulbasauro.vtmobile.R;

/**
 * Created on 02/02/2016.
 */
public class AntiFlood extends CountDownTimer {

    private AbstractActivity activity;

    /**
     * @param millisInFuture    The number of millis in the future from the call
     *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
     *                          is called.
     * @param countDownInterval The interval along the way to receive
     *                          {@link #onTick(long)} callbacks.
     */
    public AntiFlood(AbstractActivity activity, long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
        this.activity = activity;
    }

    @Override
    public void onTick(long millisUntilFinished) {

    }

    @Override
    public void onFinish() {
        Toast.makeText(activity.getApplicationContext(), activity.getString(R.string.anti_flood_liberado), Toast.LENGTH_SHORT);
        activity.getEditor().putLong(activity.getString(R.string.sp_antiflood), System.nanoTime());
        activity.getEditor().commit();
    }
}
