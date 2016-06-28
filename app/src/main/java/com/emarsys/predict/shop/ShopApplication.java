package com.emarsys.predict.shop;

import com.emarsys.predict.Session;
import com.emarsys.predict.shop.data.DataSource;
import com.emarsys.predict.shop.storage.AndroidStorage;

import android.app.Application;

public class ShopApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // These parts of our API should be implemented on ALL website pages
        Session.initialize(new AndroidStorage(this));

        Session session = Session.getInstance();
        // Identifies the merchant account (here the emarsys demo merchant 1A65B5CB868AFF1E).
        // Replace it with your own Merchant Id before run.
        session.setMerchantId("1A74F439823D2CB4");

        DataSource.initWithContext(getApplicationContext());

    }
}
