package com.xl.util;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;

import com.xl.activity.R;


/**
 * Created by sam on 14-10-30.
 */
public class UIUtils {
    public static void setSystemBarTintColor(Activity activity){
        if(SystemBarTintManager.isKitKat()){
            SystemBarTintManager tintManager = new SystemBarTintManager(activity);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintDrawable(new ColorDrawable(activity.getResources().getColor(R.color.holo_blue_dark)));
        }
    }
}
