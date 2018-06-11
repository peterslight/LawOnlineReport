package com.peterstev.lawonlinereportnigeria.services.chrome;

import android.content.ComponentName;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsServiceConnection;

import java.lang.ref.WeakReference;

/**
 * Created by Peterstev on 23/04/2018.
 */

public class ChromeServiceConnection extends CustomTabsServiceConnection {
    //a weak reference to the service connection callback to avoid leaking it
    private WeakReference<ServiceConnectionCallback> connectionCallback;

    public ChromeServiceConnection(ServiceConnectionCallback serviceConnectionCallback) {
        connectionCallback = new WeakReference<ServiceConnectionCallback>(serviceConnectionCallback);
    }

    @Override
    public void onCustomTabsServiceConnected(ComponentName name, CustomTabsClient client) {
        ServiceConnectionCallback callback = connectionCallback.get();
        if (callback != null) callback.onServiceConnected(client);
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        ServiceConnectionCallback callback = connectionCallback.get();
        if (callback != null) callback.onServiceDisconnected();
    }
}
