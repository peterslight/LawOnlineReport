package com.peterstev.lawonlinereportnigeria.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.ybq.android.spinkit.SpinKitView;
import com.peterstev.lawonlinereportnigeria.R;
import com.peterstev.lawonlinereportnigeria.adapters.home.HomeCategoryAdapter;
import com.peterstev.lawonlinereportnigeria.interfaces.ApiInterface;
import com.peterstev.lawonlinereportnigeria.interfaces.FunctionalInterfaces;
import com.peterstev.lawonlinereportnigeria.models.home.CategoryModel;
import com.peterstev.lawonlinereportnigeria.utils.Utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Peterstev on 4/14/2018.
 * for LawOnlineReport
 */

public class CategoryFragment extends BaseFragment implements HomeCategoryAdapter.ItemClickListener, SearchView.OnQueryTextListener {

    private HomeCategoryAdapter adapter;
    private Context context;
    private ArrayList<CategoryModel> categoryModel;
    private RecyclerView recyclerView;
    private SpinKitView progress, progress_secondary;
    private CategoryClickListener listener;
    private static final String CATEGORY_URL_END = "page/";

    public static CategoryFragment newInstance(String link, String title) {
        CategoryFragment fragment = new CategoryFragment();
        Bundle bundle = new Bundle();
        bundle.putString("href_link", link);
        bundle.putString("post_title", title);
        fragment.setArguments(bundle);
        return fragment;
    }

    private String getHrefLink() {
        return (getArguments().getString("href_link") + CATEGORY_URL_END).trim();
    }

    private String getPostTitle() {
        String postTitle = getArguments().getString("post_title");
        return postTitle == null ? null : postTitle.split(" ")[0];
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (CategoryClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement CategoryClickListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpViews(getActivity(), "Home :: " + getPostTitle());
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String queryText) {
        if (queryText != null && queryText.length() > 0 && adapter != null) {
            adapter.getFilter().filter(queryText);
        }
        return false;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getContext();
        return inflater.inflate(R.layout.default_recycler_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.default_recycler_view);
        recyclerView.setHasFixedSize(true);

        progress = view.findViewById(R.id.default_progress_bar);
        progress_secondary = view.findViewById(R.id.default_progress_bar_2);

        categoryModel = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
        getCategoryPosts.updateData(false);
    }

    private int retryCount = 0;
    private int page = 1;
    FunctionalInterfaces.UpdateData getCategoryPosts = (boolean notUseful) -> {

        progress.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = Utils.getRetrofitScalar(context, Utils.BASE_URL);
        Call<String> call = apiInterface.getGroupPosts(getHrefLink() + page);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {

                if (response.isSuccessful() && response.body() != null) {
                    processData(response);
                    adapter = new HomeCategoryAdapter(context, categoryModel, recyclerView, CategoryFragment.this);
                    recyclerView.setAdapter(adapter);
                }

                progress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<String> call, Throwable t) {
                progress.setVisibility(View.GONE);
                retryCount++;
                if (retryCount < 3) {
                    getCategoryPosts.updateData(false);
                } else {

                    if (getActivity() != null) {
                        View view = LayoutInflater.from(getActivity()).inflate(R.layout.default_error, null, false);
                        alertPopup(view);
                    } else {
                        Utils.toast(context, "Unable to load data, please check your network connection");
                    }
                }
            }
        });
    };

    public void alertPopup(View view) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyleLight);
        dialog.setView(view);
        dialog.setCancelable(true);
        dialog.setPositiveButton("Try Again", (DialogInterface dialogInterface, int i) -> {
                    dialogInterface.dismiss();
                    getCategoryPosts.updateData(true);
                }
        );
        dialog.setNegativeButton("Dismiss", (dialogInterface, i) -> dialogInterface.dismiss());
        dialog.show();
    }


    @Override
    public void onItemClick(int position, ArrayList<CategoryModel> categoryList) {
        listener.onCategorySelectedListener(position, categoryList);
    }

    public interface CategoryClickListener {
        void onCategorySelectedListener(int position, ArrayList<CategoryModel> categoryList);
    }

    private void processData(Response<String> response) {
        Document doc = Jsoup.parse(response.body());
        Elements elements = doc.getElementsByClass("archive-content");

        for (Element element : elements) {
            CategoryModel category = new CategoryModel();
            category.setTitle(element.getElementsByClass("entry-header").select("a[href]").text());
            category.setExcerpt(element.getElementsByClass("entry-summary").select("p").text());
            category.setHref(element.getElementsByClass("entry-summary")
                    .select("a[href]").attr("href"));
            category.setAuthor("LawOnline");
            categoryModel.add(category);
        }
        progress.setVisibility(View.GONE);
        progress_secondary.setVisibility(View.GONE);
        page++;
    }

    private int loadMoreRetryCount = 0;

    @Override
    public void loadMore() {
        progress_secondary.setVisibility(View.VISIBLE);
        Utils.getRetrofitScalar
                (context, Utils.BASE_URL)
                .getGroupPosts(getHrefLink() + page)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        if (response.isSuccessful()) {
                            try {
                                if (response.body() != null && response.body().length() > 0) {
                                    processData(response);
                                    adapter.stopLoading();
                                }
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                        } else {
                            loadMoreRetryCount++;
                            if (loadMoreRetryCount < 3) {
                                loadMore();
                            }
                        }
                        progress_secondary.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Utils.toast(context, t.getMessage());
                        loadMoreRetryCount++;
                        progress_secondary.setVisibility(View.GONE);
                        if (loadMoreRetryCount < 3) {
                            loadMore();
                        }
                    }
                });
    }

}
