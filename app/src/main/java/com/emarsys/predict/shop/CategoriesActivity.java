package com.emarsys.predict.shop;


import com.emarsys.predict.shop.adapters.RecommendedAdapter;
import com.emarsys.predict.shop.adapters.RecommendedListAdapter;
import com.emarsys.predict.shop.adapters.SimpleAdapter;
import com.emarsys.predict.shop.data.DataSource;
import com.emarsys.predict.shop.recommended.RecommendListCompletionHandler;
import com.emarsys.predict.shop.recommended.RecommendManager;
import com.emarsys.predict.shop.shopitems.Item;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoriesActivity extends Fragment {

    public static final String CATEGORY = "category_info";

    private ListView listView;
    private RecommendedListAdapter recommendedListAdapter;
    private RecommendManager recommendManager;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_categories,
                container, false);

        //view.setTitle(getString(R.string.categories));
        getActivity().setTitle(getString(R.string.categories));

        listView = (ListView) view.findViewById(R.id.categoriesListView);

        final SimpleAdapter adapter = new SimpleAdapter(getContext(),
                DataSource.sharedDataSource().getCategories());

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String category = (String) listView.getItemAtPosition(i);
                Intent intent = new Intent(getContext(), CategoryActivity.class);
                intent.putExtra(CATEGORY, category);
                startActivity(intent);

            }
        });

        Map<String, List<Item>> data = new HashMap<>();
        ArrayList<String> categories = new ArrayList<>();
        recommendedListAdapter = new RecommendedListAdapter(getContext(), categories, data);
        recommendedListAdapter.setOnItemClickListener(new RecommendedAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Item item) {
                Intent intent = new Intent(getContext(), ItemDetailActivity.class);
                intent.putExtra("item", item);
                startActivity(intent);
            }
        });
        ListView recommendedListView = (ListView) view.findViewById(R.id.recommendedListView);
        recommendedListView.setAdapter(recommendedListAdapter);

        recommendManager = new RecommendManager();

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();

        sendRecommend();
    }

    private void sendRecommend() {
        recommendManager.sendHomeRecommend(new RecommendListCompletionHandler() {
            @Override
            public void onRecommendedRequestComplete(final List<String> categories,
                                                     final HashMap<String, List<Item>> data) {
                recommendedListAdapter.clear();
                recommendedListAdapter.addAll(categories, data);
                recommendedListAdapter.notifyDataSetChanged();
            }
        });
    }

}
