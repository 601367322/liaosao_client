package com.xl.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.os.Environment;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;

public class Utils {
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

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
                int maxWidth = context.getResources().getDisplayMetrics().widthPixels;
                int maxHeight = context.getResources().getDisplayMetrics().heightPixels;

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
                int maxWidth = context.getResources().getDisplayMetrics().widthPixels;
                int maxHeight = context.getResources().getDisplayMetrics().heightPixels;

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
            // TODO Auto-generated catch block
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
            scale = (float)maxWidth / (float)width;
        }else{
            scale = (float)maxHeight / (float)height;
        }

        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }
}
