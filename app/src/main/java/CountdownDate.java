/**
 * Created by Kyle on 5/17/2016.
 */
public class CountdownDate {
    private int id;
    private String title;
    private String date;
    private String time;
    private int repeat;
    private int favorite;
    private String background;

    public CountdownDate()
    {

    }

    public CountdownDate(int id, String title, String date, String time, int repeat, int favorite, String background) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.time = time;
        this.repeat = repeat;
        this.favorite = favorite;
        this.background = background;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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
}
