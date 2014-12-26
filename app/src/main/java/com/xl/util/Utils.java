package com.xl.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.text.SimpleDateFormat;

public class Utils {
	public static final SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static void setFullScreenImage(ImageView imageView,Drawable drawable,int width){
        int height = (int) ((float) width/drawable.getMinimumWidth() * drawable.getMinimumHeight());
        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        layoutParams.height=height;
        layoutParams.width=width;
        imageView.setImageDrawable(drawable);
    }
}
