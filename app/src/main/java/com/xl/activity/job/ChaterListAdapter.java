package com.xl.activity.job;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xl.activity.R;
import com.xl.activity.base.BaseAdapter;
import com.xl.bean.ChatRoom;
import com.xl.bean.UserBean;
import com.xl.custom.CircleImageView;
import com.xl.db.DBHelper;
import com.xl.util.StaticFactory;
import com.xl.util.ToastUtil;
import com.xl.util.Utils;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Shen on 2016/1/16.
 */
public class ChaterListAdapter extends BaseAdapter<ChatRoom> {

    public ChaterListAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseHolder getHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public int getConvertView(int position) {
        return R.layout.content_chaterlist_item;
    }

    class ViewHolder extends BaseHolder {

        @Bind(R.id.userlogo)
        CircleImageView userlogo;
        @Bind(R.id.nickname)
        TextView nickname;
        @Bind(R.id.juli)
        TextView juli;
        @Bind(R.id.sex)
        TextView sex;
        @Bind(R.id.age)
        TextView age;
        @Bind(R.id.ratingBar)
        RatingBar ratingBar;
        @Bind(R.id.price)
        TextView price;
        @Bind(R.id.minMin)
        TextView minMin;
        @Bind(R.id.maxMin)
        TextView maxMin;
        @Bind(R.id.btnChat)
        Button btnChat;
        @Bind(R.id.radio_sex)
        TextView radioSex;

        public ViewHolder(View view) {
            super(view);
        }

        @Override
        public void bind(ChatRoom bean, int position) {
            try {
                UserBean userBean = bean.getUser().getBean();
                nickname.setText(userBean.getNickname());
                sex.setText(userBean.getSex() == 1 ? "女" : "男");
                if (!TextUtils.isEmpty(ac.cs.getLat()) && !TextUtils.isEmpty(userBean.getLat())) {
                    juli.setText(Utils.getDistance(Double.valueOf(ac.cs.getLng()), Double.valueOf(ac.cs.getLat()), Double.valueOf(userBean.getLng()), Double.valueOf(userBean.getLat())) + "km");
                } else {
                    juli.setText("");
                }
                price.setText(bean.getPrice() + "元/分钟");
                minMin.setText(bean.getMinTime() + "分钟");
                maxMin.setText(bean.getMaxTime() + "分钟");
                age.setText(userBean.getAge() + "岁");
                radioSex.setText(bean.getSex() == 0 ? "女" : "男");

                ImageLoader.getInstance().displayImage(userBean.getLogo() + StaticFactory._160x160, userlogo);

                btnChat.setTag(bean);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @OnClick(R.id.btnChat)
        public void btnChatClick(View view) {
            ChatRoom room = (ChatRoom) view.getTag();
            //判断是否是自己的
            if (room.getDeviceId().equals(ac.deviceId)) {
                ToastUtil.toast(context, context.getString(R.string.danteng_chat_with_yourself));
                return;
            }
            //判断性别
            if (!DBHelper.userDao.getUserTableByDeviceId(ac.deviceId).getBean().getSex().equals(room.getSex())) {
                ToastUtil.toast(context, context.getString(R.string.sex_not));
                return;
            }
            ChatRightNowDialogFragment_.builder().build().show(((AppCompatActivity) context).getSupportFragmentManager(), "dialog");
        }
    }
}
