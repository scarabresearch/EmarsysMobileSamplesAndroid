package com.emarsys.predict.shop;

import com.emarsys.predict.shop.adapters.RecommendedAdapter;
import com.emarsys.predict.shop.recommended.RecommendCompletionHandler;
import com.emarsys.predict.shop.recommended.RecommendManager;
import com.emarsys.predict.shop.shopitems.Cart;
import com.emarsys.predict.shop.shopitems.Item;
import com.emarsys.predict.shop.util.DownloadImageTask;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

public class ItemDetailActivity extends AppCompatActivity {

    private RecommendedAdapter recommendedAdapter;
    private RecyclerView recyclerView;
    private ImageView image;
    private TextView title;
    private TextView price;
    private TextView available;
    private Item item;
    private RecommendManager recommendManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.recommendedListView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);

        image = (ImageView) findViewById(R.id.itemImage);
        title = (TextView) findViewById(R.id.titleText);
        price = (TextView) findViewById(R.id.priceText);
        available = (TextView) findViewById(R.id.availabilityText);

        List<Item> data = new ArrayList<>();
        recommendedAdapter = new RecommendedAdapter(data);
        recommendedAdapter.setOnItemClickListener(new RecommendedAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Item item) {
                setItem(item);
                setup();
            }
        });
        recyclerView.setAdapter(recommendedAdapter);

        recommendManager = new RecommendManager();
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = getIntent();
        this.item = (Item) intent.getSerializableExtra("item");
        setup();
    }

    private void setItem(Item item) {
        this.item = item;
    }

    private void setup() {
        setTitle(item.getTitle());
        new DownloadImageTask(image).execute(item.getImage());
        title.setText(item.getTitle());
        Formatter formatter = new Formatter();
        price.setText(formatter.format("$%.1f", item.getPrice()).toString());
        String state = item.isAvailable() ?
                getString(R.string.in_stock) : getString(R.string.out_of_stock);
        available.setText(getString(R.string.availability) + state);
        int color = item.isAvailable() ? Color.GREEN : Color.RED;
        available.setTextColor(color);

        sendRecommend();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = menuItem.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_to_cart) {
            Cart.sharedCart().addItem(item);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.cart_massage));
            builder.setCancelable(true);

            builder.setPositiveButton(
                    getString(R.string.action_ok),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            finish();
                        }
                    });

            AlertDialog alert = builder.create();
            alert.show();
        } else if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(menuItem);
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

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String logic = sharedPref.getString(UserActivity.ITEM_DETAIL_LOGIC, "RELATED");

        if (logic.equals("RELATED")) {
            recommendManager.sendRelatedRecommend(item.getRecommendedItem(),
                    null,
                    item.getItemID(),
                    null,
                    handler);
        } else {
            recommendManager.sendAlsoBoughtRecommend(item.getRecommendedItem(),
                    item.getItemID(),
                    handler);
        }
    }
}
