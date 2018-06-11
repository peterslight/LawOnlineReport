package com.peterstev.lawonlinereportnigeria.services.broadcast_receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.peterstev.lawonlinereportnigeria.utils.Constants;
import com.peterstev.lawonlinereportnigeria.utils.Utils;

/**
 * Created by Peterstev on 08/05/2018.
 * for LawOnlineReport
 */
public class CustomTabsBcReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String url = intent.getDataString();
        Utils.toast(context, "Generating Link, Please Wait...");
        FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse(url))
                .setDynamicLinkDomain(Constants.DYNAMIC_LINK_DOMAIN)
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                .buildShortDynamicLink()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Intent linkIntent = new Intent();
                        linkIntent.setAction(Intent.ACTION_SEND);
                        linkIntent.putExtra(Intent.EXTRA_TEXT, "Hello check out Law Online Report :: law reporting and legal research made easy \n\n" + task.getResult().getShortLink());
                        linkIntent.setType("text/plain");
                        linkIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(linkIntent);
                    } else {
                        Utils.toast(context, "Failed to Generate Link, Try Again");
                    }
                });
    }
}
