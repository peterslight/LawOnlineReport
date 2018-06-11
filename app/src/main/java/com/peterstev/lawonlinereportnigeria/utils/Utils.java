package com.peterstev.lawonlinereportnigeria.utils;

import android.Manifest;
import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.RequiresPermission;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;

import com.peterstev.lawonlinereportnigeria.interfaces.ApiInterface;
import com.peterstev.lawonlinereportnigeria.interfaces.FunctionalInterfaces;
import com.peterstev.lawonlinereportnigeria.models.home.HomeModel;
import com.readystatesoftware.chuck.ChuckInterceptor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by Peterstev on 4/11/2018.
 */

public class Utils {

    public static final String POST_LINK_KEY = "html_href";
    public static final String BASE_URL = "http://lawonlinereport.com/";
    public static final String DIRECTORY_URL = "http://lawonlinereport.com/directory";
    public static final String REGISTER_URL = "https://www.lawonlinereport.com/register";

    public static ApiInterface getRetrofitGson(Context context, String baseURL) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new ChuckInterceptor(context))
                .addInterceptor(chain -> {
                    Request request = chain.request();
                    Request.Builder builder = request.newBuilder().addHeader("Cache-Control", "no-cache");
                    request = builder.build();
                    return chain.proceed(request);
                })
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(baseURL)
                .client(client)
                .build();

        return retrofit.create(ApiInterface.class);
    }

    public static ApiInterface getRetrofitScalar(Context context, String baseURL) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new ChuckInterceptor(context))
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(ScalarsConverterFactory.create())
                .baseUrl(baseURL)
                .client(client)
                .build();

        return retrofit.create(ApiInterface.class);
    }

    public static String getDeviceDetails() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        String versionCode = Build.VERSION.RELEASE;
        String version = Build.VERSION_CODES.class.getFields()[android.os.Build.VERSION.SDK_INT].getName();
        if (model.toLowerCase().startsWith(manufacturer.toLowerCase())) {
            return capitalize(model + " Android " + versionCode + " " + version.toUpperCase());
        } else {
            return capitalize(manufacturer) + " " + model + " Android " + versionCode + " " + version.toUpperCase();
        }
    }


    private static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    /**
     * Method to get internet connection status
     *
     * @return true     if {@link Boolean} internet connection is established else false
     */
    @RequiresPermission(allOf = {Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.INTERNET})
    public static boolean isInternetConnected(Context context) {
        boolean isConnected;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = null;
        if (cm != null) {
            activeNetwork = cm.getActiveNetworkInfo();
        }
        isConnected = (activeNetwork != null)
                && (activeNetwork.isConnectedOrConnecting());
        return isConnected;
    }

    public static void toast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        context = null;
    }

    public static String getAbout() {
        return "beautiful gif with twittercolor \n" +
                "http://anjithsasindran.in/blog/2015/08/15/material-sharing-card/\n" +
                "\n" +
                "\n" +
                "https://stackoverflow.com/questions/31624935/floatingactionbutton-expand-into-a-new-activity\n" +
                "\n" +
                "\n" +
                "I made a custom activity, based on this question Circular reveal transition for new activity , that handle the CircularRevealAnimation and his reverse effect when the activity finish:\n" +
                "\n" +
                "public class RevealActivity extends AppCompatActivity {\n" +
                "\n" +
                "private View revealView;\n" +
                "\n" +
                "public static final String REVEAL_X=\"REVEAL_X\";\n" +
                "public static final String REVEAL_Y=\"REVEAL_Y\";\n" +
                "\n" +
                "public void showRevealEffect(Bundle savedInstanceState, final View rootView) {\n" +
                "\n" +
                "    revealView=rootView;\n" +
                "\n" +
                "    if (savedInstanceState == null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {\n" +
                "        rootView.setVisibility(View.INVISIBLE);\n" +
                "\n" +
                "        ViewTreeObserver viewTreeObserver = rootView.getViewTreeObserver();\n" +
                "\n" +
                "        if(viewTreeObserver.isAlive()) {\n" +
                "            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {\n" +
                "                @Override\n" +
                "                public void onGlobalLayout() {\n" +
                "\n" +
                "                    circularRevealActivity(rootView);\n" +
                "\n" +
                "                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {\n" +
                "                        rootView.getViewTreeObserver().removeGlobalOnLayoutListener(this);\n" +
                "                    } else {\n" +
                "                        rootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);\n" +
                "                    }\n" +
                "\n" +
                "                }\n" +
                "            });\n" +
                "        }\n" +
                "\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "@TargetApi(Build.VERSION_CODES.LOLLIPOP)\n" +
                "private void circularRevealActivity(View rootView) {\n" +
                "\n" +
                "    int cx = getIntent().getIntExtra(REVEAL_X, 0);\n" +
                "    int cy = getIntent().getIntExtra(REVEAL_Y, 0);\n" +
                "\n" +
                "    float finalRadius = Math.max(rootView.getWidth(), rootView.getHeight());\n" +
                "\n" +
                "    // create the animator for this view (the start radius is zero)\n" +
                "    Animator circularReveal = ViewAnimationUtils.createCircularReveal(rootView, cx, cy, 0, finalRadius);\n" +
                "    circularReveal.setDuration(400);\n" +
                "\n" +
                "    // make the view visible and start the animation\n" +
                "    rootView.setVisibility(View.VISIBLE);\n" +
                "    circularReveal.start();\n" +
                "}\n" +
                "\n" +
                "@Override\n" +
                "public boolean onOptionsItemSelected(MenuItem item) {\n" +
                "\n" +
                "switch (item.getItemId()) {\n" +
                "\n" +
                "case android.R.id.home: onBackPressed();break;\n" +
                "return super.onOptionsItemSelected(item);\n" +
                "\n" +
                "}    \n" +
                "\n" +
                "}\n" +
                "\n" +
                "@Override\n" +
                "public void onBackPressed() {\n" +
                "    destroyActivity(revealView);\n" +
                "}\n" +
                "\n" +
                "private void destroyActivity(View rootView) {\n" +
                "    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP)\n" +
                "        destroyCircularRevealActivity(rootView);\n" +
                "    else\n" +
                "        finish();\n" +
                "}\n" +
                "\n" +
                "@TargetApi(Build.VERSION_CODES.LOLLIPOP)\n" +
                "private void destroyCircularRevealActivity(final View rootView) {\n" +
                "    int cx = getIntent().getIntExtra(REVEAL_X, 0);\n" +
                "    int cy = getIntent().getIntExtra(REVEAL_Y, 0);\n" +
                "\n" +
                "    float finalRadius = Math.max(rootView.getWidth(), rootView.getHeight());\n" +
                "\n" +
                "    // create the animator for this view (the start radius is zero)\n" +
                "    Animator circularReveal = ViewAnimationUtils.createCircularReveal(rootView, cx, cy, finalRadius, 0);\n" +
                "    circularReveal.setDuration(400);\n" +
                "\n" +
                "    circularReveal.addListener(new Animator.AnimatorListener() {\n" +
                "        @Override\n" +
                "        public void onAnimationStart(Animator animator) {\n" +
                "\n" +
                "        }\n" +
                "\n" +
                "        @Override\n" +
                "        public void onAnimationEnd(Animator animator) {\n" +
                "            rootView.setVisibility(View.INVISIBLE);\n" +
                "            finishAfterTransition();\n" +
                "        }\n" +
                "\n" +
                "        @Override\n" +
                "        public void onAnimationCancel(Animator animator) {\n" +
                "\n" +
                "        }\n" +
                "\n" +
                "        @Override\n" +
                "        public void onAnimationRepeat(Animator animator) {\n" +
                "\n" +
                "        }\n" +
                "    });\n" +
                "\n" +
                "    // make the view visible and start the animation\n" +
                "    rootView.setVisibility(View.VISIBLE);\n" +
                "    circularReveal.start();\n" +
                "}\n" +
                "}\n" +
                "You can extend this with your own activity and call in your onCreate the method 'showRevealEffect' like this:\n" +
                "\n" +
                "@Override\n" +
                "protected void onCreate(@Nullable Bundle savedInstanceState) {\n" +
                "    super.onCreate(savedInstanceState);\n" +
                "    setContentView(R.layout.your_activity_layout);\n" +
                "\n" +
                "    //your code\n" +
                "\n" +
                "    View root= findViewById(R.id.your_root_id);\n" +
                "    showRevealEffect(savedInstanceState, root);\n" +
                "\n" +
                "}\n" +
                "You also have to use a transparent theme like this one:\n" +
                "\n" +
                "<style name=\"Theme.Transparent\" parent=\"Theme.AppCompat.Light.NoActionBar\">\n" +
                "    <item name=\"android:windowIsTranslucent\">true</item>\n" +
                "    <item name=\"android:windowBackground\">@android:color/transparent</item>\n" +
                "    <item name=\"colorPrimary\">@color/colorPrimary</item>\n" +
                "    <item name=\"colorPrimaryDark\">@color/colorPrimaryDark</item>\n" +
                "    <item name=\"colorAccent\">@color/colorAccent</item>\n" +
                "    <item name=\"colorControlNormal\">@android:color/white</item>\n" +
                "</style>\n" +
                "In the end, to launch this activity you should pass via extra the coordinates where the animation should start:\n" +
                "\n" +
                "int[] location = new int[2];\n" +
                "\n" +
                "    fab.getLocationOnScreen(location);\n" +
                "\n" +
                "    Intent intent = new Intent(this, YourRevealActivity.class);\n" +
                "\n" +
                "    intent.putExtra(SearchActivity.REVEAL_X, location[0]);\n" +
                "    intent.putExtra(SearchActivity.REVEAL_Y, location[1]);\n" +
                "\n" +
                "    startActivity(intent);\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "you can use this lib [https://github.com/sergiocasero/RevealFAB][1]\n" +
                "\n" +
                "[1]: https://github.com/sergiocasero/RevealFAB 3rd party its easy and simple to use\n" +
                "\n" +
                "Add to your layout\n" +
                "\n" +
                "<RelativeLayout...>\n" +
                "\n" +
                "    <android.support.design.widget.CoordinatorLayout...>\n" +
                "        <!-- YOUR CONTENT -->\n" +
                "    </android.support.design.widget.CoordinatorLayout>\n" +
                "\n" +
                "    <com.sergiocasero.revealfab.RevealFAB\n" +
                "        android:id=\"@+id/reveal_fab\"\n" +
                "        android:layout_width=\"match_parent\"\n" +
                "        android:layout_height=\"match_parent\"\n" +
                "        app:fab_color=\"@color/colorAccent\"\n" +
                "        app:fab_icon=\"@drawable/ic_add_white_24dp\"\n" +
                "        app:reveal_color=\"@color/colorAccent\" />\n" +
                "</RelativeLayout>\n" +
                "Important: This component goes above your content. You can use Coordinator, LinearLayout... or another Relative layout if you want :)\n" +
                "\n" +
                "As you can see, you have 3 custom attributes for customizing colors and icon\n" +
                "\n" +
                "Setting information about intent:\n" +
                "\n" +
                "revealFAB = (RevealFAB) findViewById(R.id.reveal_fab);\n" +
                "Intent intent = new Intent(MainActivity.this, DetailActivity.class);\n" +
                "revealFAB.setIntent(intent);\n" +
                "\n" +
                "revealFAB.setOnClickListener(new RevealFAB.OnClickListener() {\n" +
                "    @Override\n" +
                "    public void onClick(RevealFAB button, View v) {\n" +
                "        button.startActivityWithAnimation();\n" +
                "    }\n" +
                "});\n" +
                "Don't forget call onResume() method!\n" +
                "\n" +
                "@Override\n" +
                "protected void onResume() {\n" +
                "    super.onResume();\n" +
                "    revealFAB.onResume();\n" +
                "}";
    }

    public static void snackBar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }

    public static void snackBarAction(View view, String message, FunctionalInterfaces.GetData getDataFunction) {
        Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE).setAction("Retry", v -> getDataFunction.getData());
    }

    public static String getStandardViewCount(Long viewCount) {
        if (viewCount == null) {
            return null;
        }
        if (viewCount < 1000) {
            return String.valueOf(viewCount);
        } else {
            int count = (int) (Math.log(viewCount) / Math.log(1000));
            return String.format(Locale.getDefault(), "%.1f %C", viewCount / Math.pow(1000, count), "KMGTPE".charAt(count - 1));
        }
    }

    public static int getSizeName(Context context) {
        int screenLayout = context.getResources().getConfiguration().screenLayout;
        screenLayout &= Configuration.SCREENLAYOUT_SIZE_MASK;

        switch (screenLayout) {
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                return 1;
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                return 2;
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                return 3;
            case 4: // Configuration.SCREENLAYOUT_SIZE_XLARGE is API >= 9
                return 4;
            default:
                return 0;
        }
    }

}
//
//preferences = PreferenceManager.getDefaultSharedPreferences(context);
//        listOfData = new TypeToken<ArrayList<CourtRulesModel>>() {
//        }.getType();
//
//        String courtRules = new Gson().toJson(courtRulesModels, listOfData);
//        preferences.edit().putString(Constants.COURT_RULES_KEY, courtRules).apply();
//        if (savedList == null) {
//        savedList = new Gson().fromJson(preferences.getString(Constants.COURT_RULES_KEY, ""),
//        listOfData);
//        }

// if (savedList != null && courtRulesModels.size() == 0) {
//         adapter = new CourtRulesAdapter(context, recyclerView, savedList, CourtRulesFragment.this);
//         Utils.toast(context, "saved list adapter count :: " + adapter.getItemCount());
//         } else {
//
//         Utils.toast(context, "original list adapter count :: " + adapter.getItemCount());
//         }

//original Categoryfragment data retrive
//    private void getPostRetrofit() {
//        progressDialog.show();
//        ApiInterface apiInterface = Utils.getRetrofitScalar(context, Utils.BASE_URL);
//        Call<String> call = apiInterface.getGroupPosts(link);
//        call.enqueue(new Callback<String>() {
//            @Override
//            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
//
//                Log.d("TAG", response.body());
//                if (response.isSuccessful() && response.body() != null) {
//
//                    Document doc = Jsoup.parse(response.body());
//                    Utils.toast(context, doc.body().toString());
//                    Elements elements = doc.getElementsByClass("article-content");
//                    for (Element element : elements) {
//                        CategoryModel category = new CategoryModel();
//                        category.setTitle(element.getElementsByClass("entry-header").select("a[href]").text());
//                        category.setExcerpt(element.getElementsByClass("entry-content").select("p").text() + "...");
//                        category.setAuthor(element.getElementsByClass("url").text());
//                        category.setDate(element.getElementsByClass("entry-date").text());
//                        category.setHref(element.getElementsByClass("entry-content")
//                                .select("a[href]").attr("href"));
//                        String tags = element.getElementsByClass("tag-links").text();
//                        ArrayList<String> tagList = new ArrayList<>(
//                                Arrays.asList(tags.split(","))
//                        );
//
//                        Utils.toast(context, "for loop finished");
//                        category.setTags(tagList);
//                        homeCategoryModel.add(category);
//                    }
//
//                    Utils.toast(context, "Size : " + homeCategoryModel.size());
//                    progressDialog.dismiss();
//                    adapter = new HomeCategoryAdapter(context, homeCategoryModel, CategoryFragment.this);
//                    recyclerView.setAdapter(adapter);
//                }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<String> call, Throwable t) {
//                progressDialog.dismiss();
//                if (t.getMessage().contains("Exception")) {
//                    retryCount++;
//                    if (retryCount <= 3) {
//                        if (t.getMessage().contains("Timeout")) {
//                            Utils.toast(context, "Timeout: Retrying");
//                            getPostRetrofit();
//                        }
//                        if (t.getMessage().contains("No address associated with hostname")) {
//                            Snackbar.make(recyclerView, "No address associated with hostname",
//                                    Snackbar.LENGTH_INDEFINITE).setAction("Retry", v -> getPostRetrofit());
//                        }
//                        if (t.getMessage().equals("")) {
//                            Utils.toast(context, "Timeout: Retrying");
//                            getPostRetrofit();
//                        }
//                    } else {
//                        Snackbar.make(recyclerView, "unable to connect try again later",
//                                Snackbar.LENGTH_INDEFINITE).setAction("Retry", v -> getPostRetrofit());
//                    }
//                }
//            }
//        });
//    }


//    private void setActiveFragment(Fragment activefragment) {
////        FragmentManager fragmentManager = getSupportFragmentManager();
////        FragmentTransaction transaction = fragmentManager.beginTransaction();
////
////        if (activefragment instanceof CategoryFragment) {
////            transaction.hide(getSupportFragmentManager().findFragmentByTag(homeFragment.getClass().getSimpleName()));
////            transaction.add(R.id.main_frame, activefragment, activefragment.getClass().getSimpleName());
////            transaction.addToBackStack(null);
////            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
////        } else if (activefragment instanceof PostDetailFragment) {
////            if (categoryFragment != null && getSupportFragmentManager().findFragmentByTag(categoryFragment.getClass().getSimpleName()).isVisible()) {
////                transaction.hide(getSupportFragmentManager().findFragmentByTag(categoryFragment.getClass().getSimpleName()));
////            } else {
////                transaction.hide(getSupportFragmentManager().findFragmentByTag(detailFragment.getClass().getSimpleName()));
////            }
////
////            if (blogFragment != null && getSupportFragmentManager().findFragmentByTag(blogFragment.getClass().getSimpleName()).isVisible()) {
////                transaction.hide(getSupportFragmentManager().findFragmentByTag(blogFragment.getClass().getSimpleName()));
////            } else {
////                transaction.hide(getSupportFragmentManager().findFragmentByTag(detailFragment.getClass().getSimpleName()));
////            }
////
////            transaction.add(R.id.main_frame, activefragment, activefragment.getClass().getSimpleName());
////            transaction.addToBackStack(null);
////            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
////        } else {
////            transaction.replace(R.id.main_frame, activefragment, activefragment.getClass().getSimpleName())
////                    .addToBackStack(null);
////            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
////        }
////        transaction.commit();
////
////    }


//        FragmentManager manager = getSupportFragmentManager();
//        int id = item.getItemId();
//        switch (id) {
//            case R.id.nav_home:
//                if (manager.findFragmentByTag(homeFragment.getClass().getSimpleName()).isHidden()) {
//                    applyFragment(getCurrentFragment(), homeFragment);
//                }
//                break;
//            case R.id.nav_blog:
//                if (manager.findFragmentByTag(blogFragment.getClass().getSimpleName()).isHidden()) {
//                    applyFragment(getCurrentFragment(), blogFragment);
//                }
//                break;
//            case R.id.nav_lotf:
//                if (manager.findFragmentByTag(LONFragment.getClass().getSimpleName()).isHidden()) {
//                    applyFragment(getCurrentFragment(), LONFragment);
//                }
//                break;
//            case R.id.nav_court_rules:
//                if (manager.findFragmentByTag(courtRulesFragment.getClass().getSimpleName()).isHidden()) {
//                    applyFragment(getCurrentFragment(), courtRulesFragment);
//                }
//                break;
//            case R.id.nav_more:
//                if (manager.findFragmentByTag(moreFragment.getClass().getSimpleName()).isHidden()) {
//                    applyFragment(getCurrentFragment(), moreFragment);
//                }
//                break;
//        }