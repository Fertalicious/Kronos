package com.industries.fertile.countdowner;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.SyncStateContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class View_Date extends AppCompatActivity {

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
        //targetDateText.setText(targetDate.getDateTime());
        if(theDate.getTime()==1) {
            dateTime.setText(tdTextWithTime);
        } else {
            dateTime.setText(tdTextWithoutTime);
        }

        //countdown.setText(PeriodFormat.getDefault().print(period));

        //String datetimeString = weeksBetween.getWeeks() + " weeks " + period.getDays() + " days " + period.getHours() + " hours " + period.getMinutes() + " minutes " + period.getSeconds() + " seconds";
        //countdown.setText(datetimeString);

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

        /*
        Spannable span = new SpannableString(countdown.getText());
        span.setSpan(new RelativeSizeSpan(1.5f), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        countdown.setText(span);
        */
    }

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

        return super.onOptionsItemSelected(item);
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

}
