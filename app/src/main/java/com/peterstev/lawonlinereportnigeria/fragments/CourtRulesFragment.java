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
import com.peterstev.lawonlinereportnigeria.adapters.court.CourtRulesAdapter;
import com.peterstev.lawonlinereportnigeria.interfaces.ApiInterface;
import com.peterstev.lawonlinereportnigeria.interfaces.FunctionalInterfaces;
import com.peterstev.lawonlinereportnigeria.models.court_rules.CourtRulesModel;
import com.peterstev.lawonlinereportnigeria.offline.OfflineConstants;
import com.peterstev.lawonlinereportnigeria.offline.dao_interfaces.CourtRulesDAO;
import com.peterstev.lawonlinereportnigeria.offline.schemas.CourtSchema;
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
 * A simple {@link Fragment} subclass.
 */
public class CourtRulesFragment extends BaseFragment implements CourtRulesAdapter.CourtRulesClickListener, SearchView.OnQueryTextListener {

    private RecyclerView recyclerView;
    private Context context;
    private List<CourtRulesModel> courtRulesModels;
    private CourtRulesAdapter adapter;
    private SpinKitView progress;
    private SpinKitView progress_secondary;
    private CourtRulesFragment.CourtRulesFragClickListener listener;

    @Override
    public void onResume() {
        super.onResume();
        setUpViews(getActivity(), "Court Rules");
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (CourtRulesFragment.CourtRulesFragClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement CourtRulesFragClickListener");
        }
    }

    private CourtRulesDAO courtRulesDAO;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        courtRulesModels = new ArrayList<>();
        progress = view.findViewById(R.id.default_progress_bar);
        progress_secondary = view.findViewById(R.id.default_progress_bar_2);

        recyclerView = view.findViewById(R.id.default_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        courtRulesDAO = getAppDatabase(OfflineConstants.COURT_RULES_TABLE_NAME).getCourtDAO();
        adapter = new CourtRulesAdapter(context, recyclerView, courtRulesDAO.getCourtRules(), CourtRulesFragment.this);
        recyclerView.setAdapter(adapter);

        if (courtRulesDAO.getCourtRules().size() > 0 && courtRulesModels.size() == 0) {
            courtRules.updateData(true);
        } else {
            courtRules.updateData(false);
        }

    }

    private int pageNumber = 1;
    FunctionalInterfaces.UpdateData courtRules = (boolean isToUpdate) -> {
        if (isToUpdate) {
            progress_secondary.setVisibility(View.VISIBLE);
            progress.setVisibility(View.GONE);
        } else {
            progress.setVisibility(View.VISIBLE);
            progress_secondary.setVisibility(View.GONE);
        }

        ApiInterface apiInterface = Utils.getRetrofitScalar(context, Utils.BASE_URL);
        Call<String> call = apiInterface.getCourtRules(pageNumber);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        processData(response);
                    }
                }

            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                progress.setVisibility(View.GONE);
                progress_secondary.setVisibility(View.GONE);
                Utils.toast(context, t.getMessage());
            }
        });
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        pageNumber = 1;
    }
    private void processData(Response<String> response) {
        Document doc = Jsoup.parse(response.body());
        Elements elements = doc.getElementsByClass("lcp_catlist").select("a[href]");

        for (Element element : elements) {
            CourtRulesModel courtModel = new CourtRulesModel();
            courtModel.setPostTitle(element.text().trim());
            courtModel.setHref(element.attr("href").trim());
            courtModel.setKey(String.valueOf(element.text().charAt(0)).toUpperCase());
            courtRulesModels.add(courtModel);
        }

        //update the db if the new list is bigger than the db data

        if (courtRulesModels.size() > 0 && (courtRulesModels.size() > courtRulesDAO.getCourtRules().size())) {

//            LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
//            int oldPosition = manager.findFirstVisibleItemPosition();

            //safely clear the data in the adapter and update
            courtRulesDAO.clearTable();
            adapter.updateAdapter(courtRulesModels);
//            courtRulesDAO.getCourtRules().clear();
//            adapter = new CourtRulesAdapter(context, recyclerView, courtRulesModels, CourtRulesFragment.this);
//            adapter.notifyDataSetChanged();
//            recyclerView.setAdapter(adapter);
//            recyclerView.scrollToPosition(oldPosition);

            new Thread(() -> {
                for (int x = 0; x < courtRulesModels.size(); x++) {
                    CourtRulesModel model = courtRulesModels.get(x);
                    CourtSchema courtSchema = new CourtSchema();
                    courtSchema.setHref(model.getHref());
                    courtSchema.setPostTitle(model.getPostTitle());
                    courtSchema.setKey(model.getKey());
                    courtRulesDAO.insert(courtSchema);
                }
            }).start();
        }
        progress.setVisibility(View.GONE);
        progress_secondary.setVisibility(View.GONE);
        pageNumber++;
    }

    public interface CourtRulesFragClickListener {
        void onCourtFragItemClick(String href);
    }

    private int loadMoreRetryCount = 0;

    @Override
    public void loadMore() {
        progress_secondary.setVisibility(View.VISIBLE);
        Utils.getRetrofitScalar
                (context, Utils.BASE_URL)
                .getCourtRules(pageNumber)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        progress_secondary.setVisibility(View.GONE);
                        if (response.isSuccessful()) {
                            try {
                                if (response.body() != null && response.body().length() > 0) {
                                    processData(response);
                                    adapter.stopLoading();
                                    adapter.notifyDataSetChanged();
                                    loadMoreRetryCount = 0;

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
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        progress_secondary.setVisibility(View.GONE);
                        Utils.toast(context, t.getMessage());
                        loadMoreRetryCount++;
                        if (loadMoreRetryCount < 3) {
                            loadMore();
                        } else {
                            Utils.toast(context, "failed to load more data, check network and retry");
                        }
                    }
                });
    }

    @Override
    public void onCourtItemClick(String url) {
        listener.onCourtFragItemClick(url);
    }
}