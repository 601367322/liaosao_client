package com.xl.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.xl.util.LogUtil;

/**
 * Created by sbb on 2015/2/12.
 */
public class MyPinTuImageView extends ImageView {

    public MyPinTuImageView(Context context) {
        super(context);
    }

    public MyPinTuImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyPinTuImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    float x, y;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                x = event.getX();
                y = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float ex = event.getX(), ey = event.getY();
                int left = (int) (getX() + (ex - x));
                int top = (int) (getY() + (ey - y));

                LogUtil.d(left+"\t"+top+"\t"+getRight()+"\t"+getBottom());
//                setLeft(left);
//                setTop(top);
                setX(left);
                setY(top);
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }
}
