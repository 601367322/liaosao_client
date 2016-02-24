package com.xl.util;

import android.content.Context;
import android.content.Intent;

import java.io.File;

/**
 * Created by Shen on 2016/2/21.
 */
public class VideoUtil {

    public static File[] onActivityResult(int requestCode, int result, Intent data) {
        try {
//            return MiniApplication.onActivityResult(requestCode, result, data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void startRecordActivity(Context context) {
//        MiniApplication.getInstance(context).startRecordActivity(context);
    }
}
