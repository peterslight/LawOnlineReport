package com.peterstev.lawonlinereportnigeria.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.AsyncTask;

import com.peterstev.lawonlinereportnigeria.models.home.HomeModel;
import com.peterstev.lawonlinereportnigeria.offline.AppDatabase;
import com.peterstev.lawonlinereportnigeria.offline.dao_interfaces.HomeDAO;
import com.peterstev.lawonlinereportnigeria.offline.schemas.HomeSchema;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Peterstev on 11/05/2018.
 * for LawOnlineReport
 */
public class HomeViewModel extends ViewModel {
    private MutableLiveData<List<HomeModel>> homeDAOMutableLiveData;

    public LiveData<List<HomeModel>> getHomeList(Context context, String tableNeme) {
        if (homeDAOMutableLiveData == null) {
            homeDAOMutableLiveData = new MutableLiveData<>();
            loadHomeData(context, tableNeme);
        }
        return homeDAOMutableLiveData;
    }

    private void loadHomeData(Context context, String tableNeme) {
        List<HomeModel> list = new ArrayList<>();
        AsyncTask.execute(() -> {
            List<HomeModel> homeSchemaList = Room.databaseBuilder(context, AppDatabase.class, tableNeme)
                    .build().getHomeDAO().getHomeGlossary();
            list.addAll(homeSchemaList);
        });
        homeDAOMutableLiveData.setValue(list);
    }

    //        HomeViewModel viewModel = ViewModelProviders.of(this)
//                .get(HomeViewModel.class);
//        viewModel.getHomeList(context, OfflineConstants.HOME_TABLE_NAME)
//                .observe(this, homeDaoList -> {
//                    assert homeDaoList != null;
//                    if (homeDaoList.size() > 0 && homeModelList.size() == 0) {
//                        adapter = new HomeAdapter(context, homeDaoList, HomeFragment.this);
//                        recyclerView.setAdapter(adapter);
//                        getHomeData.updateData(true);
//                        AsyncTask.execute(() -> homeDAO = getAppDatabase(OfflineConstants.HOME_TABLE_NAME).getHomeDAO());
//                    } else {
//                        getHomeData.updateData(false);
//                    }
//                });
}
