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
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends Fragment implements SearchView.OnQueryTextListener {

    private ListView listView;
    private ItemAdapter adapter;
    private RecommendedAdapter recommendedAdapter;
    private RecyclerView recyclerView;
    private String searchTerm;
    private RecommendManager recommendManager;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_search,
                container, false);

        getActivity().setTitle(getString(R.string.search));

        setHasOptionsMenu(true);

        listView = (ListView) view.findViewById(R.id.itemsListView);

        adapter = new ItemAdapter(getActivity(), DataSource.sharedDataSource().getItems());

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Item item = (Item) listView.getItemAtPosition(i);
                Intent intent = new Intent(getActivity(), ItemDetailActivity.class);
                intent.putExtra("item", item);
                startActivity(intent);
            }
        });

        recyclerView = (RecyclerView) view.findViewById(R.id.recommendedListView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);

        List<Item> data = new ArrayList<>();
        recommendedAdapter = new RecommendedAdapter(data);
        recommendedAdapter.setOnItemClickListener(new RecommendedAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Item item) {
                Intent intent = new Intent(getActivity(), ItemDetailActivity.class);
                intent.putExtra("item", item);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(recommendedAdapter);

        recommendManager = new RecommendManager();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        adapter.clear();
        adapter.addAll(DataSource.sharedDataSource().getItems());

        sendRecommend();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);

        SearchManager searchManager = (SearchManager)
                getActivity().getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchMenuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.
                    getSearchableInfo(getActivity().getComponentName()));
            searchView.setSubmitButtonEnabled(true);
            searchView.setOnQueryTextListener(this);
        }
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

        recommendManager.sendPersonalRecommend(searchTerm, new RecommendCompletionHandler() {
            @Override
            public void onRecommendedRequestComplete(final List<Item> resultData) {
                recommendedAdapter.setData(resultData);
                recommendedAdapter.notifyDataSetChanged();
                recyclerView.invalidate();
            }
        });
    }
}