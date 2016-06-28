package com.emarsys.predict.shop.data;

import com.emarsys.predict.shop.shopitems.Item;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class DataSource {

    private static final String TAG = "DataSource";

    private static DataSource shared = null;
    private ArrayList<Item> items;
    private ArrayList<String> categories;

    private DataSource() {
    }

    public static DataSource sharedDataSource() {
        if (shared == null) {
            throw new NullPointerException("DataSource must be initialized first with " +
                    "initWithContext");
        }
        return shared;
    }

    public static void initWithContext(Context context) {
        if (shared == null) {
            shared = new DataSource();
            shared.init(context);
        }
    }

    private void init(Context context) {
        this.items = new ArrayList<>();
        this.categories = new ArrayList<>();

        InputStream in;
        BufferedReader reader;
        String line;

        AssetManager manager = context.getAssets();
        try {
            in = manager.open("sample.csv");

            if (in != null) {
                reader = new BufferedReader(new InputStreamReader(in));

                int idx = 0;

                while ((line = reader.readLine()) != null) {
                    if (idx > 0) {
                        Item item = new Item();
                        String[] rows = line.split(",");
                        for (int i = 0; i < rows.length; i++) {
                            switch (i) {
                                case 0: {
                                    item.setItemID(rows[i]);
                                }
                                break;
                                case 1: {
                                    item.setLink(rows[i]);
                                }
                                break;
                                case 2: {
                                    item.setTitle(rows[i]);
                                }
                                break;
                                case 3: {
                                    item.setImage(rows[i]);
                                }
                                break;
                                case 4: {
                                    item.setCategory(rows[i]);
                                    if (!categories.contains(item.getCategory())) {
                                        categories.add(item.getCategory());
                                    }
                                }
                                break;
                                case 5: {
                                    item.setPrice(Float.valueOf(rows[i]));
                                }
                                break;
                                case 6: {
                                    item.setAvailable(Boolean.valueOf(rows[i]));
                                }
                                break;
                                case 7: {
                                    item.setBrand(rows[i]);
                                }
                                break;
                                default: {

                                }
                            }
                        }
                        items.add(item);
                    }
                    idx++;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Unable read sample.csv: " + e.getMessage());
        }

    }

    public ArrayList<Item> itemsFromCategory(String category) {
        ArrayList<Item> result = new ArrayList<>();

        for (Item item : items) {
            if (item.getCategory().equals(category)) {
                result.add(item);
            }
        }

        return result;
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public ArrayList<String> getCategories() {
        return categories;
    }
}
