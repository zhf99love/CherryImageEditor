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
import android.view.Gravity;
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

public class EditorImageActivity extends AppCompatActivity implements View.OnClickListener {

    //普通模式
    public final static int MODE_NORMAL = 0;
    //滤镜模式
    public final static int MODE_FILTER = 1;
    //裁剪模式
    public final static int MODE_CUT = 2;

    //表示当前的模式
    private int mode = 0;
    //功能按钮 <裁剪，滤镜时使用>
    private TextView change_button;

    //图片主View
    private TouchImageView touchImageView;

    //图片信息类
    private ImageBean imageBean;

    private ContentLoadingProgressBar progress_bar;

    //底部菜单布局
    private FrameLayout menu_layout;

    //滤镜菜单开启按钮
    private TextView filter_change;

    //滤镜菜单
    private LinearLayout filterLayout;

    //裁剪菜单开启按钮
    private TextView cut_change;

    //裁剪菜单
    private LinearLayout cutLayout;

    //裁剪遮罩
    private ImageView cut_frame;

    //是否是圆形
    private boolean isOval = true;
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
        change_button = (TextView) findViewById(R.id.change_button);
        change_button.setOnClickListener(this);
        /**
         * 滤镜变换的添加
         */
        filter_change.setOnClickListener(this);

        cut_change = (TextView) findViewById(R.id.cut_change);
        cut_frame = (ImageView) findViewById(R.id.cut_frame);
        cut_change.setOnClickListener(this);

        //显示图片
        new AsyncTask<Void, Void, Void>() {
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
     *
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
                change_button.setVisibility(View.VISIBLE);
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
                if (imageBean.tempBitmap == null) {
                    UIUtil.showToast(EditorImageActivity.this, "还没有进行修改呢");
                    break;
                }
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        ImageTools.saveBitmap(EditorImageActivity.this, "Img" + System.currentTimeMillis(), imageBean.tempBitmap);
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
            case R.id.change_button:
                switch (mode) {
                    case MODE_FILTER:
                        touchImageView.setImageBitmap(imageBean.originalBitmap);
                        change_button.setVisibility(View.GONE);
                        break;
                    case MODE_CUT:
                        touchImageView.setDrawingCacheEnabled(true);
                        touchImageView.buildDrawingCache();
                        final Bitmap bitmap = touchImageView.getDrawingCache();
                        if (bitmap != null) {
                            int x = (bitmap.getWidth() - Utils.dpToPx(320, getResources())) / 2;
                            int y = (bitmap.getHeight() - Utils.dpToPx(320, getResources())) / 2;
                            Bitmap cutBitmap = Bitmap.createBitmap(bitmap, x, y, Utils.dpToPx(320, getResources()), Utils.dpToPx(320, getResources()));
                            cutBitmap = isOval ? ImageTools.toRoundBitmap(cutBitmap) : cutBitmap;
                            imageBean.tempBitmap = cutBitmap;
                            touchImageView.setImageBitmap(imageBean.tempBitmap);
                            touchImageView.destroyDrawingCache();
                        }
                        //初始化
                        menu_layout.removeView(cutLayout);
                        change_button.setVisibility(View.GONE);
                        cut_frame.setVisibility(View.GONE);
                        imageBean.originalBitmap = imageBean.tempBitmap;
                        mode = MODE_NORMAL;
                        break;
                }
                break;
            case R.id.filter_change:
                mode = MODE_FILTER;
                change_button.setText("取消滤镜");
                if (filterLayout == null) {
                    filterLayout = new LinearLayout(EditorImageActivity.this);
                    filterLayout.setOrientation(LinearLayout.HORIZONTAL);
                    filterLayout.setBackgroundColor(ContextCompat.getColor(EditorImageActivity.this, R.color.normal_grey));

                    ImageView backView = new ImageView(EditorImageActivity.this);
                    backView.setImageResource(R.drawable.back);
                    backView.setBackground(ContextCompat.getDrawable(EditorImageActivity.this, R.drawable.press_state_bg));
                    backView.setScaleType(ImageView.ScaleType.CENTER);
                    backView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            menu_layout.removeView(filterLayout);
                            change_button.setVisibility(View.GONE);
                            imageBean.originalBitmap = imageBean.tempBitmap;
                            mode = MODE_NORMAL;
                        }
                    });

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

                    filterLayout.addView(backView, Utils.dpToPx(60, getResources()), LinearLayout.LayoutParams.MATCH_PARENT);
                    filterLayout.addView(recyclerView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    filterLayout.setClickable(true);
                }

                menu_layout.addView(filterLayout, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

                break;
            case R.id.cut_change:
                mode = MODE_CUT;
                change_button.setText("裁剪");
                change_button.setVisibility(View.VISIBLE);
                cut_frame.setVisibility(View.VISIBLE);
                if (cutLayout == null) {
                    cutLayout = new LinearLayout(EditorImageActivity.this);
                    cutLayout.setOrientation(LinearLayout.HORIZONTAL);
                    cutLayout.setBackgroundColor(ContextCompat.getColor(EditorImageActivity.this, R.color.normal_grey));

                    ImageView backView = new ImageView(EditorImageActivity.this);
                    backView.setImageResource(R.drawable.back);
                    backView.setBackground(ContextCompat.getDrawable(EditorImageActivity.this, R.drawable.press_state_bg));
                    backView.setScaleType(ImageView.ScaleType.CENTER);
                    backView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            menu_layout.removeView(cutLayout);
                            change_button.setVisibility(View.GONE);
                            cut_frame.setVisibility(View.GONE);
                            mode = MODE_NORMAL;
                            imageBean.originalBitmap = imageBean.tempBitmap;
                        }
                    });

                    //分割线
                    View divide1 = new View(EditorImageActivity.this);
                    divide1.setBackgroundColor(ContextCompat.getColor(EditorImageActivity.this, R.color.text_color));
                    View divide2 = new View(EditorImageActivity.this);
                    divide2.setBackgroundColor(ContextCompat.getColor(EditorImageActivity.this, R.color.text_color));
                    //menu
                    final TextView tv1 = new TextView(EditorImageActivity.this);
                    tv1.setText("圆形");
                    tv1.setTextColor(ContextCompat.getColor(EditorImageActivity.this, R.color.text_color));
                    tv1.setBackground(ContextCompat.getDrawable(EditorImageActivity.this, R.drawable.press_state_bg));
                    tv1.setTextSize(16);
                    tv1.setGravity(Gravity.CENTER);

                    final TextView tv2 = new TextView(EditorImageActivity.this);
                    tv2.setText("矩形");
                    tv2.setTextColor(ContextCompat.getColor(EditorImageActivity.this, R.color.text_color));
                    tv2.setBackground(ContextCompat.getDrawable(EditorImageActivity.this, R.drawable.press_state_bg));
                    tv2.setTextSize(16);
                    tv2.setGravity(Gravity.CENTER);

                    LinearLayout.LayoutParams menuLp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
                    LinearLayout.LayoutParams divideLp = new LinearLayout.LayoutParams(Utils.dpToPx(1, getResources()), LinearLayout.LayoutParams.MATCH_PARENT);
                    divideLp.setMargins(0, Utils.dpToPx(10, getResources()), 0, Utils.dpToPx(10, getResources()));

                    //圆形
                    tv1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            tv1.setSelected(true);
                            tv2.setSelected(false);
                            isOval = true;
                            cut_frame.setImageResource(R.drawable.oval_overlay);
                        }
                    });

                    //矩形
                    tv2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            tv1.setSelected(false);
                            tv2.setSelected(true);
                            isOval = false;
                            cut_frame.setImageResource(R.drawable.rect_overlay);
                        }
                    });
                    tv1.setSelected(true);

                    cutLayout.addView(backView, menuLp);
                    cutLayout.addView(divide1, divideLp);
                    cutLayout.addView(tv1, menuLp);
                    cutLayout.addView(divide2, divideLp);
                    cutLayout.addView(tv2, menuLp);
                    cutLayout.setClickable(true);
                }

                menu_layout.addView(cutLayout, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                break;
        }
    }
}
