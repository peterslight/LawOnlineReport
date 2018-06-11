package com.peterstev.lawonlinereportnigeria.services.chrome;

/**
 * Created by Peterstev on 23/04/2018.
 */

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Empty service used by the custom tab to bind to, raising the application's importance.
 */
public class KeepAliveService extends Service {
    private static final Binder sBinder = new Binder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return sBinder;
    }
}
