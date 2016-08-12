package com.industries.fertile.countdowner;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            DBHandler db = new DBHandler(context);
            Log.i("ALARM", "MESSAGE RECEIVED");
            CountdownDate date = intent.getParcelableExtra("CountdownDate");
            int early = intent.getIntExtra("early", 4);
            String log2 = "Id: " + date.getId() + " , Title: " + date.getTitle() + " , DateTime: " + date.getDateTime() +
                    " , Repeat: " + date.getRepeat() + " , EarlyNot: " + date.getFavorite() + " , Background: " + date.getBackground();
            Log.d("date from receiver: : ", log2);
            DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd hh:mm:ss a");
            DateTime dt = formatter.parseDateTime(date.getDateTime());

            DateTimeFormatter formatter2 = DateTimeFormat.forPattern("MMMM dd, yyyy hh:mm a");
            String tdTextWithTime = formatter2.print(dt);

            DateTimeFormatter formatter3 = DateTimeFormat.forPattern("MMMM dd, yyyy");
            String tdTextWithoutTime = formatter3.print(dt);

            Intent it =  new Intent(context, MainActivity.class);
            if(early == 0){
                if(date.getTime()==1) {
                    createNotification(context, it, "Reminder!", date.getTitle() + " is now!", tdTextWithTime);
                } else {
                    createNotification(context, it, "Reminder!", date.getTitle() + " is now!", tdTextWithoutTime);
                }
                CountdownDate repeatDateCD = date;
                DateTime repeatDate = dt;

                // Delete the date without repeat if the option to delete old dates is on
                SharedPreferences prefs = context.getSharedPreferences("MyPrefsFile", context.MODE_PRIVATE);
                int deleteOrNot = prefs.getInt("delete", 0); //0 is the default value.
                if(date.getRepeat() == 0 && deleteOrNot == 1){
                    db.deleteDate(date);
                }

                if (date.getRepeat()!= 0 && deleteOrNot == 0) {
                    if (date.getRepeat() == 1) {
                        repeatDate = dt.plusDays(1);
                    } else if (date.getRepeat() == 2) {
                        repeatDate = dt.plusWeeks(1);
                    } else if (date.getRepeat() == 3) {
                        repeatDate = dt.plusMonths(1);
                    } else if (date.getRepeat() == 4) {
                        repeatDate = dt.plusYears(1);
                    }

                    date.setDateTime(formatter.print(repeatDate));
                    String logeroo = "Id: " + date.getId() + " , Title: " + date.getTitle() + " , DateTime: " + date.getDateTime() +
                            " , Repeat: " + date.getRepeat() + " , EarlyNot: " + date.getFavorite() + " , Background: " + date.getBackground();
                    Log.d("Updated? Date: : ", logeroo);
                    db.updateDate(date);

                    Log.d("Reading: ", "Reading all dates...");

                    List<CountdownDate> dates = db.getAllDates();

                    for (CountdownDate aDate : dates) {
                        String log = "Id: " + aDate.getId() + " , Title: " + aDate.getTitle() + " , DateTime: " + aDate.getDateTime() +
                                " , Repeat: " + aDate.getRepeat() + " , EarlyNot: " + aDate.getFavorite() + " , Background: " + aDate.getBackground();
                        Log.d("CountDown Date: : ", log);
                    }
                }
            }else if (early == 1){
                if(date.getTime()==1) {
                    createNotification(context, it, "Reminder!", date.getTitle() + " is in " + date.getFavorite() + " day(s)!", tdTextWithTime);
                } else {
                    createNotification(context, it, "Reminder!", date.getTitle() + " is in " + date.getFavorite() + " day(s)!", tdTextWithoutTime);
                }
            }


        } catch (Exception e) {
            Log.i("date", "error == " + e.getMessage());
        }
    }


    public void createNotification(Context context, Intent intent, CharSequence ticker, CharSequence title, CharSequence descricao) {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent p = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setTicker(ticker);
        builder.setContentTitle(title);
        builder.setContentText(descricao);
        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setContentIntent(p);
        Notification n = builder.build();
        //create the notification
        n.vibrate = new long[]{150, 300, 150, 400};
        n.flags = Notification.FLAG_AUTO_CANCEL;
        nm.notify(R.drawable.error, n);
        //create a vibration
        try {
            Uri som = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone toque = RingtoneManager.getRingtone(context, som);
            toque.play();
        } catch (Exception e) {
        }
    }
}
