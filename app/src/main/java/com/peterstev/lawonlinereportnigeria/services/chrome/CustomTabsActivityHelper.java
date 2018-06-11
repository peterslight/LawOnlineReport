package com.peterstev.lawonlinereportnigeria.services.chrome;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsServiceConnection;
import android.support.customtabs.CustomTabsSession;

import java.util.List;

/**
 * Created by Peterstev on 23/04/2018.
 */

public class CustomTabsActivityHelper implements ServiceConnectionCallback {

    private CustomTabsSession customTabsSession;
    private CustomTabsClient customTabsClient;
    private CustomTabsServiceConnection serviceConnection;
    private CustomTabsConnectionCallback connectionCallback;


    //interface to handle cases where cct cannot be opened
    public interface CustomTabFallback {
        void openUri(Activity activity, Uri uri);
    }

    public interface CustomTabsConnectionCallback {
        void onCustomTabConnected();

        void onCustomTabDisconnected();
    }

    /**
     * Utility method for opening a custom tab
     *
     * @param activity         Host activity
     * @param customTabsIntent custom tabs intent
     * @param uri              uri to open
     * @param fallback         fallback to handle case where custom tab cannot be opened
     */
    public static void openCustomTab(Activity activity, CustomTabsIntent customTabsIntent, Uri uri, CustomTabFallback fallback) {
        String packageName = CustomTabsHelper.getPackageNameToUse(activity);

        if (packageName == null) {
            //thus no chromium browser.
            //trigger fallback

            if (fallback != null) {
                fallback.openUri(activity, uri);
            }
        } else {
            //set package name to use
            customTabsIntent.intent.setPackage(packageName);
            customTabsIntent.launchUrl(activity, uri);
        }
    }

    /**
     * Binds the activity to the custom tabs service.
     *
     * @param activity activity to be "bound" to the service
     */
    public void bindCustomTabsService(Activity activity) {
        if (customTabsClient != null) return;

        String packageName = CustomTabsHelper.getPackageNameToUse(activity);
        if (packageName == null) return;

        serviceConnection = new ChromeServiceConnection(this);
        CustomTabsClient.bindCustomTabsService(activity, packageName, serviceConnection);
    }

    /**
     * Unbinds the activity from the custom tabs service
     *
     * @param activity
     */
    public void unBindCustomTabsService(Activity activity) {
        if (serviceConnection != null) {
            activity.unbindService(serviceConnection);
            customTabsClient = null;
            customTabsSession = null;
            serviceConnection = null;
        }
    }

    /**
     * Creates or retrieves an exiting CustomTabsSession.
     *
     * @return a CustomTabsSession.
     */
    public CustomTabsSession getSession() {
        if (customTabsClient == null) {
            customTabsSession = null;
        } else if (customTabsSession == null) {
            customTabsSession = customTabsClient.newSession(null);
        }
        return customTabsSession;
    }

    /**
     * Register a Callback to be called when connected or disconnected from the Custom Tabs Service.
     *
     * @param callback
     */
    public void setConnectionCallback(CustomTabsConnectionCallback callback) {
        this.connectionCallback = callback;
    }

    /**
     * @return true if call to mayLaunchUrl was accepted.
     * @see {@link CustomTabsSession#mayLaunchUrl(Uri, Bundle, List)}.
     */
    public boolean mayLaunchUrl(Uri uri, Bundle extras, List<Bundle> otherLikelyBundles) {
        if (customTabsClient == null) return false;

        CustomTabsSession session = getSession();
        if (session == null) return false;

        return session.mayLaunchUrl(uri, extras, otherLikelyBundles);
    }

    @Override
    public void onServiceConnected(CustomTabsClient client) {
        customTabsClient = client;
        customTabsClient.warmup(0L);
        if (connectionCallback != null) {
            connectionCallback.onCustomTabConnected();
        }
    }

    @Override
    public void onServiceDisconnected() {
        customTabsClient = null;
        serviceConnection = null;
        if (connectionCallback != null) {
            connectionCallback.onCustomTabDisconnected();
        }
    }

}
