package com.peterstev.lawonlinereportnigeria.adapters.court;

import android.widget.Filter;

import com.peterstev.lawonlinereportnigeria.models.court_rules.CourtRulesModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Peterstev on 02/05/2018.
 * for LawOnlineReport
 */

public class CourtFilter extends Filter {

    private List<CourtRulesModel> courtModel;
    private CourtRulesAdapter adapter;

    CourtFilter(List<CourtRulesModel> courtModel, CourtRulesAdapter adapter) {

        this.courtModel = courtModel;
        this.adapter = adapter;
    }

    @Override
    protected FilterResults performFiltering(CharSequence text) {
        FilterResults results = new FilterResults();

        if (text != null && text.length() > 0) {

            ArrayList<CourtRulesModel> filterList = new ArrayList<>();
            for (int x = 0; x < courtModel.size(); x++) {
                if (courtModel.get(x).getPostTitle().toLowerCase().contains(text.toString().toLowerCase())) {
                    filterList.add(courtModel.get(x));
                }
            }
            results.count = filterList.size();
            results.values = filterList;

        } else {
            results.count = courtModel.size();
            results.values = courtModel;
        }

        return results;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
        adapter.courtModel = (ArrayList<CourtRulesModel>) filterResults.values;
        adapter.notifyDataSetChanged();
    }
}
