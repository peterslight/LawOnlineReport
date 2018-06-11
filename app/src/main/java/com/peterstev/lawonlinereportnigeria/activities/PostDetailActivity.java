package com.peterstev.lawonlinereportnigeria.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.peterstev.lawonlinereportnigeria.R;
import com.peterstev.lawonlinereportnigeria.adapters.detail.DetailAdapter;
import com.peterstev.lawonlinereportnigeria.interfaces.ApiInterface;
import com.peterstev.lawonlinereportnigeria.interfaces.FunctionalInterfaces;
import com.peterstev.lawonlinereportnigeria.models.detail.DetailModel;
import com.peterstev.lawonlinereportnigeria.models.detail.RelatedPosts;
import com.peterstev.lawonlinereportnigeria.services.chrome.CustomTabsActivityHelper;
import com.peterstev.lawonlinereportnigeria.utils.Constants;
import com.peterstev.lawonlinereportnigeria.utils.Utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Peterstev on 05/05/2018.
 * for LawOnlineReport
 */
public class PostDetailActivity extends BaseActivity implements DetailAdapter.ItemClickListener {
    private RecyclerView recyclerView;
    private Context context;
    private ArrayList<DetailModel> detailModelList;
    private SpinKitView progress;
    private String postHrefLink;
    private CustomTabsActivityHelper tabsActivityHelper;
    private LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        context = this;

        tabsActivityHelper = new CustomTabsActivityHelper();

        detailModelList = new ArrayList<>();

        progress = findViewById(R.id.detail_progress_bar);

        recyclerView = findViewById(R.id.detail_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        Toolbar toolbar = findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        toolbar.setTitle("Law Online Report");

        FloatingActionButton fab = findViewById(R.id.detail_fab);
        fab.setOnClickListener(view -> generateDynamicLink());

        if (getIntent() != null) {
            try {
                retrieveDynamicLink();
            } catch (NullPointerException e) {
                Utils.toast(context, e.getMessage());
                retrieveLocalIntent();
            }
        } else {
            retrieveLocalIntent();
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


    private void retrieveDynamicLink() {
        FirebaseDynamicLinks.getInstance().getDynamicLink(getIntent())
                .addOnSuccessListener(this, pendingDynamicLinkData -> {
                    if (pendingDynamicLinkData == null) {
                        retrieveLocalIntent();
                    } else if (pendingDynamicLinkData != null) {
                        postHrefLink = pendingDynamicLinkData.getLink().toString();
                        getPostDetail.getData(postHrefLink);
                    } else {
                        redirectOnError();
                    }
                })
                .addOnFailureListener(this, e ->
                        retrieveLocalIntent()
                );
    }

    private void retrieveLocalIntent() {
        try {
            postHrefLink = getIntent() == null ? null : getIntent().getStringExtra(Utils.POST_LINK_KEY);
            getPostDetail.getData(postHrefLink);
        } catch (NullPointerException e) {
            Utils.toast(context, getIntent().getDataString());
            redirectOnError();
        }
    }

    private void redirectOnError() {
        Utils.snackBar(recyclerView, "Link appears to be broken :: Redirecting...");
        startActivity(new Intent(PostDetailActivity.this, MainActivity.class).putExtra(Utils.POST_LINK_KEY, getIntent().getDataString()));
        this.finish();
    }


    private DetailAdapter adapter;
    FunctionalInterfaces.LoadPost getPostDetail = (link) -> {
        if (link != null) {
            progress.setVisibility(View.VISIBLE);
            ApiInterface apiInterface = Utils.getRetrofitScalar(context, Utils.BASE_URL);
            Call<String> call = apiInterface.getPostDetails(link);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {

                    if (response.isSuccessful() && response.body() != null) {
                        String html = response.body();
                        Document document = Jsoup.parse(html);
                        Elements postElement = document.getElementsByClass("hitmag-single");

                        if (postElement.size() < 1 || postElement == null) {
                            redirectOnError();
                        } else {
                            new ProcessData().execute(document);
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    progress.setVisibility(View.GONE);
                    Utils.snackBar(recyclerView, t.getMessage());
                }
            });

        } else {
            redirectOnError();
        }
    };

    private class ProcessData extends AsyncTask<Document, Void, List<DetailModel>> {

        @Override
        protected List<DetailModel> doInBackground(Document... document) {
            Elements postElement = document[0].getElementsByClass("hitmag-single");
            Element element = postElement.get(0);

            DetailModel detailModel = new DetailModel();
            detailModel.setTitle(element.getElementsByClass("entry-title").text());

            String text = Parser.
                    unescapeEntities(clean.clean(element.getElementsByClass("entry-content")
                            .select("p, h1, h2, h3, li").toString()), false).trim();


            /*
             * the full text comes with th word 'related principles' at the end
             * so what we do here is to split the text at the point where we see
             * related principles, discard it and return the remaining text as the main content
             */
            String[] content = text.split("Related Principles");
            detailModel.setContent(content[0]);

            detailModel.
                    setFullCaseLink(element.getElementsByClass("entry-content")
                            .select("a[href]").attr("href"));
            detailModel.setViews(element.getElementsByClass("post-views-count").text());
            //get data for the related posts
            detailModel.setRelatedPosts(getRelatedPosts(document[0]));

            detailModelList.add(detailModel);

            return detailModelList;
        }

        @Override
        protected void onPostExecute(List<DetailModel> detailModels) {
            super.onPostExecute(detailModels);
            adapter = new DetailAdapter(context, detailModels, PostDetailActivity.this);
            recyclerView.setAdapter(adapter);

            if (adapter.getItemCount() < 2) {
                layoutManager.setReverseLayout(false);
            } else {
                if (!layoutManager.getReverseLayout()) {
                    layoutManager.setReverseLayout(true);
                }
                recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
            }
            progress.setVisibility(View.GONE);
        }
    }

    private ArrayList<RelatedPosts> getRelatedPosts(Document document) {
        Elements elements = document.getElementsByClass("crp_related").select("a[href]");

        ArrayList<RelatedPosts> relatedPostList = new ArrayList<>();

        for (Element element : elements) {
            RelatedPosts relatedPosts = new RelatedPosts();

            relatedPosts.setRelatedTitle(element.text());
            relatedPosts.setRelatedHref(element.attr("href").trim());
            relatedPostList.add(relatedPosts);
        }

        return relatedPostList;
    }


    FunctionalInterfaces.JsoupClean clean = (String html) -> {
        Document document = Jsoup.parse(html);
        document.outputSettings(new Document.OutputSettings().prettyPrint(false));
        document.select("br").append("\\n");
        document.select("p").prepend("\\n\n");
        String text = document.html().replaceAll("\\\\n", "\n");

        return Jsoup.clean(text, "", Whitelist.none(), new Document.OutputSettings().prettyPrint(false));
    };

    @Override
    public void onRelatedPostClick(String postLink) {
        postHrefLink = postLink;
        getPostDetail.getData(postLink);
    }

    @Override
    public void onReadMoreClick(String fullCaseLink) {
        launchCustomTabs(fullCaseLink);
    }

    private void generateDynamicLink() {
        Utils.snackBar(recyclerView, "Generating Link, Please Wait...");
        FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse(postHrefLink))
                .setDynamicLinkDomain(Constants.DYNAMIC_LINK_DOMAIN)
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                .buildShortDynamicLink()
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_SEND);
                        intent.putExtra(Intent.EXTRA_TEXT, "Hello check out Law Online Report :: law reporting and legal research made easy \n\n"
                                + detailModelList.get(0).getTitle()
                                + "\n\n"
                                + task.getResult().getShortLink());
                        intent.setType("text/plain");
                        startActivity(intent);
                    } else {
                        Utils.snackBar(recyclerView, "Failed to Generate Link, Try Again");
                    }
                });
    }
}
