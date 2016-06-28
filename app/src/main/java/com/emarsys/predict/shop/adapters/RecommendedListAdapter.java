package com.emarsys.predict.shop.adapters;

import com.emarsys.predict.shop.R;
import com.emarsys.predict.shop.shopitems.Item;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecommendedListAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final List<String> categories;
    private final Map<String, List<Item>> data;
    private RecommendedAdapter.OnItemClickListener listener;

    public RecommendedListAdapter(Context context, List<String> c, Map<String, List<Item>> d) {
        super(context, R.layout.recommended_items_row);

        this.context = context;
        this.categories = new ArrayList<>();
        categories.addAll(c);
        data = new HashMap<>();
        data.putAll(d);

        getFilter();
    }

    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View row = inflater.inflate(R.layout.recommended_items_with_header_row, parent, false);

        String category = categories.get(position);

        TextView header = (TextView) row.findViewById(R.id.header);
        header.setText(category);

        RecyclerView recyclerView = (RecyclerView) row.findViewById(R.id.recommendedListView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setMinimumHeight(160);

        RecommendedAdapter recommendedAdapter = new RecommendedAdapter(data.get(category));
        recommendedAdapter.setOnItemClickListener(listener);
        recyclerView.setAdapter(recommendedAdapter);
        recyclerView.invalidate();

        return row;
    }

    @Override
    public void clear() {
        categories.clear();
        data.clear();
    }

    public void addAll(Collection<? extends String> newCategories,
                       Map<String, List<Item>> newData) {
        categories.addAll(newCategories);
        data.putAll(newData);
    }

    public void setOnItemClickListener(RecommendedAdapter.OnItemClickListener l) {
        this.listener = l;
    }
}
