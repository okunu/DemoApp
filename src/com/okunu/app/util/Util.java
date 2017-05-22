package com.okunu.app.util;

import android.content.Context;
import android.util.Log;
import android.view.WindowManager;

public class Util {

    public static final String TAG = "okunu";
    
    public static void log(String str){
        Log.i(TAG, str);
    }
    public static void log(Object str){
        Log.i(TAG, str.toString());
    }
    public static void logTrace(String str){
        Log.i(TAG, str, new Exception());
    }
    
    public static int getStatusBarHeight(Context context){
        WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        int w = wm.getDefaultDisplay().getWidth();
        int h = wm.getDefaultDisplay().getHeight();
        int id = 0;
        int height = 0;
        if (w > h) {
            id = context.getResources().getIdentifier("status_bar_width", "dimen", "android");
        }else {
            id = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        }
        if (id > 0) {
            height = context.getResources().getDimensionPixelSize(id);
        }
        log("height = " + height);
        return height;
    }
}
