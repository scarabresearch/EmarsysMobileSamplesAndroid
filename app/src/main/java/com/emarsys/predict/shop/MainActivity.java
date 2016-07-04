package com.emarsys.predict.shop;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TabWidget;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private FragmentTabHost mTabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create the TabHost that will contain the Tabs
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        mTabHost.addTab(mTabHost.newTabSpec("t1").setIndicator("Categories"),
                CategoriesActivity.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("t2").setIndicator("Search"), SearchActivity
                .class, null);
        mTabHost.addTab(mTabHost.newTabSpec("t3").setIndicator("Cart"), CartActivity
                .class, null);
        mTabHost.addTab(mTabHost.newTabSpec("t4").setIndicator("User"), UserActivity
                .class, null);

        final TabWidget tabWidget = mTabHost.getTabWidget();

        //Customize tabs
        Drawable shopIcon =
                ResourcesCompat.getDrawable(getResources(), R.drawable.categories, null);
        Drawable searchIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.search, null);
        Drawable cartIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.cart, null);
        Drawable userIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.user, null);

        Drawable[] icons = new Drawable[]{shopIcon, searchIcon, cartIcon, userIcon};
        String[] tabLabels = new String[]{getString(R.string.categories),
                getString(R.string.search), getString(R.string.cart), getString(R.string.user)};

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
