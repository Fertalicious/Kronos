package com.industries.fertile.countdowner;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.Minutes;
import org.joda.time.Months;
import org.joda.time.Period;
import org.joda.time.Weeks;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.PeriodFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private List<CountdownDate> myDates;

    private PopupWindow popupWindow;
    private Button cancelBtn;
    private Button deleteBtn;

    RelativeLayout mainLayout;

    DBHandler db;
    SwipeMenuListView dateList;
    Calendar rightNow;

    CountdownDate dateToDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mainLayout = new RelativeLayout(this);

        dateList = (SwipeMenuListView) findViewById(R.id.listView);
        dateList.setMenuCreator(creator);

        db = new DBHandler(this);
        myDates = db.getAllDates();

        rightNow = Calendar.getInstance();
        LocalDateTime localDateTime = new LocalDateTime();
        LocalDateTime calendarDateTime = localDateTime.fromCalendarFields(rightNow);
        Log.d("calendarDateTime: : ", calendarDateTime.toString());
        Log.d("ToString: : ", calendarDateTime.toDateTime().toString());
        populateListView();
        registerClickCallback();

        dateList.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                CountdownDate clickedDate = myDates.get(position);
                switch (index) {
                    case 0:
                        // delete
                        showPopupWindow(clickedDate);
                        break;
                    case 1:
                        // possible expansion room
                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });

        // Left
        dateList.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);

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
                popupWindow.dismiss();
                popupWindow=null;
                finish();
                Intent myIntent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(myIntent);
                break;
        }
    }

    private void populateListView(){
        ArrayAdapter<CountdownDate> adapter = new MyListAdapter();
        adapter.sort(new Comparator<CountdownDate>() {
            @Override
            public int compare(CountdownDate lhs, CountdownDate rhs) {
                return lhs.compareTo(rhs);
            }
        });
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
            if (weeksBetween.getWeeks() > 0){
                if (weeksBetween.getWeeks() == 1 || weeksBetween.getWeeks() == -1)
                    dateTimeText.setText("" + weeksBetween.getWeeks() + " week");
                else
                    dateTimeText.setText("" + weeksBetween.getWeeks() + " weeks");
            }
            else if (weeksBetween.getWeeks() == 0 ||daysBetween.getDays() < -1) {
                if (daysBetween.getDays() == 1 || daysBetween.getDays() == -1)
                    dateTimeText.setText("" + daysBetween.getDays() + " day");
                else
                    dateTimeText.setText("" + daysBetween.getDays() + " days");
            }else {
                if (hoursBetween.getHours() == 1 || hoursBetween.getHours() == -1)
                    if(targetDate.getTime()==1)
                        dateTimeText.setText(hoursBetween.getHours() + " hour");
                    else
                        dateTimeText.setText("today");
                else {
                    if (targetDate.getTime() == 1)
                        dateTimeText.setText(hoursBetween.getHours() + " hours");
                    else
                        dateTimeText.setText("today");
                }
            }

/*
            String log = localDateTime.toString();
            Log.d("local date time: : ", log);
*/
            return itemView;
        }
    }


    private void registerClickCallback(){
        SwipeMenuListView list = (SwipeMenuListView) findViewById(R.id.listView);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {

                CountdownDate clickedDate = myDates.get(position);

                Intent i = new Intent();
                Bundle b = new Bundle();

                b.putParcelable("COUNTDOWN_DATE", clickedDate);
                i.putExtras(b);
                i.setClass(MainActivity.this, View_Date.class);
                startActivity(i);
            }
        });
    }


    SwipeMenuCreator creator = new SwipeMenuCreator() {

        @Override
        public void create(SwipeMenu menu) {
            // create "delete" item
            SwipeMenuItem deleteItem = new SwipeMenuItem(
                    getApplicationContext());
            // set item background
            deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                    0x3F, 0x25)));
            // set item width
            deleteItem.setWidth((280));
            // set a icon
            deleteItem.setIcon(android.R.drawable.ic_menu_delete);
            // add to menu
            menu.addMenuItem(deleteItem);
        }
    };

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

}
