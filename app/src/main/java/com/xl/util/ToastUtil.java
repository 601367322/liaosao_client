package com.xl.util;

import android.content.Context;

import com.github.johnpersano.supertoasts.library.Style;
import com.github.johnpersano.supertoasts.library.SuperToast;

/**
 * Created by sbb on 2015/6/2.
 */
public class ToastUtil {

    private static SuperToast superToast;

    public static void toast(Context context, String str) {
        toast(context, str, -1, Style.DURATION_SHORT);
    }

    public static void toast(Context context, String str, int res, int duration) {
        if (context != null) {
            superToast = new SuperToast(context);
            superToast.setText(str);
            superToast.setDuration(duration);
            if (res != -1) {
                superToast.setIconResource(res,Style.ICONPOSITION_LEFT);
            }
            superToast.show();
        }
    }

    public static void toast(Context context, String str, int res) {
        if (context != null) {
            superToast = new SuperToast(context);
            superToast.setText(str);
            superToast.setDuration(Style.DURATION_SHORT);
            superToast.setIconResource(res, Style.ICONPOSITION_LEFT);
            superToast.show();
        }
    }
}
