package com.xl.custom;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by sbb on 2014/12/29.
 */
public class physicalBallManager {
    public ArrayList<physicalBall> pBalls;
    public ArrayList<physicalBall> removepBalls;
    Random random;
    View v;

    public physicalBallManager(View v) {
        this.v = v;
        random = new Random();
        pBalls = new ArrayList<physicalBall>();
        removepBalls = new ArrayList<physicalBall>();
    }

    //画物理球类
    private void drawBall(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        for (physicalBall pb : pBalls) {
            pb.draw(canvas, paint);
            pb.move();
            Log.v("balldraw", "ok");
        }
    }

    //增加球
    private void addBall() {
        int pBall_X = random.nextInt(100) + 20;
        int pBall_r = random.nextInt(10) + 5;
        physicalBall pBall = new physicalBall(pBall_X, 0, pBall_r);
        pBalls.add(pBall);
    }

    //减少球
    private void removeBall() {
        for (physicalBall pb : pBalls) {
            if (pb.isStandstill()) {
                removepBalls.add(pb);
            }
        }
        pBalls.removeAll(removepBalls);
    }
}
