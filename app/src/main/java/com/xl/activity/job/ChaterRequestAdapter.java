package com.xl.activity.job;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xl.activity.R;
import com.xl.activity.base.BaseAdapter;
import com.xl.bean.ChatRoomRequest;
import com.xl.bean.UserBean;
import com.xl.custom.CircleImageView;
import com.xl.util.StaticFactory;
import com.xl.util.Utils;

import butterknife.Bind;

/**
 * Created by Shen on 2016/3/3.
 */
public class ChaterRequestAdapter extends BaseAdapter<ChatRoomRequest> {

    public ChaterRequestAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseHolder getHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public int getConvertView(int position) {
        return R.layout.chater_request_item;
    }

    class ViewHolder extends BaseHolder {
        @Bind(R.id.userlogo)
        CircleImageView userlogo;
        @Bind(R.id.nickname)
        TextView nickname;
        @Bind(R.id.user_detail)
        TextView userDetail;
        @Bind(R.id.content)
        TextView content;
        @Bind(R.id.agree)
        Button agree;
        @Bind(R.id.disagree)
        TextView disagree;
        @Bind(R.id.btns_ll)
        View btnsLL;
        @Bind(R.id.confirmed)
        TextView confirmed;

        @Override
        public void bind(ChatRoomRequest bean, int position) {
            UserBean user = bean.getUser().getBean();
            ImageLoader.getInstance().displayImage(user.getLogo() + StaticFactory._160x160, userlogo, Utils.options_default_logo);
            nickname.setText(user.getNickname());
            userDetail.setText(user.getSex() == 1 ? "女" : "男" + "　" + user.getAge() + "岁　" + (user.isVip() ? "会员" : ""));
            content.setText("购买" + bean.getTimes() + "分钟");

            switch (bean.getState()) {
                case 0:
                    btnsLL.setVisibility(View.VISIBLE);
                    confirmed.setVisibility(View.GONE);
                    break;
                default:
                    btnsLL.setVisibility(View.GONE);
                    confirmed.setVisibility(View.VISIBLE);
                    switch (bean.getState()) {
                        case 1:
                            confirmed.setText("已同意");
                            break;
                        case 2:
                            confirmed.setText("已拒绝");
                            break;
                    }
                    break;
            }
        }

        public ViewHolder(View view) {
            super(view);
        }
    }
}
