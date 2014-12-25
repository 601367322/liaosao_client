package com.xl.custom;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.xl.util.LogUtil;

/**
 * TODO: document your custom view class.
 */
public class MyImageView extends ImageView {

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
            if(!b)
            handler.postDelayed(this,1000);
        }
    };

    boolean b =false;

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        b=false;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        b=true;
    }
}
