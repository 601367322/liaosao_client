package com.xl.activity.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xl.activity.R;
import com.xl.activity.base.BaseAdapterListView;
import com.xl.bean.ChatListBean;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.FindView;

/**
 * Created by sbb on 2015/5/6.
 */
public class ChatListAdapters extends BaseAdapterListView<ChatListBean> {

    public ChatListAdapters(List<ChatListBean> list, Context context) {
        super(list, context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.chat_list_item, parent, false);
            holder = new ViewHolder(convertView);
        }else{
            holder= (ViewHolder) convertView.getTag();
        }
        ChatListBean bean = getItem(position);
        if(bean != null) {
            holder.num.setText(String.valueOf(bean.getId()));
            holder.text.setText(bean.getContent());
            if (bean.getNum() > 99) {
                holder.messageCount.setVisibility(View.VISIBLE);
                holder.messageCount.setText("99+");
            } else if (bean.getNum() > 0) {
                holder.messageCount.setVisibility(View.VISIBLE);
                holder.messageCount.setText(bean.getNum() + "");
            } else {
                holder.messageCount.setVisibility(View.GONE);
            }
        }
        return convertView;
    }

    class ViewHolder {

        @FindView(R.id.num)
        TextView num;
        @FindView(R.id.text)
        TextView text;
        @FindView(R.id.messagecount)
        TextView messageCount;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
            view.setTag(this);
        }
    }
}
