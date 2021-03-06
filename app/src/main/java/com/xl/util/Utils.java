package com.xl.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.xl.activity.R;
import com.xl.activity.pay.PayActivity_;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    public static final SimpleDateFormat dateFormat_simple = new SimpleDateFormat("yyyy-MM-dd");

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static void setFullScreenImage(ImageView imageView, Drawable drawable, int width) {
        int height = (int) ((float) width / drawable.getMinimumWidth() * drawable.getMinimumHeight());
        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        layoutParams.height = height;
        layoutParams.width = width;
        imageView.setImageDrawable(drawable);
    }

    // 判断手机有无存储卡
    public static boolean existSDcard() {
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            return true;
        } else
            return false;
    }

    public static int[] downsize(String path, String toPath, Context context) {
        int angle = readPictureDegree(path);
        int[] wh = new int[2];
        if (angle != 0 && angle % 90 == 0) {
            try {
                File oldFile = new File(path);
                // decode image size
                BitmapFactory.Options o = new BitmapFactory.Options();
                o.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(new FileInputStream(oldFile),
                        null, o);
                o.inJustDecodeBounds = false;
                // Find the correct scale value. It should be the power of 2.
                int maxWidth = 640;
                int maxHeight = 960;

                // decode with inSampleSize
                BitmapFactory.Options temp = new BitmapFactory.Options();
                temp.outWidth = o.outHeight;
                temp.outHeight = o.outWidth;
                o.inSampleSize = computeSampleSize(o, -1, maxHeight * maxWidth);
                Bitmap bitmap1 = BitmapFactory.decodeStream(new FileInputStream(oldFile), null, o);
                if (angle != 0) {
                    bitmap1 = rotaingImageView(angle, bitmap1);
                }
                copy(bitmap1, toPath);

                wh[0] = bitmap1.getWidth();
                wh[1] = bitmap1.getHeight();
                bitmap1.recycle();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                File oldFile = new File(path);
                // decode image size
                BitmapFactory.Options o = new BitmapFactory.Options();
                o.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(new FileInputStream(oldFile),
                        null, o);
                o.inJustDecodeBounds = false;
                // Find the correct scale value. It should be the power of 2.
                int maxWidth = 640;
                int maxHeight = 960;

                // decode with inSampleSize
                o.inSampleSize = computeSampleSize(o, -1, maxHeight * maxWidth);
                Bitmap bitmap1 = BitmapFactory.decodeStream(new FileInputStream(oldFile), null, o);
                copy(bitmap1, toPath);

                wh[0] = bitmap1.getWidth();
                wh[1] = bitmap1.getHeight();
                bitmap1.recycle();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return wh;
    }

    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    public static int computeSampleSize(BitmapFactory.Options options,
                                        int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options,
                                                int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
                .sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
                Math.floor(w / minSideLength), Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }

        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    public static void copy(Bitmap bitmap, String newFile) {
        try {
            File f = new File(newFile);
            File parf = new File(f.getParent());
            if (!parf.exists()) {
                parf.mkdirs();
            }
            parf = null;
            FileOutputStream out = new FileOutputStream(f);
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 75, out)) {
                out.flush();
                out.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        Matrix matrix = new Matrix();
        ;
        matrix.postRotate(angle);
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }

    public static void CopyStream(InputStream is, OutputStream os) {
        final int buffer_size = 1024;
        try {
            byte[] bytes = new byte[buffer_size];
            for (; ; ) {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1)
                    break;
                os.write(bytes, 0, count);
            }
        } catch (Exception ex) {
        }
    }

    private static long lastClickTime;

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 1000) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    public static long locationTime;

    public static boolean isFastLocation() {
        long time = System.currentTimeMillis();
        long timeD = time - locationTime;
        if (0 < timeD && timeD < 15000) {
            return true;
        }
        return false;
    }

    public static String getDistance(double lng1, double lat1, double lng2,
                                     double lat2) {
        // System.out.println(lng1+"--------"+lat1+"--------"+lng2+"--------"+lat2);
        double a = 2 * 6378.137;
        double b = Math.PI / 360;
        double c = Math.PI / 180;
        double s = a
                * Math.asin(Math.sqrt(Math.pow(Math.sin(b * (lat1 - lat2)), 2)
                + Math.cos(c * lat1) * Math.cos(lat2 * c)
                * Math.pow(Math.sin(b * (lng1 - lng2)), 2)));
        if (s * 1000 < 1) {
            s = 0.00;
        }
        return RoundOf(String.valueOf(s));
    }

    public static String RoundOf(String str) {
        return String
                .valueOf((double) Math.round(Double.valueOf(str) * 100) / 100);
    }

    public static String getDistance(String distance) {
        try {
            return RoundOf(String.valueOf(Double.valueOf(distance) / 1000))
                    + "km";
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return "";
    }


    // 缩放图片
    public static Bitmap zoomImg(Bitmap bm, int maxHeight, int maxWidth) {
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();

        float scale = 0f;
        if (width > height) {
            scale = (float) maxWidth / (float) width;
        } else {
            scale = (float) maxHeight / (float) height;
        }

        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }

    public static ProgressDialog progress(Context context, String str) {
        if (context != null && str != null)
            return ProgressDialog.show(context, null, str, false, true);
        else
            return null;
    }

    /**
     * 弹出提示框
     *
     * @param context                上下文
     * @param icon                   图标
     * @param title                  标题
     * @param message                内容
     * @param positiveBtnStr         确认
     * @param negativeBtnStr         取消
     * @param positiveListener       确认监听
     * @param negativeListener       取消监听
     * @param cancelable             是否可以按返回键取消
     * @param canceledOnTouchOutside 是否可以触摸取消
     * @return
     */
    public static AlertDialog showDialog(Context context, Integer icon, Integer title, Integer message, Integer positiveBtnStr, Integer negativeBtnStr, Integer neutralBtnStr, DialogInterface.OnClickListener positiveListener, DialogInterface.OnClickListener negativeListener, DialogInterface.OnClickListener neutralListener, Boolean cancelable, Boolean canceledOnTouchOutside) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (icon != null) {
            builder.setIcon(icon);
        }
        if (title != null) {
            builder.setTitle(title);
        }
        if (message != null) {
            builder.setMessage(message);
        }
        if (positiveBtnStr != null) {
            builder.setPositiveButton(positiveBtnStr, positiveListener);
        }
        if (negativeBtnStr != null) {
            builder.setNegativeButton(negativeBtnStr, negativeListener);
        }
        if (neutralBtnStr != null) {
            builder.setNeutralButton(neutralBtnStr, neutralListener);
        }
        AlertDialog dialog = builder.create();
        if (cancelable != null) {
            builder.setCancelable(cancelable);
            dialog.setCancelable(cancelable);
        }
        if (canceledOnTouchOutside != null) {
            dialog.setCanceledOnTouchOutside(canceledOnTouchOutside);
        }
        dialog.show();
        return dialog;
    }

    public static void showVipDialog(final Context context){
        showDialog(context, R.drawable.pool_gay, R.string.poolgay, R.string.no_vip_message, R.string.become_vip, R.string.become_diaosi, null, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PayActivity_.intent(context).start();
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }, null, true, true);
    }

    /**
     * 开启软键盘
     */
    public static void openSoftKeyboard(EditText et) {
        InputMethodManager inputManager = (InputMethodManager) et.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(et, 0);
    }

    /**
     * 关闭软键盘
     */
    public static void closeSoftKeyboard(Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null && ((Activity) context).getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(((Activity) context).getCurrentFocus()
                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static DisplayImageOptions options_default_logo = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.default_logo)
            .showImageForEmptyUri(R.drawable.default_logo)  // empty URI时显示的图片
            .showImageOnFail(R.drawable.default_logo)       // 不是图片文件 显示图片
            .cacheInMemory(true)           // default 不缓存至内存
            .cacheOnDisk(true).build();

    public static String getDownloadFileUrl(String deviceId,String name){
        return URLS.DOWNLOADFILE + deviceId + "/" + name + URLS.LAST;
    }



    public static final DisplayImageOptions options_no_default = new DisplayImageOptions.Builder()
            .cacheInMemory(true).cacheOnDisk(true).build();

    public static <T> ArrayList<T> jsonToList(String json, Class<T> classOfT) {
        Type type = new TypeToken<ArrayList<JsonObject>>() {
        }.getType();
        ArrayList<JsonObject> jsonObjs = new Gson().fromJson(json, type);
        ArrayList<T> listOfT = null;
        try {
            listOfT = new ArrayList<>();
            for (JsonObject jsonObj : jsonObjs) {
                listOfT.add(new Gson().fromJson(jsonObj, classOfT));
            }
            return listOfT;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static <T> T jsonToBean(String json, Class<T> classOfT) {
        return new Gson().fromJson(json,classOfT);
    }

    /**
     * 获取字符的所占字节长度;
     * @param str
     * @throws UnsupportedEncodingException
     */
    private static void getStringByteLength(String str) throws UnsupportedEncodingException {
        System.out.println("\""+str+"\"字符所占的字节长度如下:");
        System.out.println("ISO-8859-1:"+str.getBytes("ISO-8859-1").length);
        System.out.println("UTF-8:"+str.getBytes("UTF-8").length);
        System.out.println("GBK:"+str.getBytes("GBK").length);
        System.out.println("GB2312:"+str.getBytes("GB2312").length);
        System.out.println("GB18030:"+str.getBytes("GB18030").length);
        System.out.println("UTF-16:"+str.getBytes("UTF-16").length);
    }

    public static void setAlarmTime(Context context, long timeInMillis, String action, int time) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(action);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        int interval = time;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            am.setWindow(AlarmManager.RTC, timeInMillis, interval, sender);
        } else {
            am.setRepeating(AlarmManager.RTC, timeInMillis, interval, sender);
        }
    }

    public static void canalAlarm(Context context, String action) {
        Intent intent = new Intent(action);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(pi);
    }

    public static boolean isServiceRunning(Context mContext, String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager
                .getRunningServices(Integer.MAX_VALUE);
        if (!(serviceList.size() > 0)) {
            return false;
        }
        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className) == true) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

}
