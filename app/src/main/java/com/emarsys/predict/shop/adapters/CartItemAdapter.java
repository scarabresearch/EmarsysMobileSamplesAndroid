package com.emarsys.predict.shop.adapters;

import com.emarsys.predict.shop.R;
import com.emarsys.predict.shop.shopitems.ShopCartItem;
import com.emarsys.predict.shop.util.DownloadImageTask;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CartItemAdapter extends ArrayAdapter<ShopCartItem> {

    private final Context context;
    private final List<ShopCartItem> items;

    public CartItemAdapter(Context context, List<ShopCartItem> data) {
        super(context, R.layout.row);

        this.context = context;
        this.items = new ArrayList<>();
        items.addAll(data);

        getFilter();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View row = inflater.inflate(R.layout.row, parent, false);

        ImageView image = (ImageView) row.findViewById(R.id.imageView);
        TextView title = (TextView) row.findViewById(R.id.title);
        TextView price = (TextView) row.findViewById(R.id.price);

        ShopCartItem shopCartItem = items.get(position);
        new DownloadImageTask(image, 200, 250).execute(shopCartItem.getItem().getImage());
        title.setText(shopCartItem.getItem().getTitle());
        price.setText(String.format("%d", shopCartItem.getCount()));

        return row;
    }

    @Override
    public ShopCartItem getItem(int position) {
        return items.get(position);
    }

    @Override
    public void clear() {
        items.clear();
    }

    @Override
    public void addAll(Collection<? extends ShopCartItem> collection) {
        items.addAll(collection);
    }

}
