package com.emarsys.predict.shop.storage;

import com.emarsys.predict.Storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class AndroidStorage implements Storage {

    private SharedPreferences sharedPref;

    public AndroidStorage(Context context) {
        this.sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public void put(String key, Object value) {
        SharedPreferences.Editor editor = sharedPref.edit();
        if (value instanceof String) {
            editor.putString(key, (String) value);
        }
        editor.apply();
    }

    @Override
    public Object get(String key) {
        return sharedPref.getString(key, null);
    }
}
