package com.xl.custom;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.xl.util.LogUtil;

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
        handler.postDelayed(refere,1000);
    }

    Handler handler = new Handler(Looper.getMainLooper());

    Runnable refere=new Runnable() {
        @Override
        public void run() {
            invalidate();
            LogUtil.d("invalidate");
            if(start)
            handler.postDelayed(this,200);
        }
    };

    public void setStart(boolean start) {
        this.start = start;
    }

    public GifDrawable getGifDrawable() {
        return drawable;
    }

    public void setDrawable(GifDrawable drawable) {
        this.drawable = drawable;
    }

    boolean start =false;

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        start=false;
    }

    public void setImageGifDrawable(GifDrawable drawable) {
        super.setImageDrawable(drawable);
        this.drawable= (GifDrawable) drawable;
    }
}
