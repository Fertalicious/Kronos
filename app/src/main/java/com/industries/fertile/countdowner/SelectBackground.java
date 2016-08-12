package com.industries.fertile.countdowner;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.provider.SyncStateContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.LocalDateTime;
import org.joda.time.Months;
import org.joda.time.Period;
import org.joda.time.Weeks;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Comparator;
import java.util.Date;

import javax.security.auth.callback.Callback;

public class SelectBackground extends AppCompatActivity {

    GridView theGrid;
    String imageName;
    private ProgressBar spinner;
    private TextView progressText;
    CountdownDate editDate;

    private static String[] IMAGE_URLS;

    private String convertImageName(String urlName){
        String simplifiedImageName = urlName.substring(16);
        return simplifiedImageName;
    }

    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        File myDir = new File(getFilesDir() + Environment.getExternalStorageDirectory().toString() + "/KronosImages");
                        if (!myDir.exists()) {
                            myDir.mkdirs();
                        }

                        String name = convertImageName(imageName);
                        String dirName = getFilesDir() + Environment.getExternalStorageDirectory().toString() + "/KronosImages" + name;
                        myDir = new File(myDir, name);
                        FileOutputStream out = new FileOutputStream(myDir);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);

                        out.flush();
                        out.close();
                        String mother = getIntent().getStringExtra("mother");
                        if(mother.equals("new")) {
                            /*
                            Intent intent = new Intent(SelectBackground.this, NewDate.class);
                            intent.putExtra("imageDir", dirName);
                            startActivity(intent);
                            */
                            editDate.setBackground(dirName);
                            Intent i = new Intent();
                            Bundle bu = new Bundle();
                            bu.putParcelable("COUNTDOWN_DATE", editDate);
                            i.putExtras(bu);
                            i.putExtra("imageDir", dirName);
                            i.setClass(SelectBackground.this, NewDate.class);
                            startActivity(i);
                        }else if (mother.equals("edit")){
                            editDate.setBackground(dirName);
                            Intent i = new Intent();
                            Bundle bu = new Bundle();
                            bu.putParcelable("COUNTDOWN_DATE", editDate);
                            i.putExtras(bu);
                            i.putExtra("imageDir", dirName);
                            i.setClass(SelectBackground.this, EditDate.class);
                            startActivity(i);
                        }
                    } catch(Exception e){
                        // some action
                        e.printStackTrace();
                    }
                }

            }).start();
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {}

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_background);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        IMAGE_URLS = getResources().getStringArray(R.array.background_resources_optimized);

        theGrid = (GridView) findViewById(R.id.gridView);
        theGrid.setAdapter(new ImageAdapter(this));



        theGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                /*
                Intent intent = new Intent(SelectBackground.this, NewDate.class);
                intent.putExtra("position", position);
                startActivity(intent);
                */
                spinner.setVisibility(View.VISIBLE);
                //progressText.setVisibility(View.VISIBLE);
                imageName = IMAGE_URLS[position];
                Picasso.with(SelectBackground.this)
                        .load(imageName)
                        .into(target);
                //spinner.setVisibility(View.GONE);
            }
        });

        spinner = (ProgressBar)findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);
        //progressText = (TextView)findViewById(R.id.progressTextView);

        // Back button
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Bundle b = this.getIntent().getExtras();
        if (b != null) {
            editDate = b.getParcelable("COUNTDOWN_DATE");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private static class ImageAdapter extends BaseAdapter {

        private LayoutInflater inflater;

        Context c;

        ImageAdapter(Context context) {
            inflater = LayoutInflater.from(context);
            c = context;

        }

        @Override
        public int getCount() {
            return IMAGE_URLS.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            View view = convertView;
            if (view == null) {
                view = inflater.inflate(R.layout.item_grid_image, parent, false);
                holder = new ViewHolder();
                assert view != null;

                holder.imageView = (ImageView) view.findViewById(R.id.image);

                //holder.progressBar = (ProgressBar) view.findViewById(R.id.progress);

                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            Picasso.with(c)
                    .load(IMAGE_URLS[position])
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.error)
                    .fit()
                    .into(holder.imageView);
/*
            , new Callback() {

                @Override
                public void onSuccess() {
                    holder.imageView.setVisibility(View.VISIBLE);
                    holder.progressBar.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onError() {
                    holder.progressBar.setVisibility(View.VISIBLE);
                    holder.imageView.setVisibility(View.INVISIBLE);
                }
            });
*/
            return view;
        }
    }

    static class ViewHolder {
        ImageView imageView;
        ProgressBar progressBar;
    }

}
