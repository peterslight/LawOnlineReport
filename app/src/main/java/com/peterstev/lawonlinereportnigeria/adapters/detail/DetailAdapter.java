package com.peterstev.lawonlinereportnigeria.adapters.detail;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.peterstev.lawonlinereportnigeria.R;
import com.peterstev.lawonlinereportnigeria.models.detail.DetailModel;
import com.peterstev.lawonlinereportnigeria.models.detail.RelatedPosts;
import com.peterstev.lawonlinereportnigeria.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Peterstev on 4/16/2018.
 * for LawOnlineReport
 */

public class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.ContentViewHolder> {

    private Context context;
    private List<DetailModel> detailModelList;
    private ItemClickListener listener;

    public DetailAdapter(Context context, List<DetailModel> detailModelList, ItemClickListener listener) {

        this.context = context;
        this.detailModelList = detailModelList;
        this.listener = listener;
    }


    @Override
    public DetailAdapter.ContentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ContentViewHolder(LayoutInflater.from(context).inflate(R.layout.fragment_post_detail_item_1, parent, false));

    }

    @Override
    public void onBindViewHolder(DetailAdapter.ContentViewHolder holder, int position) {
        holder.setContent(detailModelList.get(position), detailModelList.get(position).getRelatedPosts());
    }

    @Override
    public int getItemCount() {
        return detailModelList == null ? 0 : detailModelList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    class ContentViewHolder extends RecyclerView.ViewHolder {
        private TextView title, readCount, content;
        private Button readFullCase;
        private LinearLayout linearLayout;


        private ContentViewHolder(View itemView) {
            super(itemView);

            linearLayout = itemView.findViewById(R.id.detail_linear_layout);
            title = itemView.findViewById(R.id.detail_tv_title);
            readCount = itemView.findViewById(R.id.detail_tv_read_count);
            content = itemView.findViewById(R.id.detail_tv_content);
            readFullCase = itemView.findViewById(R.id.detail_bt_read_full_case);
        }

        void setContent(DetailModel model, ArrayList<RelatedPosts> relatedPosts) {
            title.setText(model.getTitle());
            content.setText(model.getContent());

            try {
                readCount.setText(Utils.getStandardViewCount(Long.valueOf(model.getViews())));
            } catch (NullPointerException e) {
                readCount.setVisibility(View.GONE);
            } catch (NumberFormatException e) {
                readCount.setVisibility(View.GONE);
            }
            readFullCase.setOnClickListener(
                    view -> listener.onReadMoreClick(model.getFullCaseLink())
            );

            if (model.getContent().toLowerCase().trim().contains("the link below")) {
                readFullCase.setVisibility(View.VISIBLE);
            } else {
                readFullCase.setVisibility(View.GONE);
            }

            for (int x = 0; x < relatedPosts.size(); x++) {
                View view = LayoutInflater.from(context).inflate(R.layout.fragment_detail_text, linearLayout, false);
                TextView relatedPostTitle = view.findViewById(R.id.detail_tv_related_post);
                int y = x;
                relatedPostTitle.setOnClickListener(
                        view1 -> listener.onRelatedPostClick(relatedPosts.get(y).getRelatedHref())
                );
                relatedPostTitle.setText(relatedPosts.get(x).getRelatedTitle());
                linearLayout.addView(relatedPostTitle);
            }
        }
    }

    public interface ItemClickListener {
        void onRelatedPostClick(String postLink);

        void onReadMoreClick(String fullCaseLink);
    }
}
