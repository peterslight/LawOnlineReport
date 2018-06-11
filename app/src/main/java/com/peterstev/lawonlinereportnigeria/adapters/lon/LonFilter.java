package com.peterstev.lawonlinereportnigeria.adapters.lon;

import android.widget.Filter;

import com.peterstev.lawonlinereportnigeria.models.lon.LonModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Peterstev on 02/05/2018.
 * for LawOnlineReport
 */

public class LonFilter extends Filter {

    private List<LonModel> lonModels;
    private LonAdapter adapter;

    LonFilter(List<LonModel> lonModels, LonAdapter adapter) {

        this.lonModels = lonModels;
        this.adapter = adapter;
    }

    @Override
    protected FilterResults performFiltering(CharSequence text) {
        FilterResults results = new FilterResults();

        if (text != null && text.length() > 0) {

            ArrayList<LonModel> filterList = new ArrayList<>();
            for (int x = 0; x < lonModels.size(); x++) {
                if (lonModels.get(x).getPostTitle().toLowerCase().contains(text.toString().toLowerCase())) {
                    filterList.add(lonModels.get(x));
                }
            }
            results.count = filterList.size();
            results.values = filterList;

        } else {
            results.count = lonModels.size();
            results.values = lonModels;
        }

        return results;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
        adapter.lonModels = (ArrayList<LonModel>) filterResults.values;
        adapter.notifyDataSetChanged();
    }
}