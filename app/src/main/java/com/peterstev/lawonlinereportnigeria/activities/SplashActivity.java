package com.peterstev.lawonlinereportnigeria.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.peterstev.lawonlinereportnigeria.R;
import com.crashlytics.android.Crashlytics;
import com.peterstev.lawonlinereportnigeria.utils.Utils;

import io.fabric.sdk.android.Fabric;

import static java.lang.Thread.sleep;

/**
 * Created by Peterstev on 08/05/2018.
 * for LawOnlineReport
 */

public class SplashActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        Crashlytics.setUserIdentifier(Utils.getDeviceDetails());

        setContentView(R.layout.activity_splash);

        final int SLEEP = 2500;
        Thread timerThread = new Thread(() -> {
            try {
                sleep(SLEEP);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        timerThread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
