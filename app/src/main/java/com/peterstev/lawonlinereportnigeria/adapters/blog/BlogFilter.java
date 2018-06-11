package com.peterstev.lawonlinereportnigeria.adapters.blog;

import android.widget.Filter;

import com.peterstev.lawonlinereportnigeria.models.blog.BlogModel;
import com.peterstev.lawonlinereportnigeria.offline.schemas.BlogSchema;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Peterstev on 02/05/2018.
 * for LawOnlineReport
 */

public class BlogFilter extends Filter {

    private List<BlogSchema> blogModel;
    private BlogAdapter adapter;

    BlogFilter(List<BlogSchema> blogModel, BlogAdapter adapter) {

        this.blogModel = blogModel;
        this.adapter = adapter;
    }

    @Override
    protected FilterResults performFiltering(CharSequence text) {
        FilterResults results = new FilterResults();

        if (text != null && text.length() > 0) {

            ArrayList<BlogModel> filterList = new ArrayList<>();
            for (int x = 0; x < blogModel.size(); x++) {
                if (blogModel.get(x).getTitle().toLowerCase().contains(text.toString().toLowerCase()) ||
                        blogModel.get(x).getExcerpt().toLowerCase().contains(text.toString().toLowerCase())) {
                    filterList.add(blogModel.get(x));
                }
            }
            results.count = filterList.size();
            results.values = filterList;

        } else {
            results.count = blogModel.size();
            results.values = blogModel;
        }

        return results;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
        adapter.blogModelList = (ArrayList<BlogSchema>) filterResults.values;
        adapter.notifyDataSetChanged();
    }

}
