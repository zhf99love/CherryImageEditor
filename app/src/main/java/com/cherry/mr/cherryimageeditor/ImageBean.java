package com.cherry.mr.cherryimageeditor;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by seapeak on 16/3/18.
 */
public class ImageBean implements Serializable {

    public String path = "";

    public String information = "";

    public Bitmap originalBitmap;
    public Bitmap tempBitmap;
}
