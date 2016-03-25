package com.cherry.mr.com.cherry.mr.ui;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cherry.mr.cherryimageeditor.ImageBean;
import com.cherry.mr.cherryimageeditor.R;
import com.cherry.mr.touchview.TouchImageView;
import com.cherry.mr.utils.BitmapUtil;
import com.cherry.mr.utils.ImageTools;
import com.cherry.mr.utils.UIUtil;
import com.cherry.mr.utils.Utils;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;

public class EditorImageActivity extends AppCompatActivity implements View.OnClickListener{

    private TouchImageView touchImageView;

    private ImageBean imageBean;

    private ContentLoadingProgressBar progress_bar;

    private FrameLayout menu_layout;

    private TextView filter_change;
    private TextView cancel_filter;

    private LinearLayout filterLayout;

    private TextView cut_change;

    /**
     * 加滤镜任务
     */
    private AsyncTask<GPUImageFilter, Void, Void> filterTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor_image);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("编辑图片");
        setSupportActionBar(toolbar);
        //设置是否有返回箭头
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initView();

        FilterRecycleAdapter.initFilter(EditorImageActivity.this);
    }

    private void initView() {
        final DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        imageBean = (ImageBean) getIntent().getSerializableExtra("tempImage");
        touchImageView = (TouchImageView) findViewById(R.id.touch_image);
        progress_bar = (ContentLoadingProgressBar) findViewById(R.id.progress_bar);
        touchImageView.setZoomToOriginalSize(true);

        //menu相关
        menu_layout = (FrameLayout) findViewById(R.id.menu_layout);

        filter_change = (TextView) findViewById(R.id.filter_change);
        cancel_filter = (TextView) findViewById(R.id.cancel_filter);
        cancel_filter.setOnClickListener(this);
        /**
         * 滤镜变换的添加
         */
        filter_change.setOnClickListener(this);

        cut_change = (TextView) findViewById(R.id.cut_change);
        cut_change.setOnClickListener(this);

        //显示图片
        new AsyncTask<Void, Void, Void>(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progress_bar.show();
            }

            @Override
            protected Void doInBackground(Void... params) {
                imageBean.originalBitmap = ImageTools.convertToBitmap(imageBean.path, metric);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                touchImageView.setImageBitmap(imageBean.originalBitmap);
                progress_bar.hide();
            }
        }.execute();

    }

    /**
     * 更改滤镜方法
     * @param filter 滤镜类型
     */
    private void executeChange(GPUImageFilter filter) {
        filterTask = new AsyncTask<GPUImageFilter, Void, Void>() {

            GPUImageFilter filter;

            @Override
            protected Void doInBackground(GPUImageFilter... params) {
                filter = params[0];
                // 使用GPUImage处理图像
                GPUImage gpuImage = new GPUImage(EditorImageActivity.this);
                gpuImage.setImage(imageBean.originalBitmap);
                gpuImage.setFilter(filter);
                imageBean.tempBitmap = gpuImage.getBitmapWithFilterApplied();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                progress_bar.hide();
                cancel_filter.setVisibility(View.VISIBLE);
                touchImageView.setImageBitmap(imageBean.tempBitmap);
            }
        };
        filterTask.execute(filter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //重写ToolBar返回按钮的行为，防止重新打开父Activity重走生命周期方法
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_save:
                if(imageBean.tempBitmap == null) {
                    UIUtil.showToast(EditorImageActivity.this, "还没有进行修改呢");
                    break;
                }
                new AsyncTask<Void, Void, Void>(){
                    @Override
                    protected Void doInBackground(Void... params) {
                        ImageTools.saveBitmap("Img" + System.currentTimeMillis(), imageBean.tempBitmap);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        UIUtil.showToast(EditorImageActivity.this, "保存成功~文件在/CherryImg/文件夹下");
                    }
                }.execute();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel_filter:
                touchImageView.setImageBitmap(imageBean.originalBitmap);
                cancel_filter.setVisibility(View.GONE);
                break;
            case R.id.filter_change:
                ImageView backView = new ImageView(EditorImageActivity.this);
                backView.setImageResource(R.drawable.back);
                backView.setBackground(ContextCompat.getDrawable(EditorImageActivity.this, R.drawable.press_state_bg));
                backView.setScaleType(ImageView.ScaleType.CENTER);
                backView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        menu_layout.removeView(filterLayout);
                    }
                });

                filterLayout = new LinearLayout(EditorImageActivity.this);
                filterLayout.setOrientation(LinearLayout.HORIZONTAL);
                filterLayout.setBackgroundColor(ContextCompat.getColor(EditorImageActivity.this, R.color.normal_grey));
                filterLayout.addView(backView, Utils.dpToPx(60, getResources()), LinearLayout.LayoutParams.MATCH_PARENT);

                RecyclerView recyclerView = new RecyclerView(EditorImageActivity.this);
                recyclerView.setLayoutManager(new LinearLayoutManager(EditorImageActivity.this, LinearLayoutManager.HORIZONTAL, false));
                recyclerView.setBackgroundColor(ContextCompat.getColor(EditorImageActivity.this, R.color.normal_grey));
                recyclerView.setAdapter(new FilterRecycleAdapter(EditorImageActivity.this) {

                    @Override
                    void onFilterClick(GPUImageFilter filter) {
                        progress_bar.show();
                        executeChange(filter);
                    }

                });
                filterLayout.addView(recyclerView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

                menu_layout.addView(filterLayout, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

                break;
            case R.id.cut_change:
                UIUtil.showToast(EditorImageActivity.this, "蠢逼，还没想怎么实现呢，点你妈比!");
                break;
        }
    }
}
