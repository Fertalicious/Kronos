
package com.industries.fertile.countdowner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Calendar;
import java.util.List;

public class SettingsActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler {

    DBHandler db;
    TextView resultText;
    CheckBox deleteCheckbox;
    public static final String MY_PREFS_NAME = "MyPrefsFile";

    BillingProcessor bp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        db = new DBHandler(this);
        resultText = (TextView) findViewById(R.id.textView2);
        deleteCheckbox = (CheckBox) findViewById(R.id.checkBox);

        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        int deleteOrNot = prefs.getInt("delete", 0); //0 is the default value.
        if (deleteOrNot == 0){
            deleteCheckbox.setChecked(false);
        } else if(deleteOrNot == 1){
            deleteCheckbox.setChecked(true);
        }

        // Back button
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        bp = new BillingProcessor(this, "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAg9hWJiC2vL71NLRDK3iLLxeicuxXdsui7AT0uxRRU/Fs9CVdgCOmQuAZHqa9niTyQLmk4FMy3k16N83Tw3uSWJDHHaYGalzbwPrhpgQcp40ZF8pZxMPi98+6bsqZiyvtom8hhvHSoVKcLZJj6Z4KGuMo2wlQA2X1Ve0XWwlVVx7/NIYI0tdATejcmoOASsPYKqRMqb2b+jfFHUtSCpeRXaFGlMGMqhBHDeBE23pT/Sr1ePUCQtyfQbcZRZw6FoXlRWs0mUEwMFrvvH47Nvn0DylfNy8Nts37uh5xlNVnJeJXS+SoZ6B8Vm9ZvpRbVXq8CFre3DCC/hXAkpC3j6WlFwIDAQAB", this);

        int adsOrNot = prefs.getInt("ads", 1); //1 is the default value.
        if (adsOrNot == 0) {
            findViewById(R.id.supportView).setVisibility(View.GONE);
            findViewById(R.id.supportLinearView).setVisibility(View.GONE);
        }
    }

    public void clickRecreateDB(View view){
        db.reCreateTable();
        resultText.setText("Dates Deleted");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    public void checkBoxClicked(View v) {
        //code to check if this checkbox is checked!
        CheckBox checkBox = (CheckBox)v;
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        if(checkBox.isChecked()){
            editor.putInt("delete", 1);
            editor.apply();
        }else{
            editor.putInt("delete", 0);
            editor.apply();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                int deleteOrNot = prefs.getInt("delete", 0); //0 is the default value.
                if (deleteOrNot == 0){
                    Intent myIntent = new Intent(SettingsActivity.this, MainActivity.class);
                    SettingsActivity.this.startActivity(myIntent);
                } else if(deleteOrNot == 1){
                    // go through db and delete all negative ones
                    List<CountdownDate> myDates;
                    myDates = db.getAllDates();
                    for (CountdownDate aDate : myDates) {
                        Calendar rightNow = Calendar.getInstance();
                        LocalDateTime localDateTime = new LocalDateTime();
                        LocalDateTime calendarDateTime = localDateTime.fromCalendarFields(rightNow);
                        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd hh:mm:ss a");
                        DateTime dt = formatter.parseDateTime(aDate.getDateTime());
                        Period period = new Period(calendarDateTime.toDateTime(), dt);
                        if(period.getMillis() < 0){
                            db.deleteDate(aDate);
                        }

                    }


                }
                Intent myIntent = new Intent(SettingsActivity.this, MainActivity.class);
                SettingsActivity.this.startActivity(myIntent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // IBillingHandler implementation

    @Override
    public void onBillingInitialized() {
        /*
         * Called when BillingProcessor was initialized and it's ready to purchase
         */
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        /*
         * Called when requested PRODUCT ID was successfully purchased
         */
        if(productId.equals("ad_removal")){
            SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
            editor.putInt("ads", 0);
            editor.apply();
        }
    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        /*
         * Called when some error occurred. See Constants class for more details
         *
         * Note - this includes handling the case where the user canceled the buy dialog:
         * errorCode = Constants.BILLING_RESPONSE_RESULT_USER_CANCELED
         */
    }

    @Override
    public void onPurchaseHistoryRestored() {
        /*
         * Called when purchase history was restored and the list of all owned PRODUCT ID's
         * was loaded from Google Play
         */
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        if (bp != null)
            bp.release();

        super.onDestroy();
    }

    public void clickRemove(View view){
        boolean isAvailable = BillingProcessor.isIabServiceAvailable(this);
        if(!isAvailable) {
            // continue
            Toast.makeText(this, "Billing process not available", Toast.LENGTH_LONG ).show();
        }else{
            bp.purchase(this, "ad_removal");
        }

    }

}