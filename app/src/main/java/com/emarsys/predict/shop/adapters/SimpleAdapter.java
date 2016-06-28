package com.emarsys.predict.shop.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

public class SimpleAdapter extends ArrayAdapter<String> {

    private final ArrayList<String> items = new ArrayList<>();

    public SimpleAdapter(Context context,
                         List<String> objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
        items.addAll(objects);
    }

    @Override
    public String getItem(int position) {
        return items.get(position);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

}
