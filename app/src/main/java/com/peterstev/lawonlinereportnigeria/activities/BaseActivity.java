package com.peterstev.lawonlinereportnigeria.activities;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.crashlytics.android.Crashlytics;
import com.peterstev.lawonlinereportnigeria.R;
import com.peterstev.lawonlinereportnigeria.fragments.BlogFragment;
import com.peterstev.lawonlinereportnigeria.fragments.CategoryFragment;
import com.peterstev.lawonlinereportnigeria.fragments.CourtRulesFragment;
import com.peterstev.lawonlinereportnigeria.fragments.DirectoryFragment;
import com.peterstev.lawonlinereportnigeria.fragments.HomeFragment;
import com.peterstev.lawonlinereportnigeria.fragments.LONFragment;
import com.peterstev.lawonlinereportnigeria.models.directory.DirectoryModel;
import com.peterstev.lawonlinereportnigeria.models.home.CategoryModel;
import com.peterstev.lawonlinereportnigeria.models.home.HomeModel;
import com.peterstev.lawonlinereportnigeria.offline.AppDatabase;
import com.peterstev.lawonlinereportnigeria.offline.OfflineConstants;
import com.peterstev.lawonlinereportnigeria.services.broadcast_receiver.CustomTabsBcReceiver;
import com.peterstev.lawonlinereportnigeria.services.chrome.CustomTabsActivityHelper;
import com.peterstev.lawonlinereportnigeria.utils.Constants;
import com.peterstev.lawonlinereportnigeria.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Peterstev on 30/04/2018.
 * for LawOnlineReport
 */

@SuppressLint("Registered")
public abstract class BaseActivity extends AppCompatActivity implements BlogFragment.BlogClickListener,
        HomeFragment.OnArticleSelectedListener, CategoryFragment.CategoryClickListener,
        LONFragment.LonFragClickListener, CourtRulesFragment.CourtRulesFragClickListener,
        DirectoryFragment.DirectoryClickListener {

    private BlogFragment blogFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        blogFragment = new BlogFragment();
    }

    private CustomTabsIntent customTabsIntent;

    private PendingIntent createPendingShareIntent() {
        Intent actionIntent = new Intent(this, CustomTabsBcReceiver.class);
        return PendingIntent.getBroadcast(
                getApplicationContext(), 0, actionIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void setUpCustomTab() {
        //intent builder
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        builder.setSecondaryToolbarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        String desc = getString(R.string.share);
        Bitmap share = BitmapFactory.decodeResource(getResources(), R.drawable.ic_share_white_24dp);
        PendingIntent pendingIntent = createPendingShareIntent();
        builder.setActionButton(share, desc, pendingIntent);

        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_arrow_back);
        builder.setCloseButtonIcon(icon);

        builder.setStartAnimations(this, R.anim.slide_in_right, R.anim.slide_out_left);
        builder.setExitAnimations(this, android.R.anim.slide_in_left,
                android.R.anim.slide_out_right);

        builder.setShowTitle(true);
        customTabsIntent = builder.build();
    }

    public void launchCustomTabs(String href) {
        Uri uri = Uri.parse(href);
        if (customTabsIntent != null) {
            CustomTabsActivityHelper.openCustomTab(
                    this,
                    customTabsIntent,
                    uri,
                    (activity, uri1) -> openExternalBrowser(uri1));
        } else {
            openExternalBrowser(uri);
        }
    }

    private void openExternalBrowser(Uri uri) {
        Intent extIntent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(extIntent);
    }

    public Fragment getCurrentFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.main_frame);

        if (fragment != null) {
            return fragment;
        }
        return null;
    }

    public void applyHideShowFragment(Fragment currentFragment, Fragment newFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (currentFragment != null) {
            transaction.hide(getSupportFragmentManager().findFragmentByTag(currentFragment.getClass().getSimpleName()));
        }
        if (newFragment.isAdded()) {
            transaction.show(newFragment);
        } else {
            transaction.add(R.id.main_frame, newFragment, newFragment.getClass().getSimpleName());

            // 30th April 2018
            // i added a simple name tag, and removed the null;
            //if problems arise revert to addToBackStack(null)
            transaction.addToBackStack(null);
        }
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.commit();
    }

    public void applyFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_frame, fragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.commit();
    }

    //this interface is from the home > category fragment
    @Override
    public void OnArticleSelected(int position, List<HomeModel> categoryList) {
        CategoryFragment categoryFragment = CategoryFragment
                .newInstance(categoryList.get(position).getHref(), categoryList.get(position).getPostTitle());
        applyFragment(categoryFragment);
    }

    public void redirectToDetailPage(String link) {
        Intent intent = new Intent(this, PostDetailActivity.class);
        intent.putExtra(Utils.POST_LINK_KEY, link);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_share:
                final String appLink = "https://play.google.com/store/apps/details?id=" + getPackageName();
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, "Hello try out this law report and legal resource tool app :: \nLaw Online Report \n\n" + appLink);
                intent.setType("text/plain");
                startActivity(intent);
                break;

            case R.id.menu_blog:
                applyFragment(blogFragment);
                break;

            case R.id.menu_cases:
                launchCustomTabs(getString(R.string.cases_url));
                break;

            case R.id.menu_about:
                morePopup("Law Online Report", Constants.LOR_ABOUT, R.style.AppCompatAlertDialogStyleLight);
                break;

            case R.id.menu_refresh:
                new Thread(() -> {
                    Fragment fragment = getCurrentFragment();
                    String[] tables = {OfflineConstants.HOME_TABLE_NAME, OfflineConstants.BLOG_TABLE_NAME,
                            OfflineConstants.COURT_RULES_TABLE_NAME, OfflineConstants.LON_TABLE_NAME,
                            OfflineConstants.DIRECTORY_TABLE_NAME};
                    for (int x = 0; x < tables.length; x++) {
                        switch (x) {
                            case 0:
                                getAppDatabase(tables[x]).getHomeDAO().clearTable();
                                break;
                            case 1:
                                getAppDatabase(tables[x]).getBlogDAO().clearTable();
                                break;
                            case 2:
                                getAppDatabase(tables[x]).getCourtDAO().clearTable();
                                break;
                            case 3:
                                getAppDatabase(tables[x]).getLawsOfNigeriaDAO().clearTable();
                                break;
                            case 4:
                                getAppDatabase(tables[x]).getDirectoryDao().clearTable();
                                break;
                        }
                    }
                    runOnUiThread(() -> {
                        Utils.toast(BaseActivity.this, "Data Refreshed");
                        applyFragment(fragment == null ? new HomeFragment() : fragment);
                    });
                }).start();
                break;

            case android.R.id.home:
                super.onBackPressed();
                overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public AppDatabase getAppDatabase(String tableName) {
        return Room.databaseBuilder(this, AppDatabase.class, tableName)
                .build();
    }

    private AlertDialog alertDialog;

    @SuppressLint("InflateParams")
    public void morePopup(String title, String message, int theme) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this, theme);
        if (message != null) {
            dialog.setMessage(message);
        }

        if (title != null) {
            dialog.setTitle(title);
        }
        dialog.setCancelable(true);
        alertDialog = dialog.create();
        alertDialog.show();
    }


    //on click of a law of nigeria item
    @Override
    public void onLonFragItemClick(String href) {
        launchCustomTabs(href);
    }

    @Override
    public void onCourtFragItemClick(String href) {
        redirectToDetailPage(href);
    }


    //this interface is from the category > detail fragment
    @Override
    public void onCategorySelectedListener(int position, ArrayList<CategoryModel> detailItem) {
        redirectToDetailPage(detailItem.get(position).getHref());
    }

    //this interface is from the blog > detail fragment
    @Override
    public void OnBlogArticleClick(String hrefLink) {
        redirectToDetailPage(hrefLink);
    }

    @Override
    public void onDirectoryItemClick(DirectoryModel directory) {
        if (directory.getHref().trim().equalsIgnoreCase(Utils.BASE_URL)) {
            Utils.toast(this, "No Directory Available for " + directory.getPostTitle() + " yet, Try again later");
        } else {
            launchCustomTabs(directory.getHref());
        }
    }
}
