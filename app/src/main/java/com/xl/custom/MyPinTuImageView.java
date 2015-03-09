package com.xl.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

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

    float x, y, getx, gety;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                x = event.getX();
                y = event.getY();
                getx = getX();
                gety = getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float ex = event.getX();
                float ey = event.getY();
                int left = (int) (getX() + (ex - x));
                int top = (int) (getY() + (ey - y));
                setX(left);
                setY(top);
                break;
            case MotionEvent.ACTION_UP:
                float ux = event.getX(), uy = event.getY();
                if (Math.abs(getX() - getx) < 10 && Math.abs(getY() - gety) < 10) {
                    if (touchListener != null) {
                        touchListener.onClick(this);
                    }
                } else {
                    if (touchListener != null) {
                        touchListener.onActionUp(this);
                    }
                }
                break;
        }
        return true;
    }

    public OnTouchListener getTouchListener() {
        return touchListener;
    }

    public void setTouchListener(OnTouchListener touchListener) {
        this.touchListener = touchListener;
    }

    OnTouchListener touchListener;

    public interface OnTouchListener {
        public void onActionUp(MyPinTuImageView imageView);

        public void onClick(MyPinTuImageView imageView);
    }
}
