package com.industries.fertile.countdowner;

/**
 * Created by Kyle on 5/17/2016.
 */
public class CountdownDate {
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
}
