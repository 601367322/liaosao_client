package com.xl.custom;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * Created by sbb on 2014/12/29.
 */
public class physicalBall {
    //小球的x,y坐标以及球心半径
    private int pBall_x, pBall_y, pBall_r;
    //小球的x,y方向的速度
    private float pBall_Xspeed, pBall_Yspeed;
    //小球的x,y方向的加速度
    private float pBall_XAcc = 2.2f, pBall_YAcc = 2.3f;
    private final float ACC = 0.135f;// 为了模拟加速度的偏移值
    private final float RECESSION = 0.148f;// 每次弹起的衰退系数
    private int pBall_color;
    private boolean isDown = true;

    public physicalBall(int pBall_x, int pBall_y, int pBall_r) {
        super();
        this.pBall_x = pBall_x;
        this.pBall_y = pBall_y;
        this.pBall_r = pBall_r;
    }

    public void draw(Canvas canvas, Paint paint) {
        paint.setColor(Color.BLUE);
        canvas.drawArc(new RectF(pBall_x + pBall_Xspeed, pBall_y + pBall_Yspeed, pBall_x + 2
                        * pBall_r + pBall_Xspeed, pBall_y + 2 * pBall_r + pBall_Yspeed), 0, 360, true,
                paint);
        //canvas.drawCircle(pBall_x+ pBall_Xspeed, pBall_y+ pBall_Yspeed, pBall_r, paint);
    }

    public void move() {
        if (isDown) {
            pBall_Yspeed += pBall_YAcc;
            int count = (int) pBall_YAcc++;
            for (int i = 0; i < count; i++) {
                pBall_YAcc += ACC;
            }
        } else {
            pBall_Yspeed -= pBall_YAcc;
            int count = (int) pBall_YAcc--;
            for (int i = 0; i < count; i++) {
                pBall_YAcc -= ACC;
            }
        }
        if (isCollision()) {
            isDown = !isDown;
            pBall_YAcc -= RECESSION * pBall_YAcc;
        }
    }

    private boolean isCollision() {
        // TODO Auto-generated method stub
        return pBall_y + pBall_Yspeed >= layoutSurfaceView.screenH - 6;
    }

    public boolean isStandstill() {
        return pBall_YAcc - RECESSION * pBall_YAcc <= 0.5 ? false : false;
    }
}
