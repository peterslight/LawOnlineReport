package com.peterstev.lawonlinereportnigeria.services.chrome;

import android.content.ComponentName;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsServiceConnection;

/**
 * Created by Peterstev on 23/04/2018.
 */

public class CustomTabsWarmUpService extends CustomTabsServiceConnection {

    private CustomTabsClient tabsClient;
    public static final String CUSTOM_TAB_PACKAGE_NAME = "com.android.chrome";

    @Override
    public void onCustomTabsServiceConnected(ComponentName name, CustomTabsClient client) {
        tabsClient = client;
        tabsClient.warmup(0L);
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {

    }
}
