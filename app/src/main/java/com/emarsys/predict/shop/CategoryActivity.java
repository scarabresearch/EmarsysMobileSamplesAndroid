package com.emarsys.predict.shop;


import com.emarsys.predict.shop.adapters.ItemAdapter;
import com.emarsys.predict.shop.adapters.RecommendedAdapter;
import com.emarsys.predict.shop.data.DataSource;
import com.emarsys.predict.shop.recommended.RecommendCompletionHandler;
import com.emarsys.predict.shop.recommended.RecommendManager;
import com.emarsys.predict.shop.shopitems.Item;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private ListView listView;
    private ItemAdapter adapter;
    private String category;
    private RecommendedAdapter recommendedAdapter;
    private RecyclerView recyclerView;
    private String searchTerm;
    private RecommendManager recommendManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        listView = (ListView) findViewById(R.id.itemsListView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Item item = (Item) listView.getItemAtPosition(i);
                Intent intent = new Intent(CategoryActivity.this, ItemDetailActivity.class);
                intent.putExtra("item", item);
                startActivity(intent);
            }
        });

        adapter = new ItemAdapter(this, DataSource.sharedDataSource().getItems());

        listView.setAdapter(adapter);

        recyclerView = (RecyclerView) findViewById(R.id.recommendedListView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);

        List<Item> data = new ArrayList<>();
        recommendedAdapter = new RecommendedAdapter(data);
        recommendedAdapter.setOnItemClickListener(new RecommendedAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Item item) {
                Intent intent = new Intent(CategoryActivity.this, ItemDetailActivity.class);
                intent.putExtra("item", item);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(recommendedAdapter);

        recommendManager = new RecommendManager();

    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = getIntent();
        this.category = intent.getStringExtra(CategoriesActivity.CATEGORY);

        setTitle(category);

        adapter.clear();
        adapter.addAll(DataSource.sharedDataSource().itemsFromCategory(category));

        sendRecommend();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        SearchManager searchManager = (SearchManager)
                getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchMenuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.
                    getSearchableInfo(getComponentName()));
            searchView.setSubmitButtonEnabled(true);
            searchView.setOnQueryTextListener(this);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        adapter.getFilter().filter(newText);
        this.searchTerm = newText;
        sendRecommend();

        return true;
    }

    private void sendRecommend() {
        recommendedAdapter.clear();
        recommendedAdapter.notifyDataSetChanged();
        recyclerView.invalidate();

        RecommendCompletionHandler handler = new RecommendCompletionHandler() {
            @Override
            public void onRecommendedRequestComplete(final List<Item> resultData) {
                recommendedAdapter.setData(resultData);
                recommendedAdapter.notifyDataSetChanged();
                recyclerView.invalidate();
            }
        };

        List<String> excludedItems = null;

        if (listView.getCount() > 0) {
            if (listView.getCount() > 1) {
                excludedItems = new ArrayList<>();
                for (int i = 1; i > listView.getCount(); i++) {
                    excludedItems.add(((Item) listView.getItemAtPosition(i)).getItemID());
                }
            }

            recommendManager.sendRelatedRecommend(null,
                    searchTerm,
                    ((Item) listView.getItemAtPosition(0)).getItemID(),
                    excludedItems,
                    handler);

        } else {
            recommendManager.sendCategoryRecommend(searchTerm, category, handler);
        }
    }
}