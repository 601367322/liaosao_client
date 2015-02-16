/*
package com.xl.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;

import com.xl.activity.R;

*/
/**
 * Created by sbb on 2015/2/12.
 *//*

public class MyPinDrawable {

    Drawable p1,p2,p3,p4,p5,p6,p7,p8,p9;

    public MyPinDrawable(Context context,int id) {
        p1=context.getResources().getDrawable(R.drawable.p1);
        p2=context.getResources().getDrawable(R.drawable.p2);
        p3=context.getResources().getDrawable(R.drawable.p3);
        p4=context.getResources().getDrawable(R.drawable.p4);
        p5=context.getResources().getDrawable(R.drawable.p5);
        p6=context.getResources().getDrawable(R.drawable.p6);
        p7=context.getResources().getDrawable(R.drawable.p7);
        p8=context.getResources().getDrawable(R.drawable.p8);
        p9=context.getResources().getDrawable(R.drawable.p9);

        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),id);

//        bitmap.get
    }

    private void configureBitmapBounds(Drawable shape,int viewWidth, int viewHeight) {
        Matrix matrix=new Matrix();
        int drawableWidth = shape.getIntrinsicWidth();
        int drawableHeight = shape.getIntrinsicHeight();
        boolean fits = viewWidth == drawableWidth && viewHeight == drawableHeight;

        if (drawableWidth > 0 && drawableHeight > 0 && !fits) {
            shape.setBounds(0, 0, drawableWidth, drawableHeight);
            float widthRatio = (float) viewWidth / (float) drawableWidth;
            float heightRatio = (float) viewHeight / (float) drawableHeight;
            float scale = Math.min(widthRatio, heightRatio);
            float dx = (int) ((viewWidth - drawableWidth * scale) * 0.5f + 0.5f);
            float dy = (int) ((viewHeight - drawableHeight * scale) * 0.5f + 0.5f);

            matrix.setScale(scale, scale);
            matrix.postTranslate(dx, dy);
        }
    }
}
*/
