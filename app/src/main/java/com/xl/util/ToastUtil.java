package com.xl.util;

import android.content.Context;

import com.github.johnpersano.supertoasts.SuperToast;

/**
 * Created by sbb on 2015/6/2.
 */
public class ToastUtil {

    private static SuperToast superToast;

    public static void toast(Context context, String str) {
        if (context != null) {
            superToast = new SuperToast(context);
            superToast.setText(str);
            superToast.setDuration(SuperToast.Duration.SHORT);
            superToast.show();
        }
    }

    public static void toast(Context context, String str, int res) {
        if (context != null) {
            superToast = new SuperToast(context);
            superToast.setText(str);
            superToast.setDuration(SuperToast.Duration.SHORT);
            superToast.setIcon(res, SuperToast.IconPosition.LEFT);
            superToast.show();
        }
    }
}
