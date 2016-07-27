package com.xl.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.xl.util.Utils;

/**
 * Created by Administrator on 2014/9/20.
 */
public class CView extends SurfaceView implements SurfaceHolder.Callback {

    Paint text = new Paint(Paint.ANTI_ALIAS_FLAG);
    int textSize = 14;
    SurfaceHolder holder;
    Paint paintQ = new Paint();
    Path path = new Path();
    MyThread myThread;


    public CView(Context context) {
        super(context);
        init();
    }

    public CView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    void init() {
        setZOrderOnTop(true);//设置画布  背景透明
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        holder = this.getHolder();
        holder.addCallback(this);
        myThread = new MyThread(holder);//创建一个绘图线程
        text.setColor(Color.BLACK);
        text.setTextSize(textSize = Utils.dip2px(getContext(), 24));
        paintQ.setAntiAlias(true);
        paintQ.setStyle(Paint.Style.FILL);
        paintQ.setStrokeWidth(0);
        paintQ.setColor(Color.TRANSPARENT);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        myThread.isRun = true;
        myThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        myThread.isRun = false;
    }

    class MyThread extends Thread {
        private SurfaceHolder holder;
        public boolean isRun;

        public MyThread(SurfaceHolder holder) {
            this.holder = holder;
            isRun = true;
        }

        @Override
        public void run() {
            while (isRun) {
                Canvas c = null;
                try {
                    synchronized (holder) {
                        c = holder.lockCanvas();//锁定画布，一般在锁定后就可以通过其返回的画布对象Canvas，在其上面画图等操作了。

                        path.reset();
                        path.moveTo(0, getHeight());
                        path.quadTo(getWidth() / 2f, (int)(getHeight()/1.5), getWidth(), getHeight());
                        path.close();
                        c.clipPath(path, Region.Op.INTERSECT);
                        c.drawColor(Color.YELLOW);
                        c.save();
                        c.restore();
                        c.drawPath(path, paintQ);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (c != null) {
                        holder.unlockCanvasAndPost(c);//结束锁定画图，并提交改变。
                    }
                }
                try {
                    Thread.sleep(1000);//睡眠时间为1秒
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
