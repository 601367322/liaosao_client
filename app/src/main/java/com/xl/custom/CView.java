package com.xl.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.View;

import com.xl.util.Utils;

/**
 * Created by Administrator on 2014/9/20.
 */
public class CView extends View{

    Paint text = new Paint(Paint.ANTI_ALIAS_FLAG);
    int textSize=14;

    Paint paintQ = new Paint();
    Path path = new Path();
    int height;


    public CView(Context context) {
        super(context);

        init();

    }

    public CView(Context context, AttributeSet attrs) {
        super(context, attrs);init();
    }

    public CView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);init();
    }

    void init(){
        text.setColor(Color.BLACK);
        text.setTextSize(textSize=Utils.dip2px(getContext(),24));

        paintQ.setAntiAlias(true);
        paintQ.setStyle(Paint.Style.FILL);
        paintQ.setStrokeWidth(0);
        paintQ.setColor(Color.TRANSPARENT);

    }

    @Override
    protected float getBottomFadingEdgeStrength() {
        return 1.0f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        path.reset();
        path.moveTo(0, getHeight());
        path.quadTo(getWidth()/2f, -(getHeight()/2f), getWidth(), getHeight());
        paintQ.setShadowLayer(50, 0, -10, Color.YELLOW);
        path.close();
        canvas.clipPath(path, Region.Op.INTERSECT);
        canvas.drawColor(Color.YELLOW);
        canvas.restore();
        canvas.drawPath(path, paintQ);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }
}
