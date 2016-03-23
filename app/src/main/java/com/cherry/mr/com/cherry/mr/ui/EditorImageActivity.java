package com.cherry.mr.com.cherry.mr.ui;

import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import com.cherry.mr.cherryimageeditor.ImageBean;
import com.cherry.mr.cherryimageeditor.R;
import com.cherry.mr.touchview.TouchImageView;
import com.cherry.mr.utils.ImageTools;

public class EditorImageActivity extends AppCompatActivity {

    private TouchImageView touchImageView;

    private ImageBean imageBean;

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
    }

    private void initView() {
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        imageBean = (ImageBean) getIntent().getSerializableExtra("tempImage");
        touchImageView = (TouchImageView) findViewById(R.id.touch_image);
        touchImageView.setBackgroundColor(0xff000000);
        touchImageView.setImageBitmap(ImageTools.convertToBitmap(imageBean.path, metric));
        touchImageView.setZoomToOriginalSize(true);
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
}
