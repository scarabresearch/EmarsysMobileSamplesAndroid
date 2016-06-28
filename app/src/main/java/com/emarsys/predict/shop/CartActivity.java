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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    private ListView listView;
    private CartItemAdapter adapter;
    private RecommendedAdapter recommendedAdapter;
    private RecyclerView recyclerView;
    private Menu menu;
    private RecommendManager recommendManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        listView = (ListView) findViewById(R.id.cartListView);

        adapter = new CartItemAdapter(this, Cart.sharedCart().getShopCartItems());

        listView.setAdapter(adapter);

        recyclerView = (RecyclerView) findViewById(R.id.recommendedListView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ShopCartItem item = (ShopCartItem) listView.getItemAtPosition(i);
                Intent intent = new Intent(CartActivity.this, ItemDetailActivity.class);
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
                Intent intent = new Intent(CartActivity.this, ItemDetailActivity.class);
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

        setTitle(getString(R.string.cart));

        updateCartList();
        sendRecommend();
    }

    @Override
    protected void onResume() {
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
                CartActivity.this);

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_buy, menu);
        this.menu = menu;
        updateMenu();
        return true;
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
        recommendManager.sendCartRecommend(new RecommendCompletionHandler() {
            @Override
            public void onRecommendedRequestComplete(final List<Item> resultData) {
                recommendedAdapter.clear();
                recommendedAdapter.setData(resultData);
                recommendedAdapter.notifyDataSetChanged();
                recyclerView.invalidate();
            }
        });

    }

    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
            if (listView.getCount() > 0) {
                menuItem.setEnabled(true);
            } else {
                menuItem.setEnabled(false);
            }
        }
    }
}