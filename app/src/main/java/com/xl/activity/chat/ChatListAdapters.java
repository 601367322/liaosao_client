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
        holder.num.setText(String.valueOf(getItem(position).getId()));
        holder.text.setText(getItem(position).getContent());
        return convertView;
    }

    class ViewHolder {

        @FindView(R.id.num)
        TextView num;
        @FindView(R.id.text)
        TextView text;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
            view.setTag(this);
        }
    }
}
