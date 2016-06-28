package com.emarsys.predict.shop.adapters;

import com.emarsys.predict.shop.R;
import com.emarsys.predict.shop.shopitems.Item;
import com.emarsys.predict.shop.util.DownloadImageTask;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Formatter;

public class ItemAdapter extends ArrayAdapter<Item> {

    private final Context context;
    private final ArrayList<Item> items;
    private ArrayList<Item> filteredList;
    private ItemFilter itemFilter;

    public ItemAdapter(Context context, ArrayList<Item> data) {
        super(context, R.layout.row);

        this.context = context;
        this.items = new ArrayList<>();
        this.filteredList = new ArrayList<>();
        items.addAll(data);
        filteredList.addAll(data);

        getFilter();
    }

    @Override
    public int getCount() {
        return filteredList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View row = inflater.inflate(R.layout.row, parent, false);

        ImageView image = (ImageView) row.findViewById(R.id.imageView);
        TextView title = (TextView) row.findViewById(R.id.title);
        TextView price = (TextView) row.findViewById(R.id.price);

        new DownloadImageTask(image, 200, 250).execute(filteredList.get(position).getImage());
        title.setText(filteredList.get(position).getTitle());
        Formatter formatter = new Formatter();
        price.setText(formatter.format("$%.1f", filteredList.get(position).getPrice()).toString());

        return row;
    }

    @Override
    public Item getItem(int position) {
        return filteredList.get(position);
    }

    @Override
    public Filter getFilter() {
        if (itemFilter == null) {
            itemFilter = new ItemFilter();
        }
        return itemFilter;
    }

    @Override
    public void clear() {
        items.clear();
        filteredList.clear();
    }

    @Override
    public void addAll(Collection<? extends Item> collection) {
        items.addAll(collection);
        filteredList.addAll(collection);
    }

    private class ItemFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults filterResults = new FilterResults();
            if (charSequence != null && charSequence.length() > 0) {
                ArrayList<Item> tempList = new ArrayList<>();

                // search content in friend list
                for (Item item : items) {
                    if (item.getTitle()
                            .toLowerCase()
                            .contains(charSequence.toString().toLowerCase())) {
                        tempList.add(item);
                    }
                }

                filterResults.count = tempList.size();
                filterResults.values = tempList;
            } else {
                filterResults.count = items.size();
                filterResults.values = items;
            }

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            filteredList = (ArrayList<Item>) filterResults.values;
            notifyDataSetChanged();
        }
    }
}
