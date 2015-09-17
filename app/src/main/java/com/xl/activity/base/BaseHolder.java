package com.xl.activity.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.xl.application.AppClass;

import butterknife.ButterKnife;

/**
 * Created by Shen on 2015/9/13.
 */
public abstract class BaseHolder<T> extends RecyclerView.ViewHolder {

    protected AppClass ac;
    protected Context context;

    public BaseHolder(View itemView) {
        super(itemView);

        ac = (AppClass) itemView.getContext().getApplicationContext();
        context = itemView.getContext();

        ButterKnife.bind(this, itemView);
    }

    protected abstract void bind(T bean);

}
