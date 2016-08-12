package com.industries.fertile.countdowner;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.SyncStateContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.LocalDateTime;
import org.joda.time.Minutes;
import org.joda.time.Months;
import org.joda.time.Period;
import org.joda.time.Seconds;
import org.joda.time.Weeks;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.PeriodFormat;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class View_Date extends AppCompatActivity implements View.OnClickListener {

    CountdownDate theDate;
    TextView countdown;
    PeriodFormatter periodFormat;
    Period period;
    LocalDateTime calendarDateTime;
    DateTime dt;
    private Handler handler;
    private Runnable runnable;
    Calendar rightNow;
    LocalDateTime localDateTime;
    String currentFormatString;
    int currentFormat;

    CountdownDate dateToDelete;
    DBHandler db;
    PopupWindow popupWindow;
    Button cancelBtn;
    Button deleteBtn;
    RelativeLayout mainLayout;

    ImageView test;

    RelativeLayout view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view__date);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Back button
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        TextView titleText = (TextView) findViewById(R.id.titleTextView);
        TextView dateTime = (TextView) findViewById(R.id.dateTimeTextView);
        countdown = (TextView) findViewById(R.id.countdownTextView);

        currentFormat = 10;

        Bundle b = this.getIntent().getExtras();
        if (b != null) {
            theDate = b.getParcelable("COUNTDOWN_DATE");
        }
        else {
            theDate = new CountdownDate();
        }

        dateToDelete = theDate;
        db = new DBHandler(this);
        mainLayout = new RelativeLayout(this);

        localDateTime = new LocalDateTime();
        rightNow = Calendar.getInstance();
        calendarDateTime = localDateTime.fromCalendarFields(rightNow);
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd hh:mm:ss a");
        dt = formatter.parseDateTime(theDate.getDateTime());

        DateTimeFormatter formatter2 = DateTimeFormat.forPattern("MMMM dd, yyyy hh:mm a");
        String tdTextWithTime = formatter2.print(dt);

        DateTimeFormatter formatter3 = DateTimeFormat.forPattern("MMMM dd, yyyy");
        String tdTextWithoutTime = formatter3.print(dt);

        period = new Period(calendarDateTime.toDateTime(), dt);

        // fill the view
        titleText.setText(theDate.getTitle());
        if(theDate.getTime()==1) {
            dateTime.setText(tdTextWithTime);
        } else {
            dateTime.setText(tdTextWithoutTime);
        }

        periodFormat = new PeriodFormatterBuilder()
                .appendYears()
                .appendSuffix(" year", " years")
                .appendSeparator(" ")
                .appendMonths()
                .appendSuffix(" month", " months")
                .appendSeparator(" ")
                .appendWeeks()
                .appendSuffix(" week", " weeks")
                .appendSeparator(" ")
                .appendDays()
                .appendSuffix(" day", " days")
                .appendSeparator(" ")
                .appendHours()
                .appendSuffix(" hour", " hours")
                .appendSeparator(" ")
                .appendMinutes()
                .appendSuffix(" minute", " minutes")
                .appendSeparator(" ")
                .appendSeconds()
                .appendSuffix(" second", " seconds")
                .toFormatter();

        currentFormatString = periodFormat.print(period);
        handler = new Handler();
        runnable = new Runnable() {
            int test;
            @Override
            public void run() {
                //update text every second
                setCountdownText(currentFormatString);
                Log.d("handler is working: : ", test++ + "");
                handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(runnable,1000);

        if(theDate.getBackground() != null){
            Uri uri = Uri.fromFile(new File(theDate.getBackground()));
            Picasso.with(this)
                    .load(uri)
                    .into(target);
        }

        view = (RelativeLayout)findViewById(R.id.customLayout);

    }

    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            Drawable drawable = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 2000, 2000, true));
            countdown.getRootView().setBackground(drawable);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    protected void onStop(){
        super.onStop();
        handler.removeCallbacks(runnable);
    }

    protected void onResume(){
        super.onResume();
        handler.postDelayed(runnable, 1);
    }

    private void setCountdownText(String countdownOutput){
        textLimiter(countdownOutput);
        countdown.setText(ssb);
        countdown.setTextColor(Color.rgb(255, 255, 255));
        ssb = new SpannableStringBuilder();
        rightNow = Calendar.getInstance();
        calendarDateTime = localDateTime.fromCalendarFields(rightNow);
        period = new Period(calendarDateTime.toDateTime(), dt);
        localDateTime = new LocalDateTime();
        if(currentFormat == 0){
            Seconds secondsBetween = Seconds.secondsBetween(calendarDateTime.toDateTime(), dt);
            currentFormatString = secondsBetween.getSeconds() + " seconds";
        }else if(currentFormat == 1) {
            Minutes minutesBetween = Minutes.minutesBetween(calendarDateTime.toDateTime(), dt);
            currentFormatString = minutesBetween.getMinutes() + " minutes " + period.getSeconds() + " seconds";
        }else if(currentFormat == 2){
            Hours hoursBetween = Hours.hoursBetween(calendarDateTime.toDateTime(), dt);
            currentFormatString = hoursBetween.getHours() + " hours " + period.getMinutes() + " minutes " + period.getSeconds() + " seconds";
        }else if(currentFormat == 3){
            Days daysBetween = Days.daysBetween(calendarDateTime.toDateTime(), dt);
            currentFormatString = daysBetween.getDays() + " days " + period.getHours() + " hours " + period.getMinutes() + " minutes " + period.getSeconds() + " seconds";
        }else if(currentFormat == 4){
            Weeks weeksBetween = Weeks.weeksBetween(calendarDateTime.toDateTime(), dt);
            currentFormatString = weeksBetween.getWeeks() + " weeks " + period.getDays() + " days " + period.getHours() + " hours " + period.getMinutes() + " minutes " + period.getSeconds() + " seconds";
        }else if(currentFormat == 5){
            Months monthsBetween = Months.monthsBetween(calendarDateTime.toDateTime(), dt);
            currentFormatString = monthsBetween.getMonths() + " months " + period.getWeeks() + " weeks " + period.getDays() + " days " + period.getHours() + " hours " + period.getMinutes() + " minutes " + period.getSeconds() + " seconds";
        }else if(currentFormat == 6){
            Years yearsBetween = Years.yearsBetween(calendarDateTime.toDateTime(), dt);
            currentFormatString = yearsBetween.getYears() + " years " + period.getMonths() + " months " + period.getWeeks() + " weeks " + period.getDays() + " days " + period.getHours() + " hours " + period.getMinutes() + " minutes " + period.getSeconds() + " seconds";
        }else if(currentFormat == 10){
            currentFormatString = periodFormat.print(period);
        }
    }

    SpannableStringBuilder ssb = new SpannableStringBuilder();

    private void textLimiter(String input){
        String[] parts = input.split(" ");
        for(int i = 0; i < parts.length-1; i++){
            if((i + 3) < parts.length-1){
                ssb.append(parts[i]);

                if(i==0)
                    ssb.setSpan(new RelativeSizeSpan(1.5f), 0, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                else
                    ssb.setSpan(new RelativeSizeSpan(1.5f), ssb.length()-parts[i].length(), ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                ssb.append(" " + parts[i+1]);
                ssb.append(" " + parts[i + 2]);
                ssb.setSpan(new RelativeSizeSpan(1.5f), ssb.length() - parts[i + 2].length(), ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                ssb.append(" " + parts[i + 3] + "\n");
                i += 3;
            }else{
                ssb.append(parts[i]);
                if(i==0)
                    ssb.setSpan(new RelativeSizeSpan(1.5f), 0, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                else
                    ssb.setSpan(new RelativeSizeSpan(1.5f), ssb.length()-parts[i].length(), ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                ssb.append(" " + parts[i+1]);
                i++;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_viewdate, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_edit) {
            // Pass the countdate object to editdate

            Intent i = new Intent();
            Bundle bu = new Bundle();

            bu.putParcelable("COUNTDOWN_DATE", theDate);
            i.putExtras(bu);
            i.setClass(View_Date.this, EditDate.class);
            startActivity(i);
            return true;
        }


        if (id == R.id.action_precision) {
            showFilterPopup(findViewById(R.id.action_precision));
        }

        if(id == R.id.action_share){
            if(hasPermission("android.permission.READ_EXTERNAL_STORAGE") && hasPermission("android.permission.WRITE_EXTERNAL_STORAGE")){
                takeScreenshot();
            }else if(canMakeSmores()) {
                String[] perms = {"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"};

                int permsRequestCode = 200;

                requestPermissions(perms, permsRequestCode);
            }
        }

        if(id == R.id.action_delete) {
            showPopupWindow(dateToDelete);
        }

        return super.onOptionsItemSelected(item);
    }

    private void showPopupWindow(CountdownDate date){
        dateToDelete = date;
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater.inflate(R.layout.content_pop_up, null);
        popupWindow = new PopupWindow(view, ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setContentView(view);
        TextView titleText = (TextView) view.findViewById(R.id.titleTextView);
        titleText.setText("Delete the event " + date.getTitle() + "?");
        cancelBtn = (Button) view.findViewById(R.id.btn_cancel);
        cancelBtn.setOnClickListener(this);
        deleteBtn = (Button) view.findViewById(R.id.btn_delete);
        deleteBtn.setOnClickListener(this);
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

    private boolean canMakeSmores(){

        return(Build.VERSION.SDK_INT>Build.VERSION_CODES.LOLLIPOP_MR1);

    }

    private boolean hasPermission(String permission){

        if(canMakeSmores()){

            return(checkSelfPermission(permission)==PackageManager.PERMISSION_GRANTED);

        }

        return true;

    }


    @Override

    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults){

        switch(permsRequestCode){

            case 200:

                boolean readAccepted = grantResults[0]==PackageManager.PERMISSION_GRANTED;

                boolean writeAccepted = grantResults[1]==PackageManager.PERMISSION_GRANTED;

                takeScreenshot();

                break;

        }

    }
    private void takeScreenshot() {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";

            // create bitmap screen capture
            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);
            Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0,325,bitmap.getWidth(), bitmap.getHeight()-500);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            openScreenshot(imageFile);
        } catch (Throwable e) {
            // Several error may come out with file handling or OOM
            e.printStackTrace();
        }
    }

    private void openScreenshot(File imageFile) {
        /*
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(imageFile);
        intent.setDataAndType(uri, "image/*");
        startActivity(intent);*/
        Uri uri = Uri.fromFile(imageFile);
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/jpeg");
            // shareIntent.putExtra(Intent.EXTRA_SUBJECT, "KRONOS Countdown!");
            // shareIntent.putExtra(Intent.EXTRA_TEXT, "KRONOS Countdown!");
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(shareIntent, "Share Via"));
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    // Display anchored popup menu based on view selected
    private void showFilterPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        // Inflate the menu from xml
        popup.getMenuInflater().inflate(R.menu.popup_precision, popup.getMenu());
        // Setup menu item selection
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_seconds:
                        currentFormat = 0;
                        Seconds secondsBetween = Seconds.secondsBetween(calendarDateTime.toDateTime(), dt);
                        currentFormatString = secondsBetween.getSeconds() + " seconds";
                        return true;
                    case R.id.menu_minutes:
                        currentFormat = 1;
                        return true;
                    case R.id.menu_hours:
                        currentFormat = 2;
                        return true;
                    case R.id.menu_days:
                        currentFormat = 3;
                        return true;
                    case R.id.menu_weeks:
                        currentFormat = 4;
                        Weeks weeksBetween = Weeks.weeksBetween(calendarDateTime.toDateTime(), dt);
                        currentFormatString = weeksBetween.getWeeks() + " weeks " + period.getDays() + " days " + period.getHours() + " hours " + period.getMinutes() + " minutes " + period.getSeconds() + " seconds";
                        return true;
                    case R.id.menu_months:
                        currentFormat = 5;
                        return true;
                    case R.id.menu_years:
                        currentFormat = 6;
                        return true;
                    case R.id.menu_default:
                        currentFormat = 10;
                        return true;
                    default:
                        return false;
                }
            }
        });
        // Handle dismissal with: popup.setOnDismissListener(...);
        // Show the menu
        popup.show();
    }


    @Override
    public void onClick(View v) {

        switch(v.getId()){

            case R.id.btn_cancel:
                if(popupWindow!=null & popupWindow.isShowing()){
                    popupWindow.dismiss();
                    popupWindow=null;
                }
                break;
            case R.id.btn_delete:
                db.deleteDate(dateToDelete);
                Intent itAlarm = new Intent("NOTIFICATION_SERVICE");
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this,dateToDelete.getId(),itAlarm,0);
                AlarmManager theAlarm = (AlarmManager) getSystemService(ALARM_SERVICE);
                theAlarm.cancel(pendingIntent);
                if(dateToDelete.getFavorite() != 0){
                    Intent earlyItAlarm = new Intent(dateToDelete.getId() + "earlyNot");
                    PendingIntent earlyPendingIntent = PendingIntent.getBroadcast(this,dateToDelete.getId()+1,earlyItAlarm,0);
                    AlarmManager earlyTheAlarm = (AlarmManager) getSystemService(ALARM_SERVICE);
                    earlyTheAlarm.cancel(earlyPendingIntent);
                }
                popupWindow.dismiss();
                popupWindow=null;
                finish();
                Intent myIntent = new Intent(View_Date.this, MainActivity.class);
                startActivity(myIntent);
                break;
        }
    }

}
