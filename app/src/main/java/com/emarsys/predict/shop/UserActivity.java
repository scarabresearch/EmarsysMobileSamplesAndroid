package com.emarsys.predict.shop;


import com.emarsys.predict.Session;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Formatter;
import java.util.UUID;

public class UserActivity extends Fragment {

    public static final String CUSTOMER_EMAIL = "customer_email";
    public static final String CUSTOMER_ID = "customer_id";
    public static final String ITEM_DETAIL_LOGIC = "item_detail_logic";
    public static final String RELATED = "RELATED";
    public static final String ALSO_BOUGHT = "ALSO_BOUGHT";
    public static final String LOGIN_WITH_EMAIL = "login_with_email";

    private TextView emailText;
    private TextView customerIDText;
    private Switch loginWithEmailSwitch;
    private RadioButton relatedRadioButton;
    private RadioButton alsoBoughtRadioButton;
    private boolean logged = false;
    private Menu menu;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_user,
                container, false);

        setHasOptionsMenu(true);

        emailText = (TextView) view.findViewById(R.id.emailText);
        customerIDText = (TextView) view.findViewById(R.id.customerIdText);
        loginWithEmailSwitch = (Switch) view.findViewById(R.id.loginWithEmailSwitch);
        relatedRadioButton = (RadioButton) view.findViewById(R.id.relatedRadioButton);
        alsoBoughtRadioButton = (RadioButton) view.findViewById(R.id.alsoBoughtRadioButton);

        Button saveButton = (Button) view.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });

        Button showIdsButton = (Button) view.findViewById(R.id.showIdsButton);
        showIdsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showIds();
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        setStoreValues();
        updateMenu();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateMenu();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_login, menu);
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
        if (id == R.id.action_login) {
            login();
        }

        return super.onOptionsItemSelected(menuItem);
    }

    private void save() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = sharedPref.edit();

        String email = emailText.getText().toString();
        if (!email.isEmpty()) {
            editor.putString(CUSTOMER_EMAIL, email);
        }

        String customerId = customerIDText.getText().toString();
        if (!customerId.isEmpty()) {
            editor.putString(CUSTOMER_ID, customerId);
        }

        String logic = relatedRadioButton.isChecked() ? RELATED : ALSO_BOUGHT;

        editor.putString(ITEM_DETAIL_LOGIC, logic);

        editor.putBoolean(LOGIN_WITH_EMAIL, loginWithEmailSwitch.isChecked());

        editor.apply();

        showAlert(getString(R.string.save_message));
    }

    private void showIds() {
        Formatter formatter = new Formatter();
        showAlert(formatter.format("IDFA:\n %s \n\nAdvertising UUID:\n %s",
                UUID.randomUUID().toString(),
                Session.getInstance().getAdvertisingId()).toString());
    }

    private void login() {
        String message;
        Session session = Session.getInstance();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean loginWithEmail = loginWithEmailSwitch.isChecked();

        if (logged) {
            logged = false;
            session.setCustomerEmail(null);
            session.setCustomerId(null);
            message = getString(R.string.logout_message);
            updateMenu();
        } else {

            if (loginWithEmail) {
                String email = sharedPref.getString(CUSTOMER_EMAIL, "");

                if (email.isEmpty()) {
                    message = getString(R.string.missed_customeremail_message);
                } else {
                    session.setCustomerEmail(email);
                    message = getString(R.string.login_message);
                    logged = true;
                    updateMenu();
                }
            } else {
                String customerId = sharedPref.getString(CUSTOMER_ID, "");
                if (customerId.isEmpty()) {
                    message = getString(R.string.missed_customerid_message);
                } else {
                    session.setCustomerId(customerId);
                    message = getString(R.string.login_message);
                    logged = true;
                    updateMenu();
                }
            }
        }

        showAlert(message);
    }

    private void updateMenu() {
        if (menu == null) return;

        MenuItem menuItem = menu.findItem(R.id.action_login);
        if (menuItem != null) {
            if (logged) {
                menuItem.setTitle(getString(R.string.action_logout));
            } else {
                menuItem.setTitle(getString(R.string.action_login));
            }
        }
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

    private void setStoreValues() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());

        emailText.setText(sharedPref.getString(CUSTOMER_EMAIL, ""));
        customerIDText.setText(sharedPref.getString(CUSTOMER_ID, ""));

        String userLogic = sharedPref.getString(ITEM_DETAIL_LOGIC, "");

        if (userLogic.isEmpty() || userLogic.equals(RELATED)) {
            relatedRadioButton.setChecked(true);
        } else {
            alsoBoughtRadioButton.setChecked(true);
        }

        loginWithEmailSwitch.setChecked(sharedPref.getBoolean(LOGIN_WITH_EMAIL, true));
    }
}