package com.emarsys.predict.shop.adapters;

import com.emarsys.predict.shop.R;
import com.emarsys.predict.shop.shopitems.Item;
import com.emarsys.predict.shop.util.DownloadImageTask;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class RecommendedAdapter extends RecyclerView.Adapter<RecommendedAdapter.ViewHolder> {

    private final List<Item> items;
    private OnItemClickListener listener;

    public RecommendedAdapter(List<Item> data) {
        this.items = new ArrayList<>();
        items.addAll(data);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View row = inflater.inflate(R.layout.recommended_row, parent, false);

        ImageView image = (ImageView) row.findViewById(R.id.imageView);
        TextView title = (TextView) row.findViewById(R.id.title);
        title.setMaxWidth(160);

        return new ViewHolder(row, image, title);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        new DownloadImageTask(holder.image, 120, 120).execute(items.get(position).getImage());
        holder.title.setText(items.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        public final ImageView image;
        public final TextView title;

        public ViewHolder(View v, ImageView image, TextView title) {
            super(v);
            this.image = image;
            this.title = title;
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (listener != null) {
                listener.onItemClick(items.get(position));
            }
        }
    }

    public void setOnItemClickListener(OnItemClickListener l) {
        this.listener = l;
    }

    public interface OnItemClickListener {

        void onItemClick(Item item);
    }

    public void clear() {
        items.clear();
    }

    public void setData(List<Item> resultData) {
        items.addAll(resultData);
    }

}
