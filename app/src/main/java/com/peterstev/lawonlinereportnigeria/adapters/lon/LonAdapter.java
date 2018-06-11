package com.peterstev.lawonlinereportnigeria.adapters.lon;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.peterstev.lawonlinereportnigeria.R;
import com.peterstev.lawonlinereportnigeria.models.lon.LonModel;

import java.util.List;

/**
 * Created by Peterstev on 25/04/2018.
 * for LawOnlineReport
 */

public class LonAdapter extends RecyclerView.Adapter<LonAdapter.ViewHolder> implements Filterable {

    private Context context;
    List<LonModel> lonModels;
    private ColorGenerator generator;
    private TextDrawable.IBuilder builder;
    private LonClickListener listener;
    private int visibleItemThreshold = 10;
    private int lastVisibleTtemsPosition;
    private int totalItems;
    private boolean isLoading;
    private LonFilter filter;

    public LonAdapter(Context context, RecyclerView recyclerView, List<LonModel> models, LonClickListener listener) {

        this.context = context;
        this.lonModels = models;
        this.listener = listener;

        generator = ColorGenerator.MATERIAL;
        builder = TextDrawable.builder()
                .beginConfig()
                .withBorder(1)
                .endConfig()
                .round();

        final LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItems = manager.getItemCount();
                lastVisibleTtemsPosition = manager.findLastVisibleItemPosition();

                if (!isLoading && totalItems <= (lastVisibleTtemsPosition + visibleItemThreshold)) {
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
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.fragment_lotf, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setText(lonModels.get(position));
    }
    public void updateAdapter(List<LonModel> newItem){
        lonModels.clear();
        lonModels.addAll(newItem);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return lonModels == null ? 0 : lonModels.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null){
            filter = new LonFilter(lonModels, this);
        }
        return filter;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textView;
        private ImageView imageView;

        ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.lotf_tv_title);
            imageView = itemView.findViewById(R.id.lotf_image_view);
            itemView.setOnClickListener(view -> listener.onLonItemClick(getLayoutPosition(), lonModels));
        }

        private void setText(LonModel model) {
            TextDrawable drawable = builder
                    .build(model.getKey(), generator.getRandomColor());
            imageView.setImageDrawable(drawable);
            textView.setText(model.getPostTitle().toLowerCase());
        }
    }

    public interface LonClickListener {
        void onLonItemClick(int position, List<LonModel> categoryList);

        void loadMore();
    }
}