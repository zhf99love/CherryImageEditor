package com.cherry.mr.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cherry.mr.cherryimageeditor.CherryApp;
import com.cherry.mr.cherryimageeditor.R;
import com.cherry.mr.utils.FrescoImgUtils;

import java.util.ArrayList;
import java.util.List;

import photodraweeview.PhotoDraweeView;

public class ImageDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private FloatingActionButton fab;
    private MultiTouchViewPager imageGallery;
    private List<PhotoDraweeView> draweeViewList;
    private DisplayMetrics metric;

    private TextView information;

    private int imagePosition;

    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            imageGallery.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            fab.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mVisible = true;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //设置是否有返回箭头
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initView();

        delayedHide(500);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //重写ToolBar返回按钮的行为，防止重新打开父Activity重走生命周期方法
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initView() {
        imagePosition = getIntent().getIntExtra("position", 0);
        draweeViewList = new ArrayList<>();
        imageGallery = (MultiTouchViewPager) findViewById(R.id.image_gallery);
        information = (TextView) findViewById(R.id.information);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        for (int i = 0; i < CherryApp.listBeans.size(); i++) {
            PhotoDraweeView draweeView = new PhotoDraweeView(ImageDetailActivity.this);
            draweeView.setOnClickListener(this);
            draweeViewList.add(draweeView);
        }

        metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);

        imageGallery.setAdapter(new PagerAdapter() {

            @Override
            public int getCount() {
                return CherryApp.listBeans.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                FrescoImgUtils.displayBigRectImage("file://" + CherryApp.listBeans.get(position).path,
                        draweeViewList.get(position),  metric.heightPixels > 4000);
//                        draweeViewList.get(position), metric.widthPixels, metric.heightPixels);
                container.addView(draweeViewList.get(position));
                return draweeViewList.get(position);
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(draweeViewList.get(position));
            }
        });

        //ViewPager属性设置
        imageGallery.setOffscreenPageLimit(5);
        imageGallery.setCurrentItem(imagePosition);
        information.setText("名称 : " + CherryApp.listBeans.get(imagePosition).imgName +
                "\n地址 : " + CherryApp.listBeans.get(imagePosition).parentName +
                "\nwidth : " + CherryApp.listBeans.get(imagePosition).width + "px\t\t height : " + CherryApp.listBeans.get(imagePosition).height + "px");
        getSupportActionBar().setTitle("图片" + (getIntent().getIntExtra("position", 0) + 1) + " / " + CherryApp.listBeans.size());
        imageGallery.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                imagePosition = position;
                information.setText("名称 : " + CherryApp.listBeans.get(position).imgName +
                        "\n地址 : " + CherryApp.listBeans.get(position).parentName +
                        "\nwidth : " + CherryApp.listBeans.get(position).width + "px\t\t height : " + CherryApp.listBeans.get(position).height + "px");
                getSupportActionBar().setTitle("图片 " + (position + 1) + " / " + CherryApp.listBeans.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "是否编辑图片", Snackbar.LENGTH_LONG)
                        .setAction("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent in = new Intent(ImageDetailActivity.this, EditorImageActivity.class);
                                in.putExtra("tempImage", CherryApp.listBeans.get(imagePosition));
                                startActivity(in);
                            }
                        }).show();
            }
        });
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        fab.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        imageGallery.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    @Override
    public void onClick(View v) {
        toggle();
    }
}
