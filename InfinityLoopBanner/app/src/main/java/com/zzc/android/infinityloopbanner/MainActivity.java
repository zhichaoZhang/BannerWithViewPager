package com.zzc.android.infinityloopbanner;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.zzc.android.library.InfinityLoopViewPager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    InfinityLoopViewPager viewPager;
    List<String> urls = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Fresco.initialize(this.getApplicationContext());

        viewPager = (InfinityLoopViewPager) findViewById(R.id.banner);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                urls.add("http://img0.imgtn.bdimg.com/it/u=2771133351,3794508925&fm=21&gp=0.jpg");
                viewPager.setUrls(urls);
                Snackbar.make(view, "add one pager", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            }
        });

        urls.add("http://pic38.nipic.com/20140215/12359647_224250202132_2.jpg");
        urls.add("http://www.bz55.com/uploads/allimg/150309/139-150309101A8.jpg");
//        urls.add("http://img0.imgtn.bdimg.com/it/u=2771133351,3794508925&fm=21&gp=0.jpg");
    }


    @Override
    protected void onResume() {
        super.onResume();
        viewPager.setUrls(urls);
        viewPager.startLoop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        viewPager.stopLoop();
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
