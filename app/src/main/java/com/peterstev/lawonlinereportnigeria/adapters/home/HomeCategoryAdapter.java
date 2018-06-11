package com.peterstev.lawonlinereportnigeria.adapters.home;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.peterstev.lawonlinereportnigeria.R;
import com.peterstev.lawonlinereportnigeria.models.home.CategoryModel;

import java.util.ArrayList;

/**
 * Created by Peterstev on 4/14/2018.
 * for LawOnlineReport
 */

public class HomeCategoryAdapter extends RecyclerView.Adapter<HomeCategoryAdapter.ViewHolder> implements Filterable {


    private Context context;
    ArrayList<CategoryModel> categories;
    private ItemClickListener listener;
    private HomeCategoryFilter filter;

    private int totalItems;
    private int lastVisibleItemsPosition;
    private final int itemThreshold = 5;
    private boolean isLoading;

    public HomeCategoryAdapter(Context context, ArrayList<CategoryModel> categories, RecyclerView recyclerView,  ItemClickListener listener) {
        this.context = context;
        this.categories = categories;
        this.listener = listener;

        final LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItems = manager.getItemCount();
                lastVisibleItemsPosition = manager.findLastVisibleItemPosition();

                if (!isLoading && totalItems <= (lastVisibleItemsPosition + itemThreshold)) {
                    if (listener != null) {
                        listener.loadMore();
                        isLoading = true;
                    }
                }
            }
        });
    }


    public void stopLoading() {
        isLoading = false;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.default_category_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setText(categories.get(position));
    }

    @Override
    public int getItemCount() {
        return categories == null ? 0 : categories.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new HomeCategoryFilter(categories, this);
        }
        return filter;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title, author, excerpt;
        private Button readMore;

        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.group_tv_title);
            excerpt = itemView.findViewById(R.id.group_tv_excerpt);
            author = itemView.findViewById(R.id.group_tv_author);
            readMore = itemView.findViewById(R.id.group_bt_read_more);
            readMore.setOnClickListener(view -> listener.onItemClick(getLayoutPosition(), categories));
        }

        private void setText(CategoryModel category) {
            title.setText(category.getTitle());
            excerpt.setText(category.getExcerpt());
            author.setText(category.getAuthor());
        }
    }

    public interface ItemClickListener {
        void loadMore();

        void onItemClick(int position, ArrayList<CategoryModel> categoryList);
    }
}
