package com.peterstev.lawonlinereportnigeria.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.ybq.android.spinkit.SpinKitView;
import com.peterstev.lawonlinereportnigeria.R;
import com.peterstev.lawonlinereportnigeria.adapters.directory.DirectoryAdapter;
import com.peterstev.lawonlinereportnigeria.interfaces.ApiInterface;
import com.peterstev.lawonlinereportnigeria.interfaces.FunctionalInterfaces;
import com.peterstev.lawonlinereportnigeria.models.directory.DirectoryModel;
import com.peterstev.lawonlinereportnigeria.offline.OfflineConstants;
import com.peterstev.lawonlinereportnigeria.offline.dao_interfaces.DirectoryDao;
import com.peterstev.lawonlinereportnigeria.offline.schemas.DirectorySchema;
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
 * Created by Peterstev on 31/05/2018.
 * for LawOnlineReport
 */
public class DirectoryFragment extends BaseFragment implements DirectoryAdapter.DirectoryClickListener {

    private Context context;
    private DirectoryClickListener listener;
    private DirectoryAdapter adapter;
    private List<DirectoryModel> directoryModelList;
    private DirectoryDao directoryDao;
    private SpinKitView progress, progress_secondary;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (DirectoryClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement DirectoryClickListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpViews(getActivity(), "Law Online Directory");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getContext();
        return inflater.inflate(R.layout.default_recycler_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        directoryModelList = new ArrayList<>();
        progress = view.findViewById(R.id.default_progress_bar);
        progress_secondary = view.findViewById(R.id.default_progress_bar_2);

        RecyclerView recyclerView = view.findViewById(R.id.default_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        directoryDao = getAppDatabase(OfflineConstants.DIRECTORY_TABLE_NAME).getDirectoryDao();
        adapter = new DirectoryAdapter(context, directoryDao.getLawOnlineDirectory(), DirectoryFragment.this);
        recyclerView.setAdapter(adapter);

        if (directoryDao.getLawOnlineDirectory().size() > 0 && directoryModelList.size() == 0) {
            lawOnlineDirectory.updateData(true);
        } else {
            lawOnlineDirectory.updateData(false);
        }
    }

    private int retryCount = 0;
    FunctionalInterfaces.UpdateData lawOnlineDirectory = (boolean shouldUpdate) -> {
        if (shouldUpdate) {
            progress_secondary.setVisibility(View.VISIBLE);
            progress.setVisibility(View.GONE);
        } else {
            progress.setVisibility(View.VISIBLE);
            progress_secondary.setVisibility(View.GONE);
        }

        ApiInterface apiInterface = Utils.getRetrofitScalar(context, Utils.BASE_URL);
        Call<String> call = apiInterface.getDirectory(Utils.DIRECTORY_URL);
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
                retryCount++;
                if (retryCount < 3) {
                    lawOnlineDirectory.updateData(true);
                }
            }
        });
    };

    private void processData(Response<String> response) {
        Document document = Jsoup.parse(response.body());
        Elements elements = document.getElementsByClass("hitmag-page")
                .select("a[href]");

        for (Element element : elements) {
            DirectoryModel model = new DirectoryModel();
            model.setPostTitle(element.text().trim().toUpperCase());
            model.setHref(element.attr("href").trim());
            model.setKey(String.valueOf(element.text().charAt(0)).toUpperCase());
            directoryModelList.add(model);
        }

        if (directoryModelList.size() > 0) {
            directoryDao.clearTable();
            adapter.updateAdapter(directoryModelList);

            new Thread(() -> {
                for (int x = 0; x < directoryModelList.size(); x++) {
                    DirectoryModel model = directoryModelList.get(x);
                    DirectorySchema schema = new DirectorySchema();
                    schema.setHref(model.getHref());
                    schema.setPostTitle(model.getPostTitle());
                    schema.setKey(model.getKey());
                    directoryDao.insert(schema);
                }
            }).start();
        }
        progress.setVisibility(View.GONE);
        progress_secondary.setVisibility(View.GONE);
    }

    @Override
    public void onDirectoryItemClick(DirectoryModel directory) {
        listener.onDirectoryItemClick(directory);
    }

    public interface DirectoryClickListener {
        void onDirectoryItemClick(DirectoryModel directory);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String queryText) {
        if (queryText != null && queryText.length() > 0 && adapter != null) {
            adapter.getFilter().filter(queryText.toUpperCase());
        }
        return false;
    }
}
