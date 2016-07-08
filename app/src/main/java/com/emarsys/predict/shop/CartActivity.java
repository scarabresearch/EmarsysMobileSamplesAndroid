package com.emarsys.predict.shop;


import com.emarsys.predict.shop.adapters.CartItemAdapter;
import com.emarsys.predict.shop.adapters.RecommendedAdapter;
import com.emarsys.predict.shop.recommended.RecommendCompletionHandler;
import com.emarsys.predict.shop.recommended.RecommendManager;
import com.emarsys.predict.shop.shopitems.Cart;
import com.emarsys.predict.shop.shopitems.Item;
import com.emarsys.predict.shop.shopitems.ShopCartItem;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends Fragment {

    private ListView listView;
    private CartItemAdapter adapter;
    private RecommendedAdapter recommendedAdapter;
    private RecyclerView recyclerView;
    private Menu menu;
    private RecommendManager recommendManager;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_cart,
                container, false);

        setHasOptionsMenu(true);

        listView = (ListView) view.findViewById(R.id.cartListView);

        adapter = new CartItemAdapter(getContext(), Cart.sharedCart().getShopCartItems());

        listView.setAdapter(adapter);

        recyclerView = (RecyclerView) view.findViewById(R.id.recommendedListView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ShopCartItem item = (ShopCartItem) listView.getItemAtPosition(i);
                Intent intent = new Intent(getContext(), ItemDetailActivity.class);
                intent.putExtra("item", item.getItem());
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                deleteCartItem(i);
                return true;
            }
        });

        List<Item> data = new ArrayList<>();
        recommendedAdapter = new RecommendedAdapter(data);
        recommendedAdapter.setOnItemClickListener(new RecommendedAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Item item) {
                Intent intent = new Intent(getContext(), ItemDetailActivity.class);
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

        getActivity().setTitle(getString(R.string.cart));

        updateCartList();
        sendRecommend();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateMenu();
    }

    private void updateCartList() {
        adapter.clear();
        adapter.addAll(Cart.sharedCart().getShopCartItems());
        adapter.notifyDataSetChanged();
        listView.invalidateViews();
    }

    private void deleteCartItem(int pos) {
        final ShopCartItem item = adapter.getItem(pos);

        AlertDialog.Builder alert = new AlertDialog.Builder(
                getContext());

        alert.setTitle(getString(R.string.action_delete));
        alert.setMessage(getString(R.string.delete_message));
        alert.setPositiveButton(getString(R.string.action_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Cart.sharedCart().removeItem(item.getItem());
                updateCartList();
                updateMenu();

            }
        });
        alert.setNegativeButton(getString(R.string.action_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        if (item.getCount() > 1) {
            alert.setNeutralButton(R.string.action_all, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Cart.sharedCart().removeAllItem(item.getItem());
                    updateCartList();
                    updateMenu();
                }
            });
        }

        alert.show();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_buy, menu);
        this.menu = menu;
        updateMenu();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = menuItem.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_buy) {

            recommendManager.buy();
            updateCartList();
            sendRecommend();
            updateMenu();
            showAlert(getString(R.string.buy_message));
        }

        return true;
    }

    private void sendRecommend() {
        recommendedAdapter.clear();
        recommendedAdapter.notifyDataSetChanged();
        recyclerView.invalidate();

        recommendManager.sendCartRecommend(new RecommendCompletionHandler() {
            @Override
            public void onRecommendedRequestComplete(final List<Item> resultData) {
                recommendedAdapter.setData(resultData);
                recommendedAdapter.notifyDataSetChanged();
                recyclerView.invalidate();
            }
        });

    }

    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(message);
        builder.setCancelable(true);

        builder.setPositiveButton(
                getString(R.string.action_ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void updateMenu() {
        if (menu != null) {
            MenuItem menuItem = menu.findItem(R.id.action_buy);
            if (menuItem != null) {
                if (listView.getCount() > 0) {
                    menuItem.setEnabled(true);
                } else {
                    menuItem.setEnabled(false);
                }
            }
        }
    }
}