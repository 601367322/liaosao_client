package com.xl.activity.chat;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xl.activity.R;
import com.xl.activity.base.BaseAdapter;
import com.xl.bean.ChatListBean;
import com.xl.bean.UserBean_6;
import com.xl.util.Utils;

import butterknife.Bind;

/**
 * Created by sbb on 2015/5/6.
 */
public class ChatListAdapters extends BaseAdapter<ChatListBean> {

    
    @Override
    public BaseHolder getHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public int getConvertView(int position) {
        return R.layout.chat_list_item;
    }

    public ChatListAdapters(Context context) {
        super(context);
    }

    class ViewHolder extends BaseHolder{

        @Bind(R.id.text)
        TextView text;
        @Bind(R.id.messagecount)
        TextView messageCount;
        @Bind(R.id.nickname)
        TextView nickname;
        @Bind(R.id.userlogo)
        ImageView userlogo;

        public ViewHolder(View view) {
            super(view);
        }

        @Override
        public void bind(ChatListBean bean, int position) {
            try {
                text.setText(bean.getContent());
                if (bean.getNum() > 99) {
                    messageCount.setVisibility(View.VISIBLE);
                    messageCount.setText("99+");
                } else if (bean.getNum() > 0) {
                    messageCount.setVisibility(View.VISIBLE);
                    messageCount.setText(bean.getNum() + "");
                } else {
                    messageCount.setVisibility(View.GONE);
                }
                if (bean.getFriend() != null) {
                    UserBean_6 ub = bean.getFriend().getBean();
                    nickname.setText(ub.nickname);
                    ImageLoader.getInstance().displayImage(ub.logo, userlogo, Utils.options_default_logo);
                }else{
                    nickname.setText("");
                    userlogo.setImageResource(R.drawable.default_logo);
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }
}
