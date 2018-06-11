package com.peterstev.lawonlinereportnigeria.adapters.directory;

import android.widget.Filter;

import com.peterstev.lawonlinereportnigeria.models.directory.DirectoryModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Peterstev on 31/05/2018.
 * for LawOnlineReport
 */
public class DirectoryFilter extends Filter {
    private List<DirectoryModel> modelList;
    private DirectoryAdapter adapter;

    DirectoryFilter(List<DirectoryModel> modelList, DirectoryAdapter adapter) {

        this.modelList = modelList;
        this.adapter = adapter;
    }

    @Override
    protected FilterResults performFiltering(CharSequence text) {
        FilterResults results = new FilterResults();

        if (text != null && text.length() > 0) {

            ArrayList<DirectoryModel> filterList = new ArrayList<>();
            for (int x = 0; x < modelList.size(); x++) {
                if (modelList.get(x).getPostTitle().toUpperCase().contains(text.toString().toUpperCase())) {
                    filterList.add(modelList.get(x));
                }
            }
            results.count = filterList.size();
            results.values = filterList;

        } else {
            results.count = modelList.size();
            results.values = modelList;
        }

        return results;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
        adapter.modelList = (ArrayList<DirectoryModel>) filterResults.values;
        adapter.notifyDataSetChanged();
    }
}
