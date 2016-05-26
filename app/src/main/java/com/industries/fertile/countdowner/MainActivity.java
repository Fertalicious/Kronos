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

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.Months;
import org.joda.time.Period;
import org.joda.time.Weeks;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.PeriodFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<CountdownDate> myDates;

    DBHandler db;
    ListView dateList;
    Calendar rightNow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dateList = (ListView) findViewById(R.id.dateListView);
        db = new DBHandler(this);
        myDates = db.getAllDates();

        rightNow = Calendar.getInstance();
        LocalDateTime localDateTime = new LocalDateTime();
/*
        DateTimeFormatter dateFormat = DateTimeFormat
                .forPattern("G,C,Y,x,w,e,E,Y,D,M,d,a,K,h,H,k,m,s,S,z,Z");

        LocalTime localTime = new LocalTime();
        LocalDate localDate = new LocalDate();
        DateTime dateTime = new DateTime();

        DateTimeZone dateTimeZone = DateTimeZone.getDefault();

        Log.d("dateFormater: : ", dateFormat.print(localDateTime));
        Log.d("LocalTime: : ", localTime.toString());
        Log.d("LocalDate : ", localDate.toString());
        Log.d("dateTime: : ", dateTime.toString());
        Log.d("localDateTime: : ", localDateTime.toString());
        Log.d("dateTimeZone: : ", dateTimeZone.toString());
*/
        LocalDateTime calendarDateTime = localDateTime.fromCalendarFields(rightNow);
        Log.d("calendarDateTime: : ", calendarDateTime.toString());
        Log.d("ToString: : ", calendarDateTime.toDateTime().toString());
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
            Intent myIntent = new Intent(MainActivity.this, SettingsActivity.class);
            MainActivity.this.startActivity(myIntent);
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
            TextView titleText = (TextView) itemView.findViewById(R.id.titleTextView);
            TextView targetDateText = (TextView) itemView.findViewById(R.id.targetDateTextView);

            // find the date to work with
            CountdownDate targetDate = myDates.get(position);


            // Get the time difference
            //DateTimeZone.getDefault();
            LocalDateTime localDateTime = new LocalDateTime();
            LocalDateTime calendarDateTime = localDateTime.fromCalendarFields(rightNow);
            DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd hh:mm:ss a");
            DateTime dt = formatter.parseDateTime(targetDate.getDateTime());

            DateTimeFormatter formatter2 = DateTimeFormat.forPattern("MMMM dd, yyyy hh:mm a");
            String tdTextWithTime = formatter2.print(dt);

            DateTimeFormatter formatter3 = DateTimeFormat.forPattern("MMMM dd, yyyy");
            String tdTextWithoutTime = formatter3.print(dt);

            Period period = new Period(calendarDateTime.toDateTime(), dt);
            Hours hoursBetween = Hours.hoursBetween(calendarDateTime.toDateTime(), dt);
            Days daysBetween = Days.daysBetween(calendarDateTime.toDateTime(), dt);
            Weeks weeksBetween = Weeks.weeksBetween(calendarDateTime.toDateTime(), dt);
            Months monthsBetween = Months.monthsBetween(calendarDateTime.toDateTime(), dt);

            // fill the view
            titleText.setText("" + targetDate.getTitle());
            //targetDateText.setText(targetDate.getDateTime());
            if(targetDate.getTime()==1) {
                targetDateText.setText(tdTextWithTime);
            } else {
                targetDateText.setText(tdTextWithoutTime);
            }

            //dateTimeText.setText(PeriodFormat.getDefault().print(period));
            //String datetimeString = weeksBetween.getWeeks() + ":" + period.getDays() + ":" + period.getHours() + ":" + period.getMinutes() + ":" + period.getSeconds();
            if (daysBetween.getDays() > 0 || daysBetween.getDays() < -1) {
                if (daysBetween.getDays() == 1 || daysBetween.getDays() == -1)
                    dateTimeText.setText("" + daysBetween.getDays() + " day");
                else
                    dateTimeText.setText("" + daysBetween.getDays() + " days");
            }else {
                if (hoursBetween.getHours() == 1 || hoursBetween.getHours() == -1)
                    dateTimeText.setText(hoursBetween.getHours() + " hour");
                else
                    dateTimeText.setText(hoursBetween.getHours() + " hours");
            }

/*
            String log = localDateTime.toString();
            Log.d("local date time: : ", log);
*/
            return itemView;
        }
    }
}
