package com.cherry.mr.cherryimageeditor;

import android.app.Application;
import android.graphics.drawable.Drawable;

import com.cherry.mr.utils.FrescoImgUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by seapeak on 16/3/19.
 */
public class CherryApp extends Application {

    public static Drawable imageCache;
    public static List<ImageBean> listBeans;

    @Override
    public void onCreate() {
        super.onCreate();
        listBeans = new ArrayList<ImageBean>();
        FrescoImgUtils.Init(getApplicationContext());
    }
}
