package com.xl.custom;

import android.app.ActionBar;
import android.content.Context;
import android.support.v7.widget.SwitchCompat;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.xl.util.Utils;

/**
 * Created by Shen on 2015/6/7.
 */
public class MyActionBarSwitcher extends SwitchCompat {

    public MyActionBarSwitcher(Context context) {
        super(context);
        init();
    }

    public MyActionBarSwitcher(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyActionBarSwitcher(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init(){
        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.rightMargin = Utils.dip2px(getContext(), 16);
        setLayoutParams(layoutParams);
    }

}
