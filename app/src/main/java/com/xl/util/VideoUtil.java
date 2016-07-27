package com.xl.util;

import android.app.Activity;
import android.content.Intent;

import com.duanqu.qupai.minisdk.MiniApplication;

import java.io.File;

/**
 * Created by Shen on 2016/2/21.
 */
public class VideoUtil {

    public static File[] onActivityResult(int requestCode, int result, Intent data) {
        try {
            return MiniApplication.onActivityResult(requestCode, result, data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void startRecordActivity(Activity context) {
        MiniApplication.getInstance(context).startRecordActivity(context);
    }
}
