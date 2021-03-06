package com.cherry.mr.cherryimageeditor;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import com.cherry.mr.utils.FrescoImgUtils;
import com.cherry.mr.utils.UIUtil;
import com.facebook.drawee.view.DraweeView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ScrollingActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private RecyclerView recycleView;
    private DraweeView topImage;

    private List<ImageBean> listBeans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("图片浏览");
        setSupportActionBar(toolbar);
        initView();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void initView() {
        listBeans = new ArrayList<ImageBean>();
        recycleView = (RecyclerView) findViewById(R.id.list);
        recycleView.setLayoutManager(new StaggeredGridLayoutManager(3,
                StaggeredGridLayoutManager.VERTICAL));
        recycleView.setItemAnimator(new DefaultItemAnimator());
        recycleView.setAdapter(new ImageRecycleAdapter(ScrollingActivity.this, listBeans));

        topImage = (DraweeView) findViewById(R.id.top_image);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Intent in = new Intent(ScrollingActivity.this, MainActivity.class);
                startActivity(in, ActivityOptions.makeSceneTransitionAnimation(ScrollingActivity.this, fab, "shareName").toBundle());
            }
        });

        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ScrollingActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    4);
            return;
        } else {
            loadData();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 4){
            if (permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE)
                    &&grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //用户同意使用write
                loadData();
            }
        }
    }

    private void loadData() {
        new AsyncTask<Void, Void, List<ImageBean>>(){

            @Override
            protected List<ImageBean> doInBackground(Void... params) {
                List<ImageBean> list = getNearImages();
                return list;
            }

            @Override
            protected void onPostExecute(List<ImageBean> list) {
                super.onPostExecute(list);
                listBeans = list;
                ((ImageRecycleAdapter) recycleView.getAdapter()).setData(listBeans);
                FrescoImgUtils.displayBigRectImage("file://" + list.get(0).path, topImage);
            }

        }.execute();
    }

    //最近图片
    private List<ImageBean> getNearImages() {
        List<ImageBean> listBeans = new ArrayList<ImageBean>();
        Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver mContentResolver = getContentResolver();

        //只查询jpeg和png的图片
        Cursor mCursor = mContentResolver.query(mImageUri, null,
                MediaStore.Images.Media.MIME_TYPE + "=? or "
                        + MediaStore.Images.Media.MIME_TYPE + "=?",
                new String[] { "image/jpeg", "image/png" }, MediaStore.Images.Media.DATE_MODIFIED + " DESC");

        if(mCursor == null){
            return null;
        }

//        while (mCursor.moveToNext() && listBeans.size() < 200) {
        while (mCursor.moveToNext()) {
            //获取图片的路径
            String path = mCursor.getString(mCursor
                    .getColumnIndex(MediaStore.Images.Media.DATA));

            //获取该图片的父路径名
            String parentName = new File(path).getParentFile().getName();

            ImageBean addMediaItem = new ImageBean();
            addMediaItem.path = path;
            listBeans.add(addMediaItem);
        }
        mCursor.close();
        return listBeans;
    }

}
