package com.cherry.mr.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.regex.Pattern;

public class UIUtil {

    /**
     * 全数字正则表示
     *
     * @param str
     * @return
     */
    public static boolean isNumeric1(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    public static String getVersionName(Context context) {
        PackageManager pm = context.getPackageManager();
        PackageInfo packageInfo;
        try {
            packageInfo = pm.getPackageInfo(context
                    .getPackageName(), 0);
            return packageInfo.versionName;
        } catch (NameNotFoundException e) {
        }
        return "";

    }

    public static void showToast(Context context, int resId) {
        showToast(context, resId, false);
    }

    public static void showToast(Context context, int resId,
                                 boolean durationLong) {
        if (context == null)
            return;
        int duration;
        if (durationLong) {
            duration = Toast.LENGTH_LONG;
        } else {
            duration = Toast.LENGTH_SHORT;
        }

        Toast.makeText(context, resId, duration).show();
    }

    public static void showToast(Context context, String msg) {
        showToast(context, msg, false);
    }

    public static void showToast(Context context, String msg,
                                 boolean durationLong) {
        if (context == null)
            return;
        int duration;
        if (durationLong) {
            duration = Toast.LENGTH_LONG;
        } else {
            duration = Toast.LENGTH_SHORT;
        }

        Toast.makeText(context, msg, duration).show();
    }

    @SuppressLint("NewApi")
    public static void copyContent(Activity activity, String content) {
        int currentapiVersion = Build.VERSION.SDK_INT;

        if (currentapiVersion >= Build.VERSION_CODES.HONEYCOMB) {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) activity
                    .getSystemService(Activity.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("label", content);
            clipboard.setPrimaryClip(clip);
        } else {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) activity
                    .getSystemService(Activity.CLIPBOARD_SERVICE);
            clipboard.setText(content);
        }
    }


    /**
     * 判断底部navigator是否已经显示
     * @param windowManager
     * @return
     */
    @SuppressLint("NewApi")
    public static boolean hasSoftKeys(WindowManager windowManager){
        Display d = windowManager.getDefaultDisplay();

        DisplayMetrics realDisplayMetrics = new DisplayMetrics();
        d.getRealMetrics(realDisplayMetrics);

        int realHeight = realDisplayMetrics.heightPixels;
        int realWidth = realDisplayMetrics.widthPixels;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        d.getMetrics(displayMetrics);

        int displayHeight = displayMetrics.heightPixels;
        int displayWidth = displayMetrics.widthPixels;

        return (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;
    }

    /**
     * add by mcoy for bugID=427
     * @return 底部状态栏的高度
     */
    private int getNavigationBarHeight(Context context) {
        if(!hasSoftKeys((WindowManager) context.getSystemService(Context.WINDOW_SERVICE))){
            return 0;
        }
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height","dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }
}
