package com.xl.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
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
    Path path = new Path();;

    Paint san=new Paint();
    Path sanp = new Path();

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
        paintQ.setStyle(Paint.Style.STROKE);
        paintQ.setStrokeWidth(5);
        paintQ.setColor(Color.BLACK);

        san.setAntiAlias(true);
        san.setStyle(Paint.Style.STROKE);
        san.setColor(Color.BLACK);
        san.setStrokeWidth(5);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        float textWidth = text.measureText("没有找到？试试这个");   //测量字体宽度，我们需要根据字体的宽度设置在圆环中间


        float width=(getWidth() -textWidth) / 2;
        float height=(getHeight() -textSize)/2;

        canvas.drawText("没有找到？试试这个",width, height, text);

        path.reset();
        path.moveTo(width+textWidth+10, height);
        path.quadTo(getWidth()-100, height/1.5f, getWidth()-50, 50);
        canvas.drawPath(path, paintQ);

        sanp.moveTo(getWidth()-70, 50+10);
        sanp.lineTo( getWidth()-50,10+10);
        sanp.lineTo(getWidth()-30, 50+10);
        canvas.drawPath(sanp, san);
    }

}
