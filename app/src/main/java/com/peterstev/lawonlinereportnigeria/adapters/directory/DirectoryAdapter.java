package com.peterstev.lawonlinereportnigeria.adapters.directory;

import android.content.Context;
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
import com.peterstev.lawonlinereportnigeria.adapters.lon.LonFilter;
import com.peterstev.lawonlinereportnigeria.models.court_rules.CourtRulesModel;
import com.peterstev.lawonlinereportnigeria.models.directory.DirectoryModel;

import java.util.List;

/**
 * Created by Peterstev on 31/05/2018.
 * for LawOnlineReport
 */
public class DirectoryAdapter extends RecyclerView.Adapter<DirectoryAdapter.ViewHolder> implements Filterable {

    private Context context;
    List<DirectoryModel> modelList;
    private ColorGenerator generator;
    private TextDrawable.IBuilder builder;
    private DirectoryClickListener listener;
    private DirectoryFilter filter;

    public DirectoryAdapter(Context context, List<DirectoryModel> modelList, DirectoryClickListener listener) {

        this.context = context;
        this.modelList = modelList;
        this.listener = listener;

        generator = ColorGenerator.MATERIAL;
        builder = TextDrawable.builder()
                .beginConfig()
                .withBorder(1)
                .endConfig()
                .round();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.fragment_lotf, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setText(modelList.get(position));
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public void updateAdapter(List<DirectoryModel> newItem) {
        modelList.clear();
        modelList.addAll(newItem);
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new DirectoryFilter(modelList, this);
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
            itemView.setOnClickListener(
                    view -> listener.onDirectoryItemClick(modelList.get(getLayoutPosition())
                    )
            );
        }

        private void setText(DirectoryModel model) {
            TextDrawable drawable = builder
                    .build(model.getKey(), generator.getRandomColor());
            imageView.setImageDrawable(drawable);
            textView.setText(model.getPostTitle().toUpperCase());
        }
    }

    public interface DirectoryClickListener {
        void onDirectoryItemClick(DirectoryModel directory);
    }
}
