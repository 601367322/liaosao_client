package com.xl.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.xl.util.LogUtil;

import java.util.Random;

/**
 * Created by Administrator on 2014/9/20.
 */
public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private SurfaceHolder sfh;
    private Paint paint;
    private Canvas canvas;
    private Thread th;
    private boolean flag = true;

    private int startX, startY, controlX, controlY, endX, endY;
    private Path path;
    private Paint paintQ;
    private Random random;

    public MySurfaceView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        sfh = getHolder();
        sfh.addCallback(this);

        path = new Path();
        paintQ = new Paint();
        paintQ.setAntiAlias(true);
        paintQ.setStyle(Paint.Style.STROKE);
        paintQ.setStrokeWidth(5);
        paintQ.setColor(Color.WHITE);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        // TODO Auto-generated method stub

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        paint = new Paint();
        paint.setAntiAlias(true);
        th = new Thread(this);
        flag = true;
        th.start();
    }

    private void myDraw() {
        // TODO Auto-generated method stub
        canvas = sfh.lockCanvas();
        canvas.drawColor(Color.BLACK);
        drawQpath(canvas);
        sfh.unlockCanvasAndPost(canvas);
    }

    //绘制贝塞尔曲线
    public void drawQpath(Canvas canvas) {
        path.reset();
        path.moveTo(startX, startY);
        path.quadTo(controlX, controlY, endX, endY);
        canvas.drawPath(path, paintQ);
    }


    //响应触摸屏事件，通过手指的位置取得两个重要的点，起始点，和终止点
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            startX = (int) event.getX();
            startY = (int) event.getY();
        }
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            endX = (int) event.getX();
            endY = (int) event.getY();
        }

        return true;
    }

    //通过起始点和终止点构造控制点
    private void logic() {
        if (endX != 0 && endY != 0) {
            controlX = getResources().getDisplayMetrics().widthPixels-50;
            controlY = Math.abs((endY - startY) / 2);

            LogUtil.d(startX+"-------"+startY+"------------"+controlX+"-------"+controlY+"-----------"+endX+"-----------"+endY);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        flag = false;
    }

    //线程run，刷屏得到贝塞尔曲线
    @Override
    public void run() {
        // TODO Auto-generated method stub
        while (flag) {
            long startTime = System.currentTimeMillis();
            myDraw();
            logic();
            long endTime = System.currentTimeMillis();
            if ((endTime - startTime < 50)) {
                try {
                    Thread.currentThread().sleep(50 - (endTime - startTime));
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }

    }

}