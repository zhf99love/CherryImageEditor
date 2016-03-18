package com.cherry.mr.cherryimageeditor;

import android.app.Application;
import android.graphics.drawable.Drawable;

import com.cherry.mr.utils.FrescoImgUtils;

/**
 * Created by seapeak on 16/3/19.
 */
public class CherryApp extends Application {

    public static Drawable imageCache;

    @Override
    public void onCreate() {
        super.onCreate();
        FrescoImgUtils.Init(getApplicationContext());
    }
}
