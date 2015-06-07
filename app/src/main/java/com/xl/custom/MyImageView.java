package com.xl.custom;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.ImageView;

import pl.droidsonroids.gif.GifDrawable;

/**
 * TODO: document your custom view class.
 */
public class MyImageView extends ImageView {

    private GifDrawable drawable;

    public MyImageView(Context context) {
        super(context);
        init(null, 0);
    }

    public MyImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public MyImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        handler.sendEmptyMessageDelayed(1, 1000l);
    }

    Handler handler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (start) {
                        invalidate();
                        if (drawable != null && !drawable.isPlaying()) {
                            drawable.start();
                        }
                        handler.sendEmptyMessageDelayed(1, 100l);
                    }
                    break;
            }
            super.dispatchMessage(msg);
        }
    };

    public void setStart(boolean start) {
        this.start = start;
        handler.removeMessages(1);
        if (start) {
            handler.sendEmptyMessage(1);
        }
    }

    public GifDrawable getGifDrawable() {
        return drawable;
    }

    public void setDrawable(GifDrawable drawable) {
        this.drawable = drawable;
    }

    boolean start = false;

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        start = false;
    }

    public void setImageGifDrawable(GifDrawable drawable) {
        super.setImageDrawable(drawable);
        this.drawable = drawable;
    }
}
