package com.xl.activity.chat.adapter;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xl.activity.R;
import com.xl.activity.chat.ImageViewActivity_;
import com.xl.bean.MessageBean;
import com.xl.util.StaticFactory;
import com.xl.util.Utils;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * Created by Shen on 2015/9/13.
 */
public abstract class BaseHolder extends com.xl.activity.base.BaseHolder<MessageBean> {

    @Nullable
    @BindView(R.id.error)
    View error;

    @Nullable
    @BindView(R.id.progress)
    View progress;

    @Nullable
    @BindView(R.id.userlogo)
    ImageView userlogo;

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
        //设置头像
        try {
            if (userlogo != null && adapter.friend != null && !TextUtils.isEmpty(adapter.friend.getBean().logo)) {
                ImageLoader.getInstance().displayImage(adapter.friend.getBean().logo + StaticFactory._160x160, userlogo, Utils.options_default_logo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Optional
    @OnClick(R.id.error)
    void errorClick(View view) {
        MessageBean mb = (MessageBean) view.getTag();
        if (mb != null) {
            EventBus.getDefault().post(mb);
        }
    }

    @Optional
    @OnClick(R.id.userlogo)
    void userlogoClick() {
        try {
            if (adapter.friend != null && !TextUtils.isEmpty(adapter.friend.getBean().logo)) {
                ImageViewActivity_.intent(context).imageUrl(adapter.friend.getBean().logo).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
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
