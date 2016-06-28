package com.emarsys.predict.shop;

import android.app.ActivityGroup;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

public class MainActivity extends ActivityGroup {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create the TabHost that will contain the Tabs
        TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost);

        tabHost.setup(this.getLocalActivityManager());

        TabHost.TabSpec tab1 = tabHost.newTabSpec("Shop");
        TabHost.TabSpec tab2 = tabHost.newTabSpec("Search");
        TabHost.TabSpec tab3 = tabHost.newTabSpec("Cart");
        TabHost.TabSpec tab4 = tabHost.newTabSpec("User");

        // Set the Tab name and Activity
        // that will be opened when particular Tab will be selected
        tab1.setIndicator("");
        tab1.setContent(new Intent(MainActivity.this, CategoriesActivity.class));

        tab2.setIndicator("");
        tab2.setContent(new Intent(MainActivity.this, SearchActivity.class));

        tab3.setIndicator("");
        tab3.setContent(new Intent(this, CartActivity.class));

        tab4.setIndicator("");
        tab4.setContent(new Intent(this, UserActivity.class));

        /** Add the tabs  to the TabHost to display. */
        tabHost.addTab(tab1);
        tabHost.addTab(tab2);
        tabHost.addTab(tab3);
        tabHost.addTab(tab4);

        final TabWidget tabWidget = tabHost.getTabWidget();

        //Customize tabs
        Drawable shopIcon =
                ResourcesCompat.getDrawable(getResources(), R.drawable.categories, null);
        Drawable searchIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.search, null);
        Drawable cartIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.cart, null);
        Drawable userIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.user, null);

        Drawable[] icons = new Drawable[]{shopIcon, searchIcon, cartIcon, userIcon};
        String[] tabLabels = new String[]{getString(R.string.categories), getString(R.string.search), getString(R.string.cart), getString(R.string.user)};

        for (int index = 0; index < tabWidget.getTabCount(); index++) {
            View tab = tabWidget.getChildTabViewAt(index);
            if (tab instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) tab;
                vg.removeAllViews();
                LayoutInflater inflater = (LayoutInflater) this
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                View newTab = inflater.inflate(R.layout.tab_indicator, vg, false);
                TextView textView = (TextView) newTab.findViewById(R.id.title);
                textView.setText(tabLabels[index]);
                textView.setTextColor(Color.WHITE);
                ImageView imageView = (ImageView) newTab.findViewById(R.id.icon);
                imageView.setImageDrawable(icons[index]);
                vg.addView(newTab);
            }
        }
    }
}
