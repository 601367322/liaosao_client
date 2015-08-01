package com.xl.custom;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.xl.util.LogUtil;

import java.util.ArrayList;

/**
 * Created by sbb on 2014/12/30.
 */
public class MyAnimationView extends View implements ValueAnimator.AnimatorUpdateListener {

    private static final int DURATION = 1500;
    private static final float BALL_SIZE = 100f;

    public void setmHeight(int mHeight) {
        this.mHeight = mHeight;
        LogUtil.d(mHeight+"");
        if (mHeight > 0 && getHeight() > 0) {
            final ShapeHolder ball = addBall((float) (Math.random() * (getWidth() - BALL_SIZE)), (float) getHeight());
            createAnimation(ball);
        }
    }

    private int mHeight;

    public final ArrayList<ShapeHolder> balls = new ArrayList<ShapeHolder>();

    public MyAnimationView(Context context) {
        super(context);
        init();
    }

    public MyAnimationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public MyAnimationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    void init() {
    }

    private void createAnimation(final ShapeHolder ball) {
        float f = (mHeight / 700f);
        int tempHeight = (int) (getHeight() * f);
        ObjectAnimator yStart = ObjectAnimator.ofFloat(ball, "y",
                ball.getY(), getHeight() - tempHeight).setDuration(400);
        yStart.setInterpolator(new DecelerateInterpolator());
        yStart.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                ObjectAnimator yBouncer = ObjectAnimator.ofFloat(ball, "y",
                        ball.getY(), getHeight() - BALL_SIZE).setDuration(DURATION);
                yBouncer.setInterpolator(new BounceInterpolator());
                yBouncer.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        ObjectAnimator yAlpha = ObjectAnimator.ofFloat(ball, "alpha",
                                1.0f, 0f).setDuration(DURATION);
                        yAlpha.setInterpolator(new AccelerateInterpolator());
                        yAlpha.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                balls.remove(ball);
                            }
                        });
                        yAlpha.addUpdateListener(MyAnimationView.this);
                        yAlpha.start();
                    }
                });
                yBouncer.addUpdateListener(MyAnimationView.this);
                yBouncer.start();
            }
        });
        yStart.addUpdateListener(this);
        yStart.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        try {
            for (int i = 0; i < balls.size(); i++) {
                if (i < balls.size()) {
                    ShapeHolder ball = balls.get(i);
                    canvas.translate(ball.getX(), ball.getY());
                    ball.getShape().draw(canvas);
                    canvas.translate(-ball.getX(), -ball.getY());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startAnimation() {
    }

    private ShapeHolder addBall(float x, float y) {
        OvalShape circle = new OvalShape();
        circle.resize(BALL_SIZE, BALL_SIZE);
        ShapeDrawable drawable = new ShapeDrawable(circle);
        ShapeHolder shapeHolder = new ShapeHolder(drawable);
        shapeHolder.setX(x);
        shapeHolder.setY(y);
        int red = (int) (100 + Math.random() * 155);
        int green = (int) (100 + Math.random() * 155);
        int blue = (int) (100 + Math.random() * 155);
        int color = 0xff000000 | red << 16 | green << 8 | blue;
        Paint paint = drawable.getPaint();
        int darkColor = 0xff000000 | red / 4 << 16 | green / 4 << 8 | blue / 4;
        RadialGradient gradient = new RadialGradient(37.5f, 12.5f,
                50f, color, darkColor, Shader.TileMode.CLAMP);
        paint.setShader(gradient);
        shapeHolder.setPaint(paint);
        balls.add(shapeHolder);
        return shapeHolder;
    }

    public void onAnimationUpdate(ValueAnimator animation) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    Handler handler = new Handler(Looper.getMainLooper());


}