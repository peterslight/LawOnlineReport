package com.peterstev.lawonlinereportnigeria.activities;

import android.Manifest;
import android.app.Application;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.peterstev.lawonlinereportnigeria.R;
import com.peterstev.lawonlinereportnigeria.fragments.BlogFragment;
import com.peterstev.lawonlinereportnigeria.fragments.CategoryFragment;
import com.peterstev.lawonlinereportnigeria.fragments.ContactFragment;
import com.peterstev.lawonlinereportnigeria.fragments.CourtRulesFragment;
import com.peterstev.lawonlinereportnigeria.fragments.DirectoryFragment;
import com.peterstev.lawonlinereportnigeria.fragments.HomeFragment;
import com.peterstev.lawonlinereportnigeria.fragments.LONFragment;
import com.peterstev.lawonlinereportnigeria.interfaces.ApiInterface;
import com.peterstev.lawonlinereportnigeria.models.update.UpdateModel;
import com.peterstev.lawonlinereportnigeria.services.chrome.CustomTabsActivityHelper;
import com.peterstev.lawonlinereportnigeria.utils.BottomNavViewHelper;
import com.peterstev.lawonlinereportnigeria.utils.Utils;

import okhttp3.internal.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private HomeFragment homeFragment;
    private ContactFragment contactFragment;
    private CourtRulesFragment courtRulesFragment;
    private LONFragment LONFragment;
    private DirectoryFragment directoryFragment;
    private Toolbar toolbar;

    private CustomTabsActivityHelper tabsActivityHelper;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getUpdateStatus();

        context = this;

        tabsActivityHelper = new CustomTabsActivityHelper();

        toolbar = findViewById(R.id.default_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");

        homeFragment = new HomeFragment();
        contactFragment = new ContactFragment();
        courtRulesFragment = new CourtRulesFragment();
        LONFragment = new LONFragment();
        directoryFragment = new DirectoryFragment();

        BottomNavigationView navigationView = findViewById(R.id.main_navigation_view);
        BottomNavViewHelper.disableShiftMode(navigationView);
        navigationView.setOnNavigationItemSelectedListener(MainActivity.this);

        if (getIntent() != null) {
            try {
                retrieveDynamicLink();
            } catch (NullPointerException e) {
                Utils.toast(context, e.getMessage());
                redirectToHome();
            }
        } else {
            redirectToHome();
        }
    }

    private void retrieveDynamicLink() {
        FirebaseDynamicLinks.getInstance().getDynamicLink(getIntent())
                .addOnSuccessListener(this, pendingDynamicLinkData -> {
                    if (pendingDynamicLinkData != null) {
                        String postUrl = pendingDynamicLinkData.getLink().toString();
                        redirectToDetailPage(postUrl);
                    } else if (pendingDynamicLinkData == null) {
                        setUpCustomTab();
                        if (getIntent().getDataString() != null) {
                            try {
                                launchCustomTabs(getIntent().getDataString());
                            } catch (NullPointerException e) {
                                launchCustomTabs(getIntent().getStringExtra(Utils.POST_LINK_KEY));
                            }
                        } else if (getIntent().getStringExtra(Utils.POST_LINK_KEY) != null) {
                            launchCustomTabs(getIntent().getStringExtra(Utils.POST_LINK_KEY));
                        }
                    } else {
                        redirectToHome();
                    }
                })
                .addOnFailureListener(this, e ->
                        redirectToHome()
                );
    }

    private void redirectToHome() {
        applyFragment(homeFragment);
    }

    public Toolbar getToolbar() {
        return this.toolbar;
    }

    @Override
    protected void onResume() {
        super.onResume();
        backPressCount = 0;
        if (getCurrentFragment() == null) {
            applyFragment(homeFragment);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        tabsActivityHelper.bindCustomTabsService(this);
        setUpCustomTab();
    }

    @Override
    protected void onStop() {
        super.onStop();
        tabsActivityHelper.unBindCustomTabsService(this);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                applyFragment(homeFragment);
                return true;

            case R.id.nav_lotf:
                applyFragment(LONFragment);
                return true;

            case R.id.nav_court_rules:
                applyFragment(courtRulesFragment);
                return true;

            case R.id.nav_court_directory:
                applyFragment(directoryFragment);
                return true;

            case R.id.nav_contact:
                applyFragment(contactFragment);
                return true;

        }
        return false;
    }

    private void getUpdateStatus() {

        ApiInterface apiInterface = Utils.getRetrofitGson(this, Utils.BASE_URL);
        Call<UpdateModel> call = apiInterface.checkForUpdate();
        call.enqueue(new Callback<UpdateModel>() {
            @Override
            public void onResponse(Call<UpdateModel> call, Response<UpdateModel> response) {
                if (response.isSuccessful() && response.body() != null) {

                    UpdateModel update = response.body();
                    if (update != null) {
                        Double latestVersion = Double.valueOf(update.getVersion());
                        String updateLevel = update.getLevel();
                        String updateFeatures = update.getFeatures();
                        try {
                            Double currentVersion = Double.valueOf(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
                            if (latestVersion > currentVersion) {
                                if (updateLevel.contains("normal")) {
                                    DisplayUpdateNotification(
                                            "New Update Available",
                                            String.valueOf(latestVersion),
                                            updateFeatures,
                                            "normal"
                                    );
                                } else if (updateLevel.contains("critic")) {
                                    DisplayUpdateNotification(
                                            "Critical Update Available",
                                            String.valueOf(latestVersion),
                                            updateFeatures,
                                            "critical"
                                    );
                                }
                            }

                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<UpdateModel> call, Throwable t) {
                Utils.toast(MainActivity.this, "Failure");
            }
        });
    }

    private void DisplayUpdateNotification(String header, String ver, String whatsNew, String level) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyleDark);
        View layout = LayoutInflater.from(this).inflate(R.layout.update, null, false);
        TextView title, version, message;
        title = layout.findViewById(R.id.update_title);
        version = layout.findViewById(R.id.update_version);
        message = layout.findViewById(R.id.update_message);

        title.setText(header);
        version.setText(ver);
        message.setText(whatsNew);

        dialog.setCancelable(false);
        dialog.setView(layout);

        if (level.equals("critical")) {
            dialog.setPositiveButton("Update", (dialogInterface, i) -> {
                rediretToPlayStore();
                MainActivity.this.finish();
            });

            dialog.setNegativeButton("Close & Quit", (dialogInterface, i) -> {
                dialogInterface.dismiss();
                MainActivity.this.finish();
            });
        } else if (level.equals("normal")) {
            dialog.setPositiveButton("Update", (dialogInterface, i) -> {
                rediretToPlayStore();
            });

            dialog.setNegativeButton("Remind Me Later", (dialogInterface, i) -> {
                dialogInterface.dismiss();
            });
        }
        dialog.show();
    }

    private void rediretToPlayStore() {
        final String appLink = "https://play.google.com/store/apps/details?id=" + getPackageName();
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(appLink)));
        }
    }

    private int backPressCount = 0;

    @Override
    public void onBackPressed() {
        if (getCurrentFragment() instanceof CategoryFragment || getCurrentFragment() instanceof BlogFragment) {
            applyFragment(homeFragment);
        } else {
            backPressCount++;
            if (backPressCount < 2) {
                Utils.toast(context, "press again to exit");
            } else {
                super.onBackPressed();
            }
        }
    }
}