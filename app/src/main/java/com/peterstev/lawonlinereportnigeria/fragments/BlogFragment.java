package com.peterstev.lawonlinereportnigeria.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.ybq.android.spinkit.SpinKitView;
import com.peterstev.lawonlinereportnigeria.R;
import com.peterstev.lawonlinereportnigeria.adapters.blog.BlogAdapter;
import com.peterstev.lawonlinereportnigeria.interfaces.ApiInterface;
import com.peterstev.lawonlinereportnigeria.interfaces.FunctionalInterfaces;
import com.peterstev.lawonlinereportnigeria.models.blog.BlogModel;
import com.peterstev.lawonlinereportnigeria.offline.OfflineConstants;
import com.peterstev.lawonlinereportnigeria.offline.dao_interfaces.BlogDAO;
import com.peterstev.lawonlinereportnigeria.offline.schemas.BlogSchema;
import com.peterstev.lawonlinereportnigeria.utils.Utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BlogFragment extends BaseFragment implements BlogAdapter.ItemClickListener, SearchView.OnQueryTextListener {

    private Context context;
    private List<BlogSchema> blogModelList;
    private RecyclerView recyclerView;
    private BlogAdapter adapter;
    private SpinKitView progress, progress_secondary;
    private BlogFragment.BlogClickListener listener;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (BlogFragment.BlogClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement BlogClickListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpViews(getActivity(), "Law Online Blog");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getContext();
        return inflater.inflate(R.layout.default_recycler_view, container, false);
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

    private BlogDAO blogDAO;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        blogModelList = new ArrayList<>();
        progress = view.findViewById(R.id.default_progress_bar);
        progress_secondary = view.findViewById(R.id.default_progress_bar_2);

        recyclerView = view.findViewById(R.id.default_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        blogDAO = getAppDatabase(OfflineConstants.BLOG_TABLE_NAME).getBlogDAO();
        adapter = new BlogAdapter(context, blogDAO.getBlog(), recyclerView, BlogFragment.this);
        recyclerView.setAdapter(adapter);

        if (blogDAO.getBlog().size() > 0 && blogModelList.size() == 0) {
            getBlogPost.updateData(true);
        } else {
            getBlogPost.updateData(false);
        }
    }


    private int retryCount = 0;
    private int page = 1;
    FunctionalInterfaces.UpdateData getBlogPost = (boolean isToUpdate) -> {
        if (isToUpdate) {
            progress_secondary.setVisibility(View.VISIBLE);
            progress.setVisibility(View.GONE);
        } else {
            progress.setVisibility(View.VISIBLE);
            progress_secondary.setVisibility(View.GONE);
        }

        ApiInterface apiInterface = Utils.getRetrofitScalar(context, Utils.BASE_URL);
        Call<String> call = apiInterface.getBlogPostsApi(page);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {

                if (response.isSuccessful() && response.body() != null) {
                    processData(response.body());
                }
                progress.setVisibility(View.GONE);
                progress_secondary.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                progress.setVisibility(View.GONE);
                progress_secondary.setVisibility(View.GONE);
                retryCount++;
                if (retryCount < 3) {
                    getBlogPost.updateData(true);
                }
            }
        });
    };

    private int loadMoreRetryCount = 0;

    @Override
    public void loadMore() {
        progress_secondary.setVisibility(View.VISIBLE);
        Utils.getRetrofitScalar
                (context, Utils.BASE_URL)
                .getBlogPostsApi(page)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        if (response.isSuccessful()) {
                            try {
                                if (response.body() != null && response.body().length() > 0) {
                                    processData(response.body());
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

    @Override
    public void onArticleItemClick(String hrefLink) {
        listener.OnBlogArticleClick(hrefLink);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        page = 1;
    }

    public interface BlogClickListener {
        void OnBlogArticleClick(String hrefLink);
    }

    private void processData(String response) {
        Document doc = Jsoup.parse(response);
        Elements elements = doc.getElementsByClass("archive-content");


        for (Element element : elements) {
            BlogSchema category = new BlogSchema();

            //this snippet returns an array, so we just retrieve the last entry that is our main title
            Elements titleElements = element.getElementsByClass("entry-header").select("a[href]");
            int size = titleElements.size();

            category.setTitle(titleElements.get(size - 1).text());
            category.setExcerpt(element.getElementsByClass("entry-summary").select("p").text());
            category.setHref(element.getElementsByClass("entry-summary")
                    .select("a[href]").attr("href"));
            category.setAuthor("LawOnline");
            blogModelList.add(category);
        }

        if (blogModelList.size() > 0 && (blogModelList.size() > blogDAO.getBlog().size())) {
            //LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
          //  int oldPosition = manager.findFirstVisibleItemPosition();
            blogDAO.clearTable();
            adapter.updateAdapter(blogModelList);
//            blogDAO.getBlog().clear();
//            adapter = new BlogAdapter(context, blogModelList, recyclerView, BlogFragment.this);
//            adapter.notifyDataSetChanged();
//            recyclerView.setAdapter(adapter);
//            recyclerView.scrollToPosition(oldPosition);

            new Thread(() -> {

                for (int x = 0; x < blogModelList.size(); x++) {
                    BlogModel model = blogModelList.get(x);
                    BlogSchema blogSchema = new BlogSchema();
                    blogSchema.setHref(model.getHref());
                    blogSchema.setAuthor(model.getAuthor());
                    blogSchema.setExcerpt(model.getExcerpt());
                    blogSchema.setTitle(model.getTitle());
                    //blogSchema.setId(String.valueOf(x));
                    blogDAO.insert(blogSchema);
                }
            }).start();
            retryCount = 0;
        }
        progress.setVisibility(View.GONE);
        progress_secondary.setVisibility(View.GONE);
        page++;
    }
}