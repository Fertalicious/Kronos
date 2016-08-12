package com.industries.fertile.countdowner;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.squareup.picasso.Picasso;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.File;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EditDate extends AppCompatActivity {

    TextView dateTextView;
    TextView timeTextView;
    EditText titleText;
    Switch timeSwitch;
    CheckBox repeatCheck;
    CheckBox favoriteCheck;
    RelativeLayout timeLayout;
    public static String savedTime;
    DBHandler db;
    CountdownDate editDate;
    Spinner repeatSpinner;
    Spinner earlySpinner;
    ImageView backgroundImage;
    AlarmReceiver earlyReceiver;
    AlarmReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JodaTimeAndroid.init(this);
        setContentView(R.layout.activity_edit_date);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Add the ad
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-7455497383270639~1279190105");
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        dateTextView = (TextView) findViewById(R.id.dateTextView);
        timeTextView = (TextView) findViewById(R.id.timeTextView);
        titleText = (EditText) findViewById(R.id.titleEditText);
        timeSwitch = (Switch) findViewById(R.id.switchTime);
        timeLayout = (RelativeLayout) findViewById(R.id.timeRLayout);
        db = new DBHandler(this);

        // Back button
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Bundle b = this.getIntent().getExtras();
        if (b != null) {
            editDate = b.getParcelable("COUNTDOWN_DATE");
        }
        else {
            editDate = new CountdownDate();
        }

        savedTime = " ";

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        String monthString = new DateFormatSymbols().getMonths()[month];

        DateTime dt;
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd hh:mm:ss a");
        dt = formatter.parseDateTime(editDate.getDateTime());

        titleText.setText(editDate.getTitle());

        dateTextView.setText(dt.toString("MMMM") + " " + dt.getDayOfMonth() + ", " + dt.getYear());
        //dateTextView.setText(dt.toString("MMMM dd, yyyy"));

        if(editDate.getTime() == 1){
            timeSwitch.setChecked(true);
            timeLayout.setVisibility(View.VISIBLE);
            timeTextView.setText(dt.toString("h:mm a"));
        }else{
            timeSwitch.setChecked(false);
            timeLayout.setVisibility(View.GONE);
        }

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

        repeatSpinner = (Spinner)findViewById(R.id.repeatSpinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.repeat_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        repeatSpinner.setAdapter(adapter);
        repeatSpinner.setSelection(editDate.getRepeat());

        earlySpinner = (Spinner)findViewById(R.id.earlySpinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> earlyAdapter = ArrayAdapter.createFromResource(this,
                R.array.early_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        earlySpinner.setAdapter(earlyAdapter);
        earlySpinner.setSelection(editDate.getFavorite());

        String imageName = editDate.getBackground();
        backgroundImage = (ImageView)findViewById(R.id.imageView);
        if (imageName != null) {
            Uri uri = Uri.fromFile(new File(imageName));
            Log.d("URI :: ", "" + uri);
            Picasso.with(EditDate.this)
                    .load(uri)
                    .resize(200, 200)
                    .placeholder(R.drawable.error)
                    .into(backgroundImage);
        } else {
            backgroundImage.setImageResource(R.drawable.placeholder);
        }

        // kill keyboard when enter is pressed
        titleText.setOnKeyListener(new View.OnKeyListener()
        {
            /**
             * This listens for the user to press the enter button on
             * the keyboard and then hides the virtual keyboard
             */
            public boolean onKey(View arg0, int arg1, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ( (event.getAction() == KeyEvent.ACTION_DOWN  ) &&
                        (arg1           == KeyEvent.KEYCODE_ENTER)   )
                {
                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(titleText.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        } );

        final String MY_PREFS_NAME = "MyPrefsFile";
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        int adsOrNot = prefs.getInt("ads", 1); //0 is the default value.
        if (adsOrNot == 0) {
            LinearLayout adsContainer = (LinearLayout) findViewById(R.id.adsContainer);

            View adMobAds = findViewById(R.id.adView);

            adsContainer.removeView(adMobAds);
        }

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
                int timeChecked;
                String background;
                Date startDate;
                int repeat = 0;
                int earlyNot;

                DateTime dt;

                // Set the title
                title = titleText.getText().toString();

                // Set the date and time if time is chosen
                date = dateTextView.getText().toString();

                if(timeSwitch.isChecked()){
                    timeChecked = 1;
                    time = timeTextView.getText().toString();
                    dateTime = date + " " + time;
                    DateTimeFormatter formatter = DateTimeFormat.forPattern("MMMM dd, yyyy hh:mm a");
                    dt = formatter.parseDateTime(dateTime);
                }else{
                    timeChecked = 0;
                    dateTime = date;
                    DateTimeFormatter formatter = DateTimeFormat.forPattern("MMMM dd, yyyy");
                    dt = formatter.parseDateTime(dateTime);
                }

                String repeatResult = repeatSpinner.getSelectedItem().toString();
                if(repeatResult.equals("No")){
                    repeat = 0;
                }else if(repeatResult.equals("Daily")){
                    repeat = 1;
                }else if(repeatResult.equals("Weekly")){
                    repeat = 2;
                }else if(repeatResult.equals("Monthly")){
                    repeat = 3;
                }else if(repeatResult.equals("Yearly")){
                    repeat = 4;
                }

                Log.d("Repeat: ", "Repeat = " + repeat);

                String earlyResult = earlySpinner.getSelectedItem().toString();
                if (earlyResult.equals("No")){
                    earlyNot = 0;
                }else{
                    earlyNot = Integer.parseInt(earlyResult);
                }

                Log.d("Early Notification: ", "earlyNot = " + earlyNot);

                background = editDate.getBackground();

                // Place each piece of info in database
                DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd hh:mm:ss a");

                int id = (int)dt.getMillis()+(int)(Math.random() * 100000);
                CountdownDate updatedDate = new CountdownDate(id, title, fmt.print(dt), repeat, earlyNot, background, timeChecked);

                // Edit the date in the DB--------------------------------------------------------------------------------------------------------------------------------------
               // db.updateDate(updatedDate);

                Intent olditAlarm = new Intent("NOTIFICATION_SERVICE");
                PendingIntent oldpendingIntent = PendingIntent.getBroadcast(getApplicationContext(), editDate.getId(), olditAlarm, 0);
                AlarmManager oldtheAlarm = (AlarmManager) getSystemService(ALARM_SERVICE);
                oldtheAlarm.cancel(oldpendingIntent);
                if(editDate.getFavorite() != 0){
                    Intent aearlyItAlarm = new Intent("NOTIFICATION_SERVICE");
                    PendingIntent aearlyPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), editDate.getId()+1,aearlyItAlarm,0);
                    AlarmManager aearlyTheAlarm = (AlarmManager) getSystemService(ALARM_SERVICE);
                    aearlyTheAlarm.cancel(aearlyPendingIntent);
                }
                db.deleteDate(editDate);
                db.addCountdownDate(updatedDate);

                Log.d("Reading: ", "Reading all dates...");

                List<CountdownDate> dates = db.getAllDates();

                for(CountdownDate aDate : dates){
                    String log = "Id: " + aDate.getId() + " , Title: " + aDate.getTitle() + " , DateTime: " + aDate.getDateTime() +
                            " , Repeat: " + aDate.getRepeat() + " , Favorite: " + aDate.getFavorite() + " , Background: " + aDate.getBackground();
                    Log.d("Editdate list: : ", log);
                }

                db.close();
                /*
                Intent olditAlarm = new Intent(Integer.toString(updatedDate.getId()));
                PendingIntent oldpendingIntent = PendingIntent.getBroadcast(this,0,olditAlarm,0);
                AlarmManager oldtheAlarm = (AlarmManager) getSystemService(ALARM_SERVICE);
                oldtheAlarm.cancel(oldpendingIntent);
                if(updatedDate.getFavorite() != 0){
                    Intent aearlyItAlarm = new Intent(updatedDate.getId() + "earlyNot");
                    PendingIntent aearlyPendingIntent = PendingIntent.getBroadcast(this,0,aearlyItAlarm,0);
                    AlarmManager aearlyTheAlarm = (AlarmManager) getSystemService(ALARM_SERVICE);
                    aearlyTheAlarm.cancel(aearlyPendingIntent);
                }*/

                // Start alarm here
                receiver = new AlarmReceiver();
                /*
                IntentFilter filter = new IntentFilter(Integer.toString(updatedDate.getId()));
                registerReceiver(receiver, filter);
*/
                DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd hh:mm:ss a");
                DateTime theDateTime = formatter.parseDateTime(updatedDate.getDateTime());

                Calendar calendar = Calendar.getInstance();

                calendar.set(Calendar.MONTH, theDateTime.getMonthOfYear()-1);
                calendar.set(Calendar.YEAR, theDateTime.getYear());
                calendar.set(Calendar.DAY_OF_MONTH, theDateTime.getDayOfMonth());

                calendar.set(Calendar.HOUR_OF_DAY, theDateTime.getHourOfDay());
                calendar.set(Calendar.MINUTE, theDateTime.getMinuteOfHour());
                calendar.set(Calendar.SECOND, theDateTime.getSecondOfMinute());
                //calendar.set(Calendar.AM_PM, Calendar.PM);

                //Intent itAlarm = new Intent(Integer.toString(updatedDate.getId()));
                Intent itAlarm = new Intent("NOTIFICATION_SERVICE");
                itAlarm.putExtra("CountdownDate", updatedDate);
                String log2 = "Id: " + updatedDate.getId() + " , Title: " + updatedDate.getTitle() + " , DateTime: " + updatedDate.getDateTime() +
                        " , Repeat: " + updatedDate.getRepeat() + " , EarlyNot: " + updatedDate.getFavorite() + " , Background: " + updatedDate.getBackground();
                Log.d("updatedDate: : ", log2);
                itAlarm.putExtra("early", 0);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this,updatedDate.getId(),itAlarm,0);
                AlarmManager theAlarm = (AlarmManager) getSystemService(ALARM_SERVICE);

                Log.d("ALARM SET ", "Calendar: " + calendar.getTimeInMillis() + "   " + theDateTime.getHourOfDay());
                Log.d("ALARM SET ", "DateTime: " + theDateTime.getMillis());

                // Set repeating if repeat is set
                DateTime repeatDate = null;
                if (updatedDate.getRepeat()!=0) {
                    if (updatedDate.getRepeat() == 1) {
                        repeatDate = dt.plusDays(1);
                    } else if (updatedDate.getRepeat() == 2) {
                        repeatDate = dt.plusWeeks(1);
                    } else if (updatedDate.getRepeat() == 3) {
                        repeatDate = dt.plusMonths(1);
                    } else if (updatedDate.getRepeat() == 4) {
                        repeatDate = dt.plusYears(1);
                    }

                    long repeatInMillis = repeatDate.getMillis() - theDateTime.getMillis();
                    Log.d("REPEAT IN MILLIS: ", "repeatInMillis= " + repeatInMillis);
                    theAlarm.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), repeatInMillis, pendingIntent);
                }else{
                    theAlarm.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                }

                // Set Early Notification
                if (earlyNot != 0){
                    earlyReceiver = new AlarmReceiver();
                    /*
                    IntentFilter earlyFilter = new IntentFilter(updatedDate.getId() + "earlyNot");
                    registerReceiver(earlyReceiver, earlyFilter);
*/

                    DateTime earlyTheDateTime = formatter.parseDateTime(updatedDate.getDateTime());
                    earlyTheDateTime = earlyTheDateTime.minusDays(earlyNot);

                    Calendar earlyCalendar = Calendar.getInstance();

                    earlyCalendar.set(Calendar.MONTH, earlyTheDateTime.getMonthOfYear()-1);
                    earlyCalendar.set(Calendar.YEAR, earlyTheDateTime.getYear());
                    earlyCalendar.set(Calendar.DAY_OF_MONTH, earlyTheDateTime.getDayOfMonth());

                    earlyCalendar.set(Calendar.HOUR_OF_DAY, earlyTheDateTime.getHourOfDay());
                    earlyCalendar.set(Calendar.MINUTE, earlyTheDateTime.getMinuteOfHour());
                    earlyCalendar.set(Calendar.SECOND, earlyTheDateTime.getSecondOfMinute());
                    //calendar.set(Calendar.AM_PM, Calendar.PM);

                    //Intent earlyItAlarm = new Intent(updatedDate.getId() + "earlyNot");
                    Intent earlyItAlarm = new Intent("NOTIFICATION_SERVICE");
                    earlyItAlarm.putExtra("CountdownDate", updatedDate);
                    earlyItAlarm.putExtra("early", 1);

                    PendingIntent earlyPendingIntent = PendingIntent.getBroadcast(this,updatedDate.getId()+1,earlyItAlarm,0);
                    AlarmManager earlyTheAlarm = (AlarmManager) getSystemService(ALARM_SERVICE);

                    earlyTheAlarm.set(AlarmManager.RTC_WAKEUP, earlyCalendar.getTimeInMillis(), earlyPendingIntent);

                    Log.d("Early ALARM SET ", "Calendar: " + earlyCalendar.getTimeInMillis() + "   " + earlyTheDateTime.getHourOfDay());
                    Log.d("Early ALARM SET ", "DateTime: " + earlyTheDateTime.getMillis());
                }

                Intent i = new Intent();
                Bundle b = new Bundle();

                b.putParcelable("COUNTDOWN_DATE", updatedDate);
                i.putExtras(b);
                i.setClass(EditDate.this, View_Date.class);
                startActivity(i);

                return true;
/*
            case R.id.home:
                Intent i2 = new Intent();
                Bundle b2 = new Bundle();

                b2.putParcelable("COUNTDOWN_DATE", editDate);
                i2.putExtras(b2);
                i2.setClass(EditDate.this, View_Date.class);
                startActivity(i2);

                return true;
*/

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
            EditDate activity = (EditDate) getActivity();
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
            EditDate activity = (EditDate) getActivity();
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

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    // Camera roll was scrapped, update if interest//-----------------------------------------------------------------------------------
    public void clickBackground(View v) {
        //showPopupWindow();
        if(isNetworkAvailable()) {
            String title;
            String date;
            String time;
            String dateTime;
            int timeChecked;
            String background;
            Date startDate;
            int repeat = 0;
            int earlyNot;

            DateTime dt;

            // Set the title
            title = titleText.getText().toString();

            // Set the date and time if time is chosen
            date = dateTextView.getText().toString();

            if (timeSwitch.isChecked()) {
                timeChecked = 1;
                time = timeTextView.getText().toString();
                dateTime = date + " " + time;
                DateTimeFormatter formatter = DateTimeFormat.forPattern("MMMM dd, yyyy hh:mm a");
                dt = formatter.parseDateTime(dateTime);
            } else {
                timeChecked = 0;
                dateTime = date;
                DateTimeFormatter formatter = DateTimeFormat.forPattern("MMMM dd, yyyy");
                dt = formatter.parseDateTime(dateTime);
            }

            String repeatResult = repeatSpinner.getSelectedItem().toString();
            if (repeatResult.equals("No")) {
                repeat = 0;
            } else if (repeatResult.equals("Daily")) {
                repeat = 1;
            } else if (repeatResult.equals("Weekly")) {
                repeat = 2;
            } else if (repeatResult.equals("Monthly")) {
                repeat = 3;
            } else if (repeatResult.equals("Daily")) {
                repeat = 4;
            }

            Log.d("Repeat: ", "Repeat = " + repeat);

            String earlyResult = earlySpinner.getSelectedItem().toString();
            if (earlyResult.equals("No")) {
                earlyNot = 0;
            } else {
                earlyNot = Integer.parseInt(earlyResult);
            }

            DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd hh:mm:ss a");
            CountdownDate newDate = new CountdownDate(editDate.getId(), title, fmt.print(dt), repeat, earlyNot, "", timeChecked);

            Intent i = new Intent();
            Bundle bu = new Bundle();

            bu.putParcelable("COUNTDOWN_DATE", newDate);
            i.putExtras(bu);
            i.putExtra("mother", "edit");
            i.setClass(EditDate.this, SelectBackground.class);
            startActivity(i);
        }else{
            Toast.makeText(EditDate.this, "Internet connection required",
                    Toast.LENGTH_LONG).show();
        }
    }

}
