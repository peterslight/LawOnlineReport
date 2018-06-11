package com.peterstev.lawonlinereportnigeria.adapters.home;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.peterstev.lawonlinereportnigeria.R;
import com.peterstev.lawonlinereportnigeria.models.home.HomeModel;
import com.peterstev.lawonlinereportnigeria.models.home.wp_json_api.LawModel;

import java.util.List;

import android.widget.Filter;

/**
 * Created by Peterstev on 4/11/2018.
 * for LawOnlineReport
 */

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeVH> implements Filterable {

    private Context context;
    List<HomeModel> homeModelsList;
    private ItemClickListener clickListener;
    private ColorGenerator generator;
    private TextDrawable.IBuilder builder;
    private HomeFilter filter;

    public HomeAdapter(Context context, List<HomeModel> homeModelsList, ItemClickListener clickListener) {

        this.context = context;
        this.homeModelsList = homeModelsList;
        this.clickListener = clickListener;


        generator = ColorGenerator.MATERIAL;
        builder = TextDrawable.builder()
                .beginConfig()
                .withBorder(1)
                .endConfig()
                .roundRect(5);
    }


    @Override
    public HomeVH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HomeVH(LayoutInflater.from(context)
                .inflate(R.layout.fragment_home, parent, false));
    }

    @Override
    public void onBindViewHolder(HomeVH holder, int position) {
        holder.setText(homeModelsList.get(position));
    }

    public void updateAdapter(List<HomeModel> newList){
        homeModelsList.clear();
        homeModelsList.addAll(newList);
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return homeModelsList == null ? 0 : homeModelsList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new HomeFilter(homeModelsList, this);
        }
        return filter;
    }

    class HomeVH extends RecyclerView.ViewHolder {

        private TextView textView;
        private ImageView imageView;

        HomeVH(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.home_tv_title);
            imageView = itemView.findViewById(R.id.home_image_view);
            itemView.setOnClickListener(view -> clickListener.onItemClick(getLayoutPosition(), homeModelsList));
        }

        private void setText(HomeModel model) {
            TextDrawable drawable = builder
                    .build(model.getKey().toUpperCase(), generator.getRandomColor());
            imageView.setImageDrawable(drawable);
            textView.setText(model.getPostTitle());
        }
    }


    public interface ItemClickListener {
        void onItemClick(int position, List<HomeModel> categoryList);
    }
}
