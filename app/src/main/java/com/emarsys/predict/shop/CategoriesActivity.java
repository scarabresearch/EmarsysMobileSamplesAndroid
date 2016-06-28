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
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoriesActivity extends AppCompatActivity {

    public static final String CATEGORY = "category_info";

    private ListView listView;
    private RecommendedListAdapter recommendedListAdapter;
    private RecommendManager recommendManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        setTitle(getString(R.string.categories));

        listView = (ListView) findViewById(R.id.categoriesListView);

        final SimpleAdapter adapter = new SimpleAdapter(this,
                DataSource.sharedDataSource().getCategories());

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String category = (String) listView.getItemAtPosition(i);
                Intent intent = new Intent(CategoriesActivity.this, CategoryActivity.class);
                intent.putExtra(CATEGORY, category);
                startActivity(intent);

            }
        });

        Map<String, List<Item>> data = new HashMap<>();
        ArrayList<String> categories = new ArrayList<>();
        recommendedListAdapter = new RecommendedListAdapter(this, categories, data);
        recommendedListAdapter.setOnItemClickListener(new RecommendedAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Item item) {
                Intent intent = new Intent(CategoriesActivity.this, ItemDetailActivity.class);
                intent.putExtra("item", item);
                startActivity(intent);
            }
        });
        ListView recommendedListView = (ListView) findViewById(R.id.recommendedListView);
        recommendedListView.setAdapter(recommendedListAdapter);

        recommendManager = new RecommendManager();
    }

    @Override
    protected void onStart() {
        super.onStart();

        sendRecommend();
    }

    private void sendRecommend() {
        recommendManager.sendHomeRecommend(new RecommendListCompletionHandler() {
            @Override
            public void onRecommendedRequestComplete(final List<String> categories, final HashMap<String, List<Item>> data) {
                recommendedListAdapter.clear();
                recommendedListAdapter.addAll(categories, data);
                recommendedListAdapter.notifyDataSetChanged();
            }
        });
    }

}
