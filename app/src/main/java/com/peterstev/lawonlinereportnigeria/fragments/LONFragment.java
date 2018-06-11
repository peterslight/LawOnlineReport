package com.peterstev.lawonlinereportnigeria.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.ybq.android.spinkit.SpinKitView;
import com.peterstev.lawonlinereportnigeria.R;
import com.peterstev.lawonlinereportnigeria.adapters.lon.LonAdapter;
import com.peterstev.lawonlinereportnigeria.interfaces.ApiInterface;
import com.peterstev.lawonlinereportnigeria.interfaces.FunctionalInterfaces;
import com.peterstev.lawonlinereportnigeria.models.lon.LonModel;
import com.peterstev.lawonlinereportnigeria.offline.OfflineConstants;
import com.peterstev.lawonlinereportnigeria.offline.dao_interfaces.LonDAO;
import com.peterstev.lawonlinereportnigeria.offline.schemas.LonSchema;
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

/**
 * Created by Peterstev on 26/04/2018.
 * for LawOnlineReport
 */
public class LONFragment extends BaseFragment implements LonAdapter.LonClickListener, SearchView.OnQueryTextListener {

    private RecyclerView recyclerView;
    private Context context;
    private ArrayList<LonModel> lonModelList;
    private LonAdapter adapter;
    private SpinKitView progress, progress_secondary;
    private LonFragClickListener listener;
    private LonDAO lonDAO;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getContext();
        return inflater.inflate(R.layout.default_recycler_view, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (LonFragClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement LonFragClickListener");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        pageNumber = 1;
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpViews(getActivity(), "Laws of Nigeria");
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lonModelList = new ArrayList<>();
        progress = view.findViewById(R.id.default_progress_bar);
        progress_secondary = view.findViewById(R.id.default_progress_bar_2);

        recyclerView = view.findViewById(R.id.default_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        lonDAO = getAppDatabase(OfflineConstants.LON_TABLE_NAME).getLawsOfNigeriaDAO();
        adapter = new LonAdapter(context, recyclerView, lonDAO.getLawsOfNigeria(), LONFragment.this);
        recyclerView.setAdapter(adapter);

        if (lonDAO.getLawsOfNigeria().size() > 0 && lonModelList.size() == 0) {
            lawsOfNigeria.updateData(true);
        } else {
            lawsOfNigeria.updateData(false);
        }
    }

    private int retryCount = 0;
    private int pageNumber = 1;
    FunctionalInterfaces.UpdateData lawsOfNigeria = (boolean shouldUpdate) -> {
        if (shouldUpdate) {
            progress_secondary.setVisibility(View.VISIBLE);
            progress.setVisibility(View.GONE);
        } else {
            progress.setVisibility(View.VISIBLE);
            progress_secondary.setVisibility(View.GONE);
        }

        ApiInterface apiInterface = Utils.getRetrofitScalar(context, Utils.BASE_URL);
        Call<String> call = apiInterface.getLawsOfNigeria(pageNumber);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        processData(response);
                    }
                }
                progress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                progress.setVisibility(View.GONE);
                retryCount++;
                if (retryCount < 3) {
                    lawsOfNigeria.updateData(true);
                }
            }
        });
    };

    private void processData(Response<String> response) {
        Document doc = Jsoup.parse(response.body());
        Elements elements = doc.getElementsByClass("lcp_catlist").select("a[href]");

        for (Element element : elements) {
            LonModel lonModel = new LonModel();
            lonModel.setPostTitle(element.text().trim());
            lonModel.setHref(element.attr("href").trim());
            lonModel.setKey(String.valueOf(element.text().charAt(0)).toUpperCase());
            lonModelList.add(lonModel);
        }

        //update the db if the new list is bigger than the db data

        if (lonModelList.size() > 0 && (lonModelList.size() > lonDAO.getLawsOfNigeria().size())) {
//            LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
//            int oldPosition = manager.findFirstVisibleItemPosition();

            //safely clear the data in the adapter and update
            lonDAO.clearTable();
            adapter.updateAdapter(lonModelList);
//            lonDAO.getLawsOfNigeria().clear();
//            adapter = new LonAdapter(context, recyclerView, lonModelList, LONFragment.this);
//            adapter.notifyDataSetChanged();
//            recyclerView.setAdapter(adapter);
//            recyclerView.scrollToPosition(oldPosition);

            new Thread(() -> {
                for (int x = 0; x < lonModelList.size(); x++) {
                    LonModel model = lonModelList.get(x);
                    LonSchema schema = new LonSchema();
                    schema.setHref(model.getHref());
                    schema.setKey(model.getKey());
                    schema.setPostTitle(model.getPostTitle());
                    //schema.setId(String.valueOf(x));
                    lonDAO.insert(schema);
                }
            }).start();
            retryCount = 0;
        }
        progress.setVisibility(View.GONE);
        progress_secondary.setVisibility(View.GONE);
        pageNumber++;
    }

    @Override
    public void onLonItemClick(int position, List<LonModel> lonModelList) {
        listener.onLonFragItemClick(lonModelList.get(position).getHref());
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

    public interface LonFragClickListener {
        void onLonFragItemClick(String href);
    }


    private int loadMoreRetryCount = 0;

    @Override
    public void loadMore() {
        progress_secondary.setVisibility(View.VISIBLE);
        Utils.getRetrofitScalar
                (context, Utils.BASE_URL)
                .getLawsOfNigeria(pageNumber)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        if (response.isSuccessful()) {
                            try {
                                if (response.body() != null && response.body().length() > 0) {
                                    processData(response);
                                    loadMoreRetryCount = 0;
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
                    public void onFailure(@NonNull Call<String> call, Throwable t) {
                        progress_secondary.setVisibility(View.GONE);
                        loadMoreRetryCount++;
                        if (loadMoreRetryCount < 3) {
                            loadMore();
                        }
                    }
                });
    }
}
