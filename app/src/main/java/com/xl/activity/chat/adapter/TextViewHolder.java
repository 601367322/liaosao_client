package com.xl.activity.chat.adapter;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.xl.activity.R;
import com.xl.bean.MessageBean;

import butterknife.Bind;

/**
 * Created by Shen on 2015/9/13.
 */
public class TextViewHolder extends BaseHolder {

    @Nullable
    @Bind(R.id.content)
    TextView content;

    public TextViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    protected void bind(MessageBean bean) {
        content.setText(bean.getContent().toString());
    }
}
