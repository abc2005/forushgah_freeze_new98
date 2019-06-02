package com.persiandesigners.freeze.Util;

import android.os.CountDownTimer;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

/**
 * Created by Navid on 4/5/2018.
 */

public class CountDown extends CountDownTimer {
    TextView tv;

    public CountDown(long millisInFuture, long countDownInterval, TextView tv) {
        super(millisInFuture, countDownInterval);
        this.tv=tv;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        long ms = millisUntilFinished;
        String text = String.format("%02d\' %02d\"",
                TimeUnit.MILLISECONDS.toMinutes(ms) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(ms)),
                TimeUnit.MILLISECONDS.toSeconds(ms) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(ms)));
        tv.setText(text);
    }

    @Override
    public void onFinish() {
        tv.setText("0");
    }
}
