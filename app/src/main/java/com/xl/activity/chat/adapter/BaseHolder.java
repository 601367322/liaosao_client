package com.xl.activity.chat.adapter;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.view.View;

import com.xl.activity.R;
import com.xl.bean.MessageBean;

import butterknife.Bind;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * Created by Shen on 2015/9/13.
 */
public abstract class BaseHolder extends com.xl.activity.base.BaseHolder<MessageBean> {

    @Nullable
    @Bind(R.id.error)
    View error;

    @Nullable
    @Bind(R.id.progress)
    View progress;

    ChatAdapters adapter;

    int mstType;

    Handler handler = new Handler(Looper.getMainLooper());

    public BaseHolder(View itemView) {
        super(itemView);
    }

    protected void bind(MessageBean bean) {

        if (progress != null) {
            if (bean.getLoading() == MessageBean.LOADING_DOWNLOADING) {
                progress.setVisibility(View.VISIBLE);
            } else {
                progress.setVisibility(View.GONE);
            }
        }
        if (error != null) {
            if (bean.getLoading() == MessageBean.LOADING_DOWNLOADFAIL) {
                error.setVisibility(View.VISIBLE);
                error.setTag(bean);
            } else {
                error.setVisibility(View.GONE);
            }
        }

    }

    @Nullable
    @OnClick(R.id.error)
    void errorClick(View view) {
        MessageBean mb = (MessageBean) view.getTag();
        if (mb != null) {
            EventBus.getDefault().post(mb);
        }
    }

    public int getMstType() {
        return mstType;
    }

    public void setMstType(int mstType) {
        this.mstType = mstType;
    }

    public ChatAdapters getAdapter() {
        return adapter;
    }

    public void setAdapter(ChatAdapters adapter) {
        this.adapter = adapter;
    }

    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }
}
