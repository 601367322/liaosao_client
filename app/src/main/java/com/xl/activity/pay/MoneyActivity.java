package com.xl.activity.pay;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.xl.activity.R;
import com.xl.activity.base.BaseBackActivity;
import com.xl.bean.Account;
import com.xl.util.JsonHttpResponseHandler;
import com.xl.util.ResultCode;
import com.xl.util.ToastUtil;
import com.xl.util.URLS;
import com.xl.util.Utils;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

/**
 * Created by Shen on 2016/1/24.
 */
@EActivity(R.layout.activity_money)
public class MoneyActivity extends BaseBackActivity {

    @ViewById(R.id.money)
    TextView money;
    @ViewById(R.id.cold_money)
    TextView coldMoney;

    @Override
    protected void init() {
        ac.httpClient.post(URLS.GETACCOUNT, ac.getRequestParams(), new JsonHttpResponseHandler(this, getString(R.string.loading)) {
            @Override
            public void onSuccessCode(JSONObject jo) throws Exception {
                refreshAccountUI(Utils.jsonToBean(jo.getString(ResultCode.CONTENT), Account.class));
            }
        });
    }

    public void refreshAccountUI(Account account) {
        money.setText(account.getCoin() + getString(R.string.coin));
        coldMoney.setText(account.getColdCoin() + getString(R.string.coin));
    }

    @Click(R.id.zhifubao)
    public void chongZhi() {
        new AlertDialog.Builder(this).setTitle("支付提醒").setMessage("由于第三方支付平台Bmob征收5%的手续费，所以充值1元所得结果是0.95烧币。").setPositiveButton("充值", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MoneyActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.dialog_pay_money, null);
                final EditText money = (EditText) view.findViewById(R.id.money);
                builder.setTitle("请输入充值额度").setView(view)
                        .setPositiveButton("充值",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        String timeStr = money.getText().toString();
                                        if (TextUtils.isEmpty(timeStr)) {
                                            return;
                                        } else {
                                            int time = Integer.valueOf(timeStr);
                                            if (time < 1) {
                                                ToastUtil.toast(mContext, "最低1块钱，谢谢");
                                                return;
                                            }
                                        }
//                                        sendChatRequest(timeStr);
                                    }
                                }).setNegativeButton("取消", null);
                AlertDialog dialog1 = builder.create();
                dialog1.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                dialog1.show();
            }
        }).setNegativeButton(R.string.cancle_send_btn, null).create().show();
    }
}
