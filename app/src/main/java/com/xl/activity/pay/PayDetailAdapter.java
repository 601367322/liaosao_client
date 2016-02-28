package com.xl.activity.pay;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.xl.activity.R;
import com.xl.activity.base.BaseAdapter;
import com.xl.bean.Pay;

import butterknife.Bind;

/**
 * Created by Shen on 2016/2/28.
 */
public class PayDetailAdapter extends BaseAdapter<Pay> {

    public PayDetailAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseHolder getHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public int getConvertView(int position) {
        return R.layout.pay_detail_list_item;
    }

    class ViewHolder extends BaseHolder {

        @Bind(R.id.name)
        TextView name;
        @Bind(R.id.time)
        TextView time;
        @Bind(R.id.money)
        TextView money;

        public ViewHolder(View view) {
            super(view);
        }

        @Override
        public void bind(Pay bean, int position) {
            if (bean.getTrade_state().equals("LOADING")) {
                name.setText(bean.getName() + "[正在审核]");
            } else {
                name.setText(bean.getName());
            }
            time.setText(bean.getCreate_time());

            String money_str = bean.getTotal_fee().toString();
            switch (bean.getPay_type()) {
                case "WECHATPAY":
                case "ALIPAY":
                    money_str = "+ " + money_str;
                    money.setTextColor(context.getResources().getColor(R.color.holo_green_light));
                    break;
                case "TIXIAN":
                    money_str = "- " + money_str;
                    money.setTextColor(context.getResources().getColor(R.color.holo_red_light));
                    break;
            }
            money.setText(money_str);
        }
    }
}
