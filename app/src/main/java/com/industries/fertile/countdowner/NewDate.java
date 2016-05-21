package com.industries.fertile.countdowner;



import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NewDate extends AppCompatActivity {

    TextView dateTextView;
    TextView timeTextView;
    EditText titleText;
    Switch timeSwitch;
    CheckBox repeatCheck;
    CheckBox favoriteCheck;
    RelativeLayout timeLayout;
    public static String savedTime;
    DBHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JodaTimeAndroid.init(this);
        setContentView(R.layout.activity_new_date);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        dateTextView = (TextView) findViewById(R.id.dateTextView);
        timeTextView = (TextView) findViewById(R.id.timeTextView);
        titleText = (EditText) findViewById(R.id.titleEditText);
        timeSwitch = (Switch) findViewById(R.id.switchTime);
        repeatCheck = (CheckBox) findViewById(R.id.repeatCheckBox);
        favoriteCheck = (CheckBox) findViewById(R.id.favoriteCheckBox);
        timeLayout = (RelativeLayout) findViewById(R.id.timeRLayout);
        timeLayout.setVisibility(View.GONE);
        db = new DBHandler(this);

        savedTime = " ";

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        String monthString = new DateFormatSymbols().getMonths()[month];
        dateTextView.setText(monthString + " " + day + ", " + year);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        timeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    timeLayout.setVisibility(View.VISIBLE);
                    if (savedTime.equals(" ")) {
                        final Calendar c = Calendar.getInstance();
                        int hourOfDay = c.get(Calendar.HOUR_OF_DAY);
                        int minute = c.get(Calendar.MINUTE);
                        Calendar datetime = Calendar.getInstance();
                        String am_pm = "";

                        datetime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        datetime.set(Calendar.MINUTE, minute);
                        if (datetime.get(Calendar.AM_PM) == Calendar.AM)
                            am_pm = "AM";
                        else if (datetime.get(Calendar.AM_PM) == Calendar.PM)
                            am_pm = "PM";

                        String strHrsToShow = (datetime.get(Calendar.HOUR) == 0) ? "12" : datetime.get(Calendar.HOUR) + "";
                        if (-1 < minute && minute < 10) {
                            timeTextView.setText(strHrsToShow + ":0" + datetime.get(Calendar.MINUTE) + " " + am_pm);
                        } else {
                            timeTextView.setText(strHrsToShow + ":" + datetime.get(Calendar.MINUTE) + " " + am_pm);
                        }
                    } else {
                        timeTextView.setText(savedTime);
                    }
                } else {
                        timeLayout.setVisibility(View.GONE);

                    }
                }

        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_newdate, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {

            case R.id.action_new:

                Log.d("Insert: ", "Inserting...");
                // Put in DB if all info is in there
                String title;
                String date;
                String time;
                String dateTime;
                int repeat;
                int favorite;
                String background;
                Date startDate;

                DateTime dt;

                // Set the title
                title = titleText.getText().toString();

                // Set the date and time if time is chosen
                date = dateTextView.getText().toString();

                if(timeSwitch.isChecked()){
                    time = timeTextView.getText().toString();
                    dateTime = date + " " + time;
                    DateTimeFormatter formatter = DateTimeFormat.forPattern("MMMM dd, yyyy hh:mm a");
                    dt = formatter.parseDateTime(dateTime);
                }else{
                    dateTime = date;
                    DateTimeFormatter formatter = DateTimeFormat.forPattern("MMMM dd, yyyy");
                    dt = formatter.parseDateTime(dateTime);
                }

                // Set repeat on or off
                if(repeatCheck.isChecked()){
                    repeat = 1;
                }else{
                    repeat = 0;
                }

                // Set favorite on or off
                if(favoriteCheck.isChecked()){
                    favorite = 1;
                }else{
                    favorite = 0;
                }

                background = "nothing";

                // Place each piece of info in database
                DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd hh:mm:ss a");
                db.addCountdownDate(new CountdownDate(0, title, fmt.print(dt), repeat, favorite, background));

                Log.d("Reading: ", "Reading all dates...");

                List<CountdownDate> dates = db.getAllDates();

                for(CountdownDate aDate : dates){
                    String log = "Id: " + aDate.getId() + " , Title: " + aDate.getTitle() + " , DateTime: " + aDate.getDateTime() +
                            " , Repeat: " + aDate.getRepeat() + " , Favorite: " + aDate.getFavorite() + " , Background: " + aDate.getBackground();
                    Log.d("CountDown Date: : ", log);
                }

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }


    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            String monthString = new DateFormatSymbols().getMonths()[month];
            NewDate activity = (NewDate) getActivity();
            TextView dateText = activity.dateTextView;
            dateText.setText(monthString + " " + day + ", " + year);
        }

    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        // DateFormat.is24HourFormat(context);
        // check users setting of 24 hour clock

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
            NewDate activity = (NewDate) getActivity();
            TextView dateText = activity.timeTextView;
            Calendar datetime = Calendar.getInstance();
            String am_pm = "";

            datetime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            datetime.set(Calendar.MINUTE, minute);
            if (datetime.get(Calendar.AM_PM) == Calendar.AM)
                am_pm = "AM";
            else if (datetime.get(Calendar.AM_PM) == Calendar.PM)
                am_pm = "PM";

            String strHrsToShow = (datetime.get(Calendar.HOUR) == 0) ?"12":datetime.get(Calendar.HOUR)+"";
            if(-1 < minute && minute < 10)
            {
                dateText.setText(strHrsToShow+":0"+datetime.get(Calendar.MINUTE)+" "+am_pm);
            }else {
                dateText.setText(strHrsToShow+":"+datetime.get(Calendar.MINUTE)+" "+am_pm);
            }

            savedTime = dateText.getText().toString();
        }
    }

    public int convertTo12Hour(int hour24){
        int hour12;
        if(hour24 > 12){
            hour12 = hour24 - 12;
        }else{
            hour12 = hour24;
        }
        return hour12;
    }
    /*
    public int convertTo24Hour(int hour12){

    }
    */
}


