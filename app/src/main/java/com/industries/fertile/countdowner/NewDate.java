package com.industries.fertile.countdowner;



import android.app.ActionBar;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import java.io.File;
import java.net.URI;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NewDate extends AppCompatActivity implements View.OnClickListener {

    TextView dateTextView;
    TextView timeTextView;
    EditText titleText;
    Switch timeSwitch;
    CheckBox repeatCheck;
    CheckBox favoriteCheck;
    RelativeLayout timeLayout;
    public static String savedTime;
    DBHandler db;
    String imageName = " ";
    RelativeLayout mainLayout;
    PopupWindow popupWindow;
    private Button cancelBtn;
    private static int RESULT_LOAD_IMG = 1;
    ImageView backgroundImage;
    Spinner repeatSpinner;
    Spinner earlySpinner;
    AlarmReceiver receiver;
    AlarmReceiver earlyReceiver;

    //private static String[] IMAGE_URLS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JodaTimeAndroid.init(this);
        setContentView(R.layout.activity_new_date);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //IMAGE_URLS = getResources().getStringArray(R.array.background_resources);
        mainLayout = new RelativeLayout(this);

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
        timeLayout.setVisibility(View.GONE);
        db = new DBHandler(this);

        imageName = getIntent().getStringExtra("imageDir");

        savedTime = " ";

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        String monthString = new DateFormatSymbols().getMonths()[month];
        dateTextView.setText(monthString + " " + day + ", " + year);

        // Back button
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

        // Set the small image on the background

        backgroundImage = (ImageView) findViewById(R.id.imageView);

        Log.d("imageName = ", "" + imageName);

        if (imageName != null) {
            Uri uri = Uri.fromFile(new File(imageName));
            Log.d("URI :: ", "" + uri);
            Picasso.with(NewDate.this)
                    .load(uri)
                    .resize(200, 200)
                    .placeholder(R.drawable.error)
                    .into(backgroundImage);
        } else {
            backgroundImage.setImageResource(R.drawable.placeholder);
        }


        repeatSpinner = (Spinner) findViewById(R.id.repeatSpinner);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.repeat_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        repeatSpinner.setAdapter(adapter);

        earlySpinner = (Spinner) findViewById(R.id.earlySpinner);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> earlyAdapter = ArrayAdapter.createFromResource(this,
                R.array.early_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        earlySpinner.setAdapter(earlyAdapter);

        Bundle b = this.getIntent().getExtras();
        if (b != null) {
            CountdownDate newDate = b.getParcelable("COUNTDOWN_DATE");
            DateTime dt;
            DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd hh:mm:ss a");
            dt = formatter.parseDateTime(newDate.getDateTime());

            titleText.setText(newDate.getTitle());

            dateTextView.setText(dt.toString("MMMM") + " " + dt.getDayOfMonth() + ", " + dt.getYear());
            //dateTextView.setText(dt.toString("MMMM dd, yyyy"));

            if(newDate.getTime() == 1){
                timeSwitch.setChecked(true);
                timeLayout.setVisibility(View.VISIBLE);
                timeTextView.setText(dt.toString("h:mm a"));
            }else{
                timeSwitch.setChecked(false);
                timeLayout.setVisibility(View.GONE);
            }
            repeatSpinner.setSelection(newDate.getRepeat());
            earlySpinner.setSelection(newDate.getFavorite());
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
                int repeat = 0;
                int earlyNot;
                int timeChecked;
                String background;
                Date startDate;

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

                /*
                <item>No</item>
        <item>Daily</item>
        <item>Weekly</item>
        <item>Monthly</item>
        <item>Yearly</item>
                 */
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

                // Place each piece of info in database
                DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd hh:mm:ss a");
/*
                if (position != -1){
                    background = IMAGE_URLS[position];
                }else{
                    background = "nothing";
                }
  */
                background = imageName;

                int id = (int)dt.getMillis()+(int)(Math.random() * 100000);
                CountdownDate newDate = new CountdownDate(id, title, fmt.print(dt), repeat, earlyNot, background, timeChecked);

                db.addCountdownDate(newDate);

                Log.d("Reading: ", "Reading all dates...");

                List<CountdownDate> dates = db.getAllDates();

                for (CountdownDate aDate : dates) {
                    String log = "Id: " + aDate.getId() + " , Title: " + aDate.getTitle() + " , DateTime: " + aDate.getDateTime() +
                            " , Repeat: " + aDate.getRepeat() + " , EarlyNot: " + aDate.getFavorite() + " , Background: " + aDate.getBackground();
                    Log.d("CountDown Date: : ", log);
                }
                db.close();
                // Start alarm here
                receiver = new AlarmReceiver();
                IntentFilter filter = new IntentFilter(Integer.toString(newDate.getId()));
                registerReceiver(receiver, filter);

                    DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd hh:mm:ss a");
                    DateTime theDateTime = formatter.parseDateTime(newDate.getDateTime());

                    Calendar calendar = Calendar.getInstance();

                    calendar.set(Calendar.MONTH, theDateTime.getMonthOfYear()-1);
                    calendar.set(Calendar.YEAR, theDateTime.getYear());
                    calendar.set(Calendar.DAY_OF_MONTH, theDateTime.getDayOfMonth());

                    calendar.set(Calendar.HOUR_OF_DAY, theDateTime.getHourOfDay());
                    calendar.set(Calendar.MINUTE, theDateTime.getMinuteOfHour());
                    calendar.set(Calendar.SECOND, theDateTime.getSecondOfMinute());
                    //calendar.set(Calendar.AM_PM, Calendar.PM);

                    Intent itAlarm = new Intent("NOTIFICATION_SERVICE");
                    itAlarm.putExtra("CountdownDate", newDate);
                    itAlarm.putExtra("early", 0);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(this,newDate.getId(),itAlarm,0);
                    AlarmManager theAlarm = (AlarmManager) getSystemService(ALARM_SERVICE);

                Log.d("ALARM SET ", "Calendar: " + calendar.getTimeInMillis() + "   " + theDateTime.getHourOfDay());
                Log.d("ALARM SET ", "DateTime: " + theDateTime.getMillis());

                // Set repeating if repeat is set
                DateTime repeatDate = null;
                if (newDate.getRepeat()!=0) {
                    if (newDate.getRepeat() == 1) {
                        repeatDate = dt.plusDays(1);
                    } else if (newDate.getRepeat() == 2) {
                        repeatDate = dt.plusWeeks(1);
                    } else if (newDate.getRepeat() == 3) {
                        repeatDate = dt.plusMonths(1);
                    } else if (newDate.getRepeat() == 4) {
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
                    /*
                    earlyReceiver = new AlarmReceiver();
                    IntentFilter earlyFilter = new IntentFilter(newDate.getId() + "earlyNot");
                    registerReceiver(earlyReceiver, earlyFilter);
*/
                    DateTime earlyTheDateTime = formatter.parseDateTime(newDate.getDateTime());
                    earlyTheDateTime = earlyTheDateTime.minusDays(earlyNot);

                    Calendar earlyCalendar = Calendar.getInstance();

                    earlyCalendar.set(Calendar.MONTH, earlyTheDateTime.getMonthOfYear()-1);
                    earlyCalendar.set(Calendar.YEAR, earlyTheDateTime.getYear());
                    earlyCalendar.set(Calendar.DAY_OF_MONTH, earlyTheDateTime.getDayOfMonth());

                    earlyCalendar.set(Calendar.HOUR_OF_DAY, earlyTheDateTime.getHourOfDay());
                    earlyCalendar.set(Calendar.MINUTE, earlyTheDateTime.getMinuteOfHour());
                    earlyCalendar.set(Calendar.SECOND, earlyTheDateTime.getSecondOfMinute());
                    //calendar.set(Calendar.AM_PM, Calendar.PM);

                    Intent earlyItAlarm = new Intent("NOTIFICATION_SERVICE");
                    earlyItAlarm.putExtra("CountdownDate", newDate);
                    earlyItAlarm.putExtra("early", 1);

                    PendingIntent earlyPendingIntent = PendingIntent.getBroadcast(this,newDate.getId()+1,earlyItAlarm,0);
                    AlarmManager earlyTheAlarm = (AlarmManager) getSystemService(ALARM_SERVICE);

                    earlyTheAlarm.set(AlarmManager.RTC_WAKEUP, earlyCalendar.getTimeInMillis(), earlyPendingIntent);

                    Log.d("Early ALARM SET ", "Calendar: " + earlyCalendar.getTimeInMillis() + "   " + earlyTheDateTime.getHourOfDay());
                    Log.d("Early ALARM SET ", "DateTime: " + earlyTheDateTime.getMillis());
                }

                Intent myIntent = new Intent(NewDate.this, MainActivity.class);
                NewDate.this.startActivity(myIntent);

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

            String strHrsToShow = (datetime.get(Calendar.HOUR) == 0) ? "12" : datetime.get(Calendar.HOUR) + "";
            if (-1 < minute && minute < 10) {
                dateText.setText(strHrsToShow + ":0" + datetime.get(Calendar.MINUTE) + " " + am_pm);
            } else {
                dateText.setText(strHrsToShow + ":" + datetime.get(Calendar.MINUTE) + " " + am_pm);
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
        /*
        Intent myIntent = new Intent(NewDate.this, SelectBackground.class);
        myIntent.putExtra("mother", "new");
        NewDate.this.startActivity(myIntent);
        */
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
            CountdownDate newDate = new CountdownDate(0, title, fmt.print(dt), repeat, earlyNot, "", timeChecked);

            Intent i = new Intent();
            Bundle bu = new Bundle();

            bu.putParcelable("COUNTDOWN_DATE", newDate);
            i.putExtras(bu);
            i.putExtra("mother", "new");
            i.setClass(NewDate.this, SelectBackground.class);
            startActivity(i);
        }else{
            Toast.makeText(NewDate.this, "Internet connection required",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void showPopupWindow() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater.inflate(R.layout.select_background_pop_up, null);
        popupWindow = new PopupWindow(view, ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setContentView(view);

        cancelBtn = (Button) view.findViewById(R.id.btn_cancel);
        cancelBtn.setOnClickListener(this);
        Button galleryBtn = (Button) view.findViewById(R.id.btn_gallery);
        galleryBtn.setOnClickListener(this);
        Button onlineBtn = (Button) view.findViewById(R.id.btn_online);
        onlineBtn.setOnClickListener(this);
        Drawable d = new ColorDrawable(Color.WHITE);
        d.setAlpha(130);
        //popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);
        getWindow().setBackgroundDrawable(d);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                Drawable d = new ColorDrawable(Color.WHITE);
                getWindow().setBackgroundDrawable(d);
            }
        });

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btn_cancel:
                if (popupWindow != null & popupWindow.isShowing()) {
                    popupWindow.dismiss();
                    popupWindow = null;
                }
                break;
            case R.id.btn_gallery:
                // Create intent to Open Image applications like Gallery, Google Photos
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                // Start the Intent
                startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
                break;
            case R.id.btn_online:
                Intent myIntent = new Intent(NewDate.this, SelectBackground.class);
                NewDate.this.startActivity(myIntent);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data
                Uri selectedImageUri = data.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                bitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, false);
                if (null != selectedImageUri) {
                    // Get the path from the Uri
                    String path = getRealPathFromURI(selectedImageUri);
                    imageName = path;
                    // Set the image in ImageView
                    backgroundImage.setImageBitmap(bitmap);
                    popupWindow.dismiss();
                    popupWindow = null;
                /*
                imageName = getRealPathFromURI(selectedImage);

                Log.d("ImageName :: ", imageName);

                if (imageName != null) {
                    popupWindow.dismiss();
                    popupWindow = null;
                    Uri uri = Uri.fromFile(new File(imageName));
                    Log.d("URI :: ", "" + uri);
                    Picasso.with(NewDate.this)
                            .load(uri)
                            .memoryPolicy(MemoryPolicy.NO_CACHE)
                            .networkPolicy(NetworkPolicy.NO_CACHE)
                            //.resize(200, 200)
                            .config(Bitmap.Config.RGB_565)
                            .fit()
                            .centerCrop()
                            .placeholder(R.drawable.error)
                            .into(backgroundImage);
                } else {
                    backgroundImage.setImageResource(R.drawable.placeholder);
                }

            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }*/
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

    }

    // And to convert the image URI to the direct file system path of the image file
    public String getRealPathFromURI(Uri uri) {

        // just some safety built in
        if( uri == null ) {
            // TODO perform some logging or show user feedback
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if( cursor != null ){
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        // this is our fallback here
        return uri.getPath();
    }

}
