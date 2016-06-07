package com.industries.fertile.countdowner;

import android.os.Parcel;
import android.os.Parcelable;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.joda.time.Minutes;
import org.joda.time.Seconds;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by Kyle on 5/17/2016.
 */
public class CountdownDate implements Parcelable{

    private int id;
    private String title;
    private String dateTime;
    private int repeat;
    private int favorite;
    private String background;
    private int time;

    public CountdownDate()
    {

    }

    public CountdownDate(int id, String title, String date, int repeat, int favorite, String background, int time) {
        this.id = id;
        this.title = title;
        this.dateTime = date;
        this.repeat = repeat;
        this.favorite = favorite;
        this.background = background;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String date) {
        this.dateTime = date;
    }

    public int getRepeat() {
        return repeat;
    }

    public void setRepeat(int repeat) {
        this.repeat = repeat;
    }

    public int getFavorite() {
        return favorite;
    }

    public void setFavorite(int favorite) {
        this.favorite = favorite;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(dateTime);
        dest.writeInt(repeat);
        dest.writeInt(favorite);
        dest.writeString(background);
        dest.writeInt(time);
    }

    public static final Parcelable.Creator<CountdownDate> CREATOR
            = new Parcelable.Creator<CountdownDate>() {
        public CountdownDate createFromParcel(Parcel in) {
            CountdownDate date = new CountdownDate();
            date.id = in.readInt();
            date.title = in.readString();
            date.dateTime = in.readString();
            date.repeat = in.readInt();
            date.favorite = in.readInt();
            date.background = in.readString();
            date.time = in.readInt();
            return date;
        }

        public CountdownDate[] newArray(int size) {
            return new CountdownDate[size];
        }
    };

    public int compareTo(CountdownDate dateToCompare){
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd hh:mm:ss a");
        DateTime thisDateTime = formatter.parseDateTime(this.getDateTime());
        DateTime dateToCompareDateTime = formatter.parseDateTime(dateToCompare.getDateTime());
        LocalDateTime localDateTime = new LocalDateTime();
        Calendar rightNow = Calendar.getInstance();
        LocalDateTime calendarDateTime = localDateTime.fromCalendarFields(rightNow);

        Seconds secondsBetweenThisDate = Seconds.secondsBetween(calendarDateTime.toDateTime(), thisDateTime);
        Seconds secondsBetweenDateToCompare = Seconds.secondsBetween(calendarDateTime.toDateTime(), dateToCompareDateTime);

        if (secondsBetweenThisDate.isGreaterThan(secondsBetweenDateToCompare)){
            return 1;
        } else if (secondsBetweenThisDate.isLessThan(secondsBetweenDateToCompare)){
            return -1;
        } else {
            return 0;
        }
    }
}
