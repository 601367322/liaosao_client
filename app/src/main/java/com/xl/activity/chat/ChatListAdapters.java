package com.xl.activity.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xl.activity.R;
import com.xl.activity.base.BaseAdapterListView;
import com.xl.bean.ChatListBean;
import com.xl.bean.UserBean_6;
import com.xl.util.Utils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

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
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        try {
            ChatListBean bean = getItem(position);
            if (bean != null) {
                if (holder != null) {
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
                    if (bean.getFriend() != null) {
                        UserBean_6 ub = bean.getFriend().getBean();
                        holder.nickname.setText(ub.nickname);
                        ImageLoader.getInstance().displayImage(ub.logo, holder.userlogo, Utils.options_default_logo);
                    }else{
                        holder.nickname.setText("");
                        holder.userlogo.setImageResource(R.drawable.default_logo);
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return convertView;
    }

    class ViewHolder {

        @Bind(R.id.text)
        TextView text;
        @Bind(R.id.messagecount)
        TextView messageCount;
        @Bind(R.id.nickname)
        TextView nickname;
        @Bind(R.id.userlogo)
        ImageView userlogo;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
            view.setTag(this);
        }
    }
}
