package com.peterstev.lawonlinereportnigeria.adapters.blog;

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
import com.peterstev.lawonlinereportnigeria.models.blog.BlogModel;
import com.peterstev.lawonlinereportnigeria.offline.schemas.BlogSchema;

import java.util.List;

/**
 * Created by Peterstev on 4/17/2018.
 * for LawOnlineReport
 */

public class BlogAdapter extends RecyclerView.Adapter<BlogAdapter.ViewHolder> implements Filterable {

    private Context context;
    List<BlogSchema> blogModelList;
    private BlogAdapter.ItemClickListener listener;

    private int totalItems;
    private int lastVisibleItemsPosition;
    private final int itemThreshold = 5;
    private boolean isLoading;
    private BlogFilter filter;

    public BlogAdapter(Context context, List<BlogSchema> blogModelList, RecyclerView recyclerView, BlogAdapter.ItemClickListener listener) {
        this.context = context;
        this.blogModelList = blogModelList;
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
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.fragment_blog, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setText(blogModelList.get(position));
    }
    public void updateAdapter(List<BlogSchema> newItem){
        blogModelList.clear();
        blogModelList.addAll(newItem);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return blogModelList == null ? 0 : blogModelList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new BlogFilter(blogModelList, this);
        }
        return filter;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title, author, excerpt;
        private Button readMore;

        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.blog_tv_title);
            excerpt = itemView.findViewById(R.id.blog_tv_excerpt);
            author = itemView.findViewById(R.id.blog_tv_author);
            readMore = itemView.findViewById(R.id.blog_bt_read_more);
            readMore.setOnClickListener(view -> listener.onArticleItemClick(
                    blogModelList.get(getLayoutPosition()).getHref()
            ));
        }

        private void setText(BlogSchema blogModel) {
            title.setText(blogModel.getTitle());
            excerpt.setText(blogModel.getExcerpt());
            author.setText(blogModel.getAuthor());
        }
    }


    public interface ItemClickListener {
        void loadMore();

        void onArticleItemClick(String hrefLink);
    }
}
