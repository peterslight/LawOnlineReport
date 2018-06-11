package com.peterstev.lawonlinereportnigeria.adapters.home;

import android.widget.Filter;

import com.peterstev.lawonlinereportnigeria.models.home.CategoryModel;

import java.util.ArrayList;

/**
 * Created by Peterstev on 02/05/2018.
 * for LawOnlineReport
 */

public class HomeCategoryFilter extends Filter {

    private ArrayList<CategoryModel> categoryModel;
    private HomeCategoryAdapter adapter;

    HomeCategoryFilter(ArrayList<CategoryModel> categoryModel, HomeCategoryAdapter adapter) {

        this.categoryModel = categoryModel;
        this.adapter = adapter;
    }

    @Override
    protected FilterResults performFiltering(CharSequence text) {
        FilterResults results = new FilterResults();

        if (text != null && text.length() > 0) {

            ArrayList<CategoryModel> filterList = new ArrayList<>();
            for (int x = 0; x < categoryModel.size(); x++) {
                if (categoryModel.get(x).getTitle().toLowerCase().contains(text.toString().toLowerCase()) ||
                        categoryModel.get(x).getExcerpt().toLowerCase().contains(text.toString().toLowerCase())) {
                    filterList.add(categoryModel.get(x));
                }
            }
            results.count = filterList.size();
            results.values = filterList;

        } else {
            results.count = categoryModel.size();
            results.values = categoryModel;
        }

        return results;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
        adapter.categories = (ArrayList<CategoryModel>) filterResults.values;
        adapter.notifyDataSetChanged();
    }
}
