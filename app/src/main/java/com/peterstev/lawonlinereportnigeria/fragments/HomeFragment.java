package com.peterstev.lawonlinereportnigeria.fragments;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.ybq.android.spinkit.SpinKitView;
import com.peterstev.lawonlinereportnigeria.R;
import com.peterstev.lawonlinereportnigeria.adapters.home.HomeAdapter;
import com.peterstev.lawonlinereportnigeria.interfaces.ApiInterface;
import com.peterstev.lawonlinereportnigeria.interfaces.FunctionalInterfaces;
import com.peterstev.lawonlinereportnigeria.models.home.HomeModel;
import com.peterstev.lawonlinereportnigeria.models.home.wp_json_api.LawModel;
import com.peterstev.lawonlinereportnigeria.offline.OfflineConstants;
import com.peterstev.lawonlinereportnigeria.offline.dao_interfaces.HomeDAO;
import com.peterstev.lawonlinereportnigeria.offline.schemas.HomeSchema;
import com.peterstev.lawonlinereportnigeria.utils.Utils;
import com.peterstev.lawonlinereportnigeria.viewmodels.HomeViewModel;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*
 * Created by Peterstev on 4/11/2018.
 * for LawOnlineReport
 */

public class HomeFragment extends BaseFragment implements HomeAdapter.ItemClickListener, SearchView.OnQueryTextListener {

    private Context context;
    private HomeAdapter adapter;
    private ArrayList<HomeModel> homeModelList;
    private RecyclerView recyclerView;
    private SpinKitView progress, progress_secondary;
    private OnArticleSelectedListener articleSelectedListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            articleSelectedListener = (OnArticleSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnArticleSelectedListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpViews(getActivity(), "Law Online Report");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getContext();
        return inflater.inflate(R.layout.default_recycler_view, container, false);
    }

    private HomeDAO homeDAO;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        homeModelList = new ArrayList<>();
        progress = view.findViewById(R.id.default_progress_bar);
        progress_secondary = view.findViewById(R.id.default_progress_bar_2);

        recyclerView = view.findViewById(R.id.default_recycler_view);
        recyclerView.setHasFixedSize(true);
        StaggeredGridLayoutManager layoutManager;
        switch (Utils.getSizeName(context)) {
            case 0:
                layoutManager = new StaggeredGridLayoutManager(2, 1);
                break;
            case 1:
                layoutManager = new StaggeredGridLayoutManager(2, 1);
                break;
            case 2:
                layoutManager = new StaggeredGridLayoutManager(3, 1);
                break;
            case 3:
                layoutManager = new StaggeredGridLayoutManager(4, 1);
                break;
            case 4:
                layoutManager = new StaggeredGridLayoutManager(4, 1);
                break;
            default:
                layoutManager = new StaggeredGridLayoutManager(2, 1);
                break;
        }
        recyclerView.setLayoutManager(layoutManager);
        homeDAO = getAppDatabase(OfflineConstants.HOME_TABLE_NAME).getHomeDAO();
        adapter = new HomeAdapter(context, homeDAO.getHomeGlossary(), HomeFragment.this);
        recyclerView.setAdapter(adapter);

        if (homeDAO.getHomeGlossary().size() > 0 && homeModelList.size() == 0) {
            getHomeData.updateData(true);
        } else {
            getHomeData.updateData(false);
        }
    }

    private int retryCount = 0;
    FunctionalInterfaces.UpdateData getHomeData = (boolean shouldUpdate) -> {
        if (shouldUpdate) {
            progress_secondary.setVisibility(View.VISIBLE);
            progress.setVisibility(View.GONE);
        } else {
            progress.setVisibility(View.VISIBLE);
            progress_secondary.setVisibility(View.GONE);
        }


        ApiInterface apiInterface = Utils.getRetrofitGson(context, Utils.BASE_URL);
        Call<LawModel> call = apiInterface.getLawHome();
        call.enqueue(new Callback<LawModel>() {
            @Override
            public void onResponse(@NonNull Call<LawModel> call, @NonNull Response<LawModel> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        processData(response);
                    }
                }
                progress.setVisibility(View.GONE);
                progress_secondary.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<LawModel> call, @NonNull Throwable t) {
                progress.setVisibility(View.GONE);
                progress_secondary.setVisibility(View.GONE);
                retryCount++;
                if (retryCount < 3) {
                    getHomeData.updateData(true);
                }
            }

        });
    };

    private void processData(Response<LawModel> response) {
        LawModel model = response.body();
        if (model != null) {

            String htmlBody = model.getContent().getRendered();
            Document doc = Jsoup.parse(htmlBody);
            Elements elements = doc.getElementsByClass("holdleft").select("a[href]");
            for (Element element : elements) {
                HomeModel category = new HomeModel();
                category.setKey(String.valueOf(element.text().charAt(0)));
                category.setPostTitle(element.text().trim());
                category.setHref(element.attr("href").trim());
                homeModelList.add(category);
            }

            //update the db if the new list is bigger than the db data

            if (homeModelList.size() > 0 && (homeModelList.size() != homeDAO.getHomeGlossary().size())) {
                homeDAO.clearTable();
                adapter.updateAdapter(homeModelList);
                new Thread(() -> {
                    for (int x = 0; x < homeModelList.size(); x++) {
                        HomeModel homeModel = homeModelList.get(x);
                        HomeSchema schema = new HomeSchema();
                        schema.setHref(homeModel.getHref());
                        schema.setKey(homeModel.getKey());
                        schema.setPostTitle(homeModel.getPostTitle());
                        // schema.setId(String.valueOf(x));
                        homeDAO.insert(schema);
                    }
                }).start();
                retryCount = 0;
                //homeDAO.getHomeGlossary().clear();
                //adapter = new HomeAdapter(context, homeModelList, HomeFragment.this);
                //adapter.notifyDataSetChanged();
                //recyclerView.setAdapter(adapter);
            }
            progress.setVisibility(View.GONE);
            progress_secondary.setVisibility(View.GONE);
        }

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

    public interface OnArticleSelectedListener {
        void OnArticleSelected(int position, List<HomeModel> categoryList);
    }

    @Override
    public void onItemClick(int position, List<HomeModel> categoryList) {
        articleSelectedListener.OnArticleSelected(position, categoryList);
    }

}