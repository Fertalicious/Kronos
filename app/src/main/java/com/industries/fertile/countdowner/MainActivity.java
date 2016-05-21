package com.industries.fertile.countdowner;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<CountdownDate> myDates;

    DBHandler db;
    ListView dateList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dateList = (ListView) findViewById(R.id.dateListView);
        db = new DBHandler(this);
        myDates = db.getAllDates();

        populateListView();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_new) {
            Intent myIntent = new Intent(MainActivity.this, NewDate.class);
            MainActivity.this.startActivity(myIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void populateListView(){
        ArrayAdapter<CountdownDate> adapter = new MyListAdapter();
        dateList.setAdapter(adapter);
    }

    private class MyListAdapter extends ArrayAdapter<CountdownDate> {
        public MyListAdapter(){
            super(MainActivity.this, R.layout.item_view, myDates);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Make sure we have a view
            View itemView = convertView;
            if(itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.item_view, parent, false);
            }

            TextView dateTimeText = (TextView) itemView.findViewById(R.id.dateTimeTextView);

            // find the date to work with
            CountdownDate currentDate = myDates.get(position);

            // fill the view
            dateTimeText.setText("" + currentDate.getDateTime());


            return itemView;
        }
    }
}
