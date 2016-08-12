package com.industries.fertile.countdowner;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.util.Calendar;
import java.util.List;

/**
 * Implementation of App Widget functionality.
 */
public class KronosWidget extends AppWidgetProvider {

    private static final String MyOnClick = "myOnClickTag";
    private static final String MyOnClick2 = "myOnClickTag2";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
        }
    }


    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    static private List<CountdownDate> myDates;
    static DBHandler db;
    //static Context theContext;
    static int position = 0;
    //static AppWidgetManager theAppWidgetManager;
    //static int theAppWidgetId;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.kronos_widget);
        //views.setTextViewText(R.id.appwidget_text_countdown, widgetText);
/*
        theContext = context;
        theAppWidgetManager = appWidgetManager;
        theAppWidgetId = appWidgetId;
*/
        db = new DBHandler(context.getApplicationContext());
        myDates = db.getAllDates();
        String countdownText;
        if (!myDates.isEmpty()) {
            CountdownDate selectedDate = myDates.get(position);

            countdownText = getCountdownText(selectedDate);
            Log.d("current countdown text:", countdownText);

            views.setTextViewText(R.id.appwidget_text_countdown, countdownText);

            DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd hh:mm:ss a");
            DateTime dt = formatter.parseDateTime(selectedDate.getDateTime());

            DateTimeFormatter formatter2 = DateTimeFormat.forPattern("MMMM dd, yyyy hh:mm a");
            String tdTextWithTime = formatter2.print(dt);

            DateTimeFormatter formatter3 = DateTimeFormat.forPattern("MMMM dd, yyyy");
            String tdTextWithoutTime = formatter3.print(dt);
            if(selectedDate.getTime()==1) {
                views.setTextViewText(R.id.appwidget_text_date, tdTextWithTime);
            } else {
                views.setTextViewText(R.id.appwidget_text_date, tdTextWithoutTime);
            }
            views.setTextViewText(R.id.appwidget_text_title, selectedDate.getTitle());


        }else{
            views.setTextViewText(R.id.appwidget_text_countdown, "Empty. Add dates in Chronos");
        }
/*
        Intent intentSync = new Intent(context, KronosWidget.class);
        intentSync.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE); //You need to specify the action for the intent. Right now that intent is doing nothing for there is no action to be broadcasted.
        PendingIntent pendingSync = PendingIntent.getBroadcast(context,0, intentSync, PendingIntent.FLAG_UPDATE_CURRENT); //You need to specify a proper flag for the intent. Or else the intent will become deleted.

        views.setOnClickPendingIntent(R.id.imageButton, pendingSync);
*/
        // Instruct the widget manager to update the widget

        Intent intent = new Intent(context, KronosWidget.class);
        intent.setAction(MyOnClick);
        PendingIntent pendingSync = PendingIntent.getBroadcast(context,0, intent, 0);
        views.setOnClickPendingIntent(R.id.imageButton, pendingSync);

        Intent intent2 = new Intent(context, KronosWidget.class);
        intent2.setAction(MyOnClick2);
        PendingIntent pendingSync2 = PendingIntent.getBroadcast(context,0, intent2, 0);
        views.setOnClickPendingIntent(R.id.buttonNext, pendingSync2);


        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    static private String getCountdownText(CountdownDate targetDate){
        String countdownText = "";

        LocalDateTime localDateTime = new LocalDateTime();
        Calendar rightNow = Calendar.getInstance();
        LocalDateTime calendarDateTime = localDateTime.fromCalendarFields(rightNow);
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd hh:mm:ss a");
        DateTime dt = formatter.parseDateTime(targetDate.getDateTime());

        Period period = new Period(calendarDateTime.toDateTime(), dt);

        String currentFormatString;
        Days daysBetween = Days.daysBetween(calendarDateTime.toDateTime(), dt);
        if (daysBetween.getDays()>0 || daysBetween.getDays()<0) {
            currentFormatString = daysBetween.getDays() + " days " + period.getHours() + " hours " + period.getMinutes() + " minutes ";
        }else if(period.getHours()>0 || period.getHours()<0){
            currentFormatString = period.getHours() + " hours " + period.getMinutes() + " minutes ";
        }else{
            currentFormatString = period.getMinutes() + " minutes ";
        }
        //-------------------------------------------------------------
        textLimiter(currentFormatString);
        countdownText = ssb.toString();
        //countdown.setTextColor(Color.rgb(255, 255, 255));
        ssb = new SpannableStringBuilder();
        rightNow = Calendar.getInstance();
        calendarDateTime = localDateTime.fromCalendarFields(rightNow);
        period = new Period(calendarDateTime.toDateTime(), dt);
        localDateTime = new LocalDateTime();
        //--------------------------------------------------------------

        return countdownText;
    }

    static SpannableStringBuilder ssb = new SpannableStringBuilder();

    static private void textLimiter(String input){
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
    public void onReceive(Context context, Intent intent) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.kronos_widget);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisAppWidget = new ComponentName(context.getPackageName(), KronosWidget.class.getName());
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
        if (MyOnClick.equals(intent.getAction())) {
            //your onClick action is here
            //db = new DBHandler(context.getApplicationContext());
            db = new DBHandler(context.getApplicationContext());
            myDates = db.getAllDates();
            String countdownText;
            if (!myDates.isEmpty()) {
                CountdownDate selectedDate = myDates.get(position);


                countdownText = getCountdownText(selectedDate);
                Log.d("current countdown text:", countdownText);

                views.setTextViewText(R.id.appwidget_text_countdown, countdownText);

                DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd hh:mm:ss a");
                DateTime dt = formatter.parseDateTime(selectedDate.getDateTime());

                DateTimeFormatter formatter2 = DateTimeFormat.forPattern("MMMM dd, yyyy hh:mm a");
                String tdTextWithTime = formatter2.print(dt);

                DateTimeFormatter formatter3 = DateTimeFormat.forPattern("MMMM dd, yyyy");
                String tdTextWithoutTime = formatter3.print(dt);
                if (selectedDate.getTime() == 1) {
                    views.setTextViewText(R.id.appwidget_text_date, tdTextWithTime);
                } else {
                    views.setTextViewText(R.id.appwidget_text_date, tdTextWithoutTime);
                }
                views.setTextViewText(R.id.appwidget_text_title, selectedDate.getTitle());


            }else {
                views.setTextViewText(R.id.appwidget_text_countdown, "Empty. Add dates in Chronos");
            }
            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetIds, views);
        }else if(MyOnClick2.equals(intent.getAction())){
            db = new DBHandler(context.getApplicationContext());
            myDates = db.getAllDates();
            position++;
            if (position > myDates.size()-1){
                position = 0;
            }
            Log.d("position : ", position + " ");
            //your onClick action is here

            String countdownText;
            if (!myDates.isEmpty()) {
                CountdownDate selectedDate = myDates.get(position);


                countdownText = getCountdownText(selectedDate);
                Log.d("current countdown text:", countdownText);

                views.setTextViewText(R.id.appwidget_text_countdown, countdownText);

                DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd hh:mm:ss a");
                DateTime dt = formatter.parseDateTime(selectedDate.getDateTime());

                DateTimeFormatter formatter2 = DateTimeFormat.forPattern("MMMM dd, yyyy hh:mm a");
                String tdTextWithTime = formatter2.print(dt);

                DateTimeFormatter formatter3 = DateTimeFormat.forPattern("MMMM dd, yyyy");
                String tdTextWithoutTime = formatter3.print(dt);
                if (selectedDate.getTime() == 1) {
                    views.setTextViewText(R.id.appwidget_text_date, tdTextWithTime);
                } else {
                    views.setTextViewText(R.id.appwidget_text_date, tdTextWithoutTime);
                }
                views.setTextViewText(R.id.appwidget_text_title, selectedDate.getTitle());


            }else {
                views.setTextViewText(R.id.appwidget_text_countdown, "Empty. Add dates in Chronos");
            }
            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetIds, views);

        }else{
            onUpdate(context, appWidgetManager, appWidgetIds);
        }


    }
}