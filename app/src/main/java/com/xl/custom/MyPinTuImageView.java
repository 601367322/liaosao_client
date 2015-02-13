package com.xl.custom;

import android.content.Context;
import android.util.AttributeSet;

import com.github.siyamed.shapeimageview.mask.PorterShapeImageView;

/**
 * Created by sbb on 2015/2/12.
 */
public class MyPinTuImageView extends PorterShapeImageView {

    public MyPinTuImageView(Context context) {
        super(context);
    }

    public MyPinTuImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyPinTuImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

  /*  @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:


                break;
            case MotionEvent.ACTION_MOVE:
                Bitmap bitmap = ((BitmapDrawable) getDrawable()).getBitmap();
                int temp = bitmap.getPixel((int) event.getX(), (int) event.getY());
                int transValue = Color.alpha(temp);
                int redValue = Color.red(temp);
                int blueValue = Color.blue(temp);
                int greenValue = Color.green(temp);

                LogUtil.d(transValue+"\t"+redValue+"\t"+blueValue+"\t"+greenValue);
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }*/
}
