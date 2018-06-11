package com.peterstev.lawonlinereportnigeria.adapters.court;

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
import com.peterstev.lawonlinereportnigeria.models.court_rules.CourtRulesModel;

import java.util.List;

/**
 * Created by Peterstev on 26/04/2018.
 * for LawOnlineReport
 */

public class CourtRulesAdapter extends RecyclerView.Adapter<CourtRulesAdapter.ViewHolder> implements Filterable {

    private Context context;
    List<CourtRulesModel> courtModel;
    private ColorGenerator generator;
    private TextDrawable.IBuilder builder;
    private CourtRulesClickListener listener;
    private int visibleItemThreshold = 5;
    private int lastVisibleTtemsPosition;
    private int totalItems;
    private boolean isLoading;
    private CourtFilter filter;

    public CourtRulesAdapter(Context context, RecyclerView recyclerView, List<CourtRulesModel> models, CourtRulesClickListener listener) {

        this.context = context;
        this.courtModel = models;
        this.listener = listener;

        generator = ColorGenerator.MATERIAL;
        builder = TextDrawable.builder()
                .beginConfig()
                .withBorder(1)
                .endConfig()
                .roundRect(5);

        recyclerView.addOnScrollListener(scrollListener);
    }

    RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            final LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
            totalItems = manager.getItemCount();
            lastVisibleTtemsPosition = manager.findLastVisibleItemPosition();

            if (!isLoading && totalItems <= (lastVisibleTtemsPosition + visibleItemThreshold)) {
                if (listener != null) {
                    listener.loadMore();
                    isLoading = true;
                }
            }
        }
    };

    public void stopLoading() {
        isLoading = false;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.fragment_court_rules, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setText(courtModel.get(position));
    }

    @Override
    public int getItemCount() {
        return courtModel == null ? 0 : courtModel.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new CourtFilter(courtModel, this);
        }
        return filter;
    }

    public void updateAdapter(List<CourtRulesModel> newItem){
        courtModel.clear();
        courtModel.addAll(newItem);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textView;
        private ImageView imageView;

        ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.court_rules_tv_title);
            imageView = itemView.findViewById(R.id.court_rules_image_view);
            itemView.setOnClickListener(view -> listener.onCourtItemClick(courtModel.get(getLayoutPosition()).getHref()));
        }

        private void setText(CourtRulesModel model) {
            TextDrawable drawable = builder
                    .build(model.getKey(), generator.getRandomColor());
            imageView.setImageDrawable(drawable);
            textView.setText(model.getPostTitle().toLowerCase());
        }
    }

    public interface CourtRulesClickListener {
        void onCourtItemClick(String url);

        void loadMore();
    }

}
