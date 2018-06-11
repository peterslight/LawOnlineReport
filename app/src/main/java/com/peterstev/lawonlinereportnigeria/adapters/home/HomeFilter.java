package com.peterstev.lawonlinereportnigeria.adapters.home;

import android.widget.Filter;

import com.peterstev.lawonlinereportnigeria.models.home.HomeModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Peterstev on 02/05/2018.
 * for LawOnlineReport
 */

public class HomeFilter extends Filter {

    private List<HomeModel> homeModels;
    private HomeAdapter adapter;

    HomeFilter(List<HomeModel> homeModels, HomeAdapter adapter) {
        this.homeModels = homeModels;
        this.adapter = adapter;
    }

    @Override
    protected FilterResults performFiltering(CharSequence text) {
        FilterResults results = new FilterResults();

        if (text != null && text.length() > 0) {

            ArrayList<HomeModel> filterList = new ArrayList<>();
            for (int x = 0; x < homeModels.size(); x++) {
                if (homeModels.get(x).getPostTitle().toLowerCase().contains(text.toString().toLowerCase())) {
                    filterList.add(homeModels.get(x));
                }
            }
            results.count = filterList.size();
            results.values = filterList;

        } else {
            results.count = homeModels.size();
            results.values = homeModels;
        }

        return results;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
        adapter.homeModelsList = (ArrayList<HomeModel>) filterResults.values;
        adapter.notifyDataSetChanged();
    }
}