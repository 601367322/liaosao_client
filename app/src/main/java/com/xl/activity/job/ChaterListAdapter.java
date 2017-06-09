package com.xl.activity.job;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xl.activity.R;
import com.xl.activity.base.BaseAdapter;
import com.xl.bean.ChatRoom;
import com.xl.bean.UserBean;
import com.xl.custom.CircleImageView;
import com.xl.db.DBHelper;
import com.xl.util.JsonHttpResponseHandler;
import com.xl.util.StaticFactory;
import com.xl.util.ToastUtil;
import com.xl.util.URLS;
import com.xl.util.Utils;

import org.json.JSONObject;

import butterknife.BindView;
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

        @BindView(R.id.userlogo)
        CircleImageView userlogo;
        @BindView(R.id.nickname)
        TextView nickname;
        @BindView(R.id.juli)
        TextView juli;
        @BindView(R.id.sex)
        TextView sex;
        @BindView(R.id.age)
        TextView age;
        @BindView(R.id.ratingBar)
        RatingBar ratingBar;
        @BindView(R.id.price)
        TextView price;
        @BindView(R.id.minMin)
        TextView minMin;
        @BindView(R.id.maxMin)
        TextView maxMin;
        @BindView(R.id.btnChat)
        Button btnChat;
        @BindView(R.id.radio_sex)
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
                radioSex.setText(bean.getSex() == 1 ? "女" : "男");

                ImageLoader.getInstance().displayImage(userBean.getLogo() + StaticFactory._160x160, userlogo);

                btnChat.setTag(bean);

                if (bean.getDeviceId().equals(ac.deviceId)) {
                    btnChat.setText("关闭");
                } else {
                    btnChat.setText("立刻聊");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @OnClick(R.id.btnChat)
        public void btnChatClick(View view) {
            final ChatRoom room = (ChatRoom) view.getTag();
            //判断是否是自己的
            if (room.getDeviceId().equals(ac.deviceId)) {
                RequestParams params = ac.getRequestParams();
                params.put("roomId", room.getId());
                ac.httpClient.post(context, URLS.DELETE_CHAT_ROOM, params, new JsonHttpResponseHandler(context, context.getString(R.string.loading)) {
                    @Override
                    public void onSuccessCode(JSONObject jo) throws Exception {
                        getList().remove(room);
                        notifyDataSetChanged();
                    }
                });
                return;
            }
            //判断性别
            if (!DBHelper.userDao.getUserTableByDeviceId(ac.deviceId).getBean().getSex().equals(room.getSex())) {
                ToastUtil.toast(context, context.getString(R.string.sex_not));
                return;
            }
            ChatRightNowDialogFragment_.builder().room(room).build().show(((AppCompatActivity) context).getSupportFragmentManager(), "dialog");
        }
    }
}
