package com.xl.activity.pay;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;
import com.xl.activity.R;
import com.xl.activity.base.BaseBackActivity;
import com.xl.activity.share.CommonShared;
import com.xl.bean.Account;
import com.xl.bean.UnSuccessOrder;
import com.xl.db.DBHelper;
import com.xl.util.JsonHttpResponseHandler;
import com.xl.util.MD5;
import com.xl.util.ResultCode;
import com.xl.util.ToastUtil;
import com.xl.util.URLS;
import com.xl.util.Utils;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import c.b.BP;
import c.b.PListener;

/**
 * Created by Shen on 2016/1/24.
 */
@EActivity(R.layout.activity_money)
public class MoneyActivity extends BaseBackActivity {

    @ViewById(R.id.money)
    TextView money;
    @ViewById(R.id.cold_money)
    TextView coldMoney;
    @ViewById
    Button zhifubao, tixian, detail;


    @Override
    protected void init() {

        BP.init(this, "2c9f0c5fbeb32f1b1bce828d29514f5d");

        updateAccount();
    }

    public void updateAccount() {
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
        zhifubao.setEnabled(true);
        detail.setEnabled(true);
        tixian.setEnabled(true);
    }

    @Click(R.id.zhifubao)
    public void chongZhi() {
        if (ac.cs.getZHIFUTIXING() == CommonShared.ON) {
            new AlertDialog.Builder(this).setTitle("支付提醒").setMessage("由于第三方支付平台Bmob征收5%的手续费，所以充值1烧币应支付1.05元").setPositiveButton("充值", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    chosePayType();
                }
            }).setNegativeButton(R.string.cancle_send_btn, null)
                    .setNeutralButton("不再提醒", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ac.cs.setZHIFUTIXING(CommonShared.OFF);
                            chosePayType();
                        }
                    }).create().show();
        } else {
            chosePayType();
        }
    }

    public void chosePayType() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MoneyActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_pay_money, null);
        final EditText money = (EditText) view.findViewById(R.id.money);
        builder.setTitle("请输入充值额度").setView(view)
                .setPositiveButton("充值",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                final String moneyStr = money.getText().toString();
                                if (TextUtils.isEmpty(moneyStr)) {
                                    return;
                                } else {
                                    int money = Integer.valueOf(moneyStr);
                                    if (money < 1) {
                                        ToastUtil.toast(mContext, "最低1块钱，谢谢");
                                        return;
                                    }
                                    new AlertDialog.Builder(mContext).setItems(new String[]{"支付宝支付", "微信支付"}, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            float money = Float.valueOf(moneyStr) + Float.valueOf(moneyStr) * 0.05f;
                                            switch (which) {
                                                case 0:
                                                    startPay(Integer.valueOf(moneyStr), PayActivity.PayType.ZHIFUBAO, money);
                                                    break;
                                                case 1:
                                                    startPay(Integer.valueOf(moneyStr), PayActivity.PayType.WEIXIN, money);
                                                    break;
                                            }
                                        }
                                    }).create().show();
                                }
                            }
                        }).setNegativeButton("取消", null);
        AlertDialog dialog1 = builder.create();
        dialog1.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog1.show();
    }

    public void startPay(final int n, final PayActivity.PayType type, final float money) {
        BP.pay(this, n + "个烧币", MD5.GetMD5Code(ac.deviceId).substring(8, 24), 0.01, type == PayActivity.PayType.ZHIFUBAO ? true : false, new PListener() {

            String orderId = "";

            @Override
            public void orderId(String s) {
                orderId = s;
            }

            @Override
            public void succeed() {
                sendSuccessPost(0, orderId);
            }

            @Override
            public void fail(int code, String s) {
                BP.ForceFree();
                if (code == -3) {
                    new AlertDialog.Builder(mContext)
                            .setMessage(
                                    "监测到你尚未安装支付插件,无法进行微信支付,请选择安装插件(已打包在本地,无流量消耗)还是用支付宝支付")
                            .setPositiveButton("安装",
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            installBmobPayPlugin("bp_wx.db");
                                        }
                                    })
                            .setNegativeButton("支付宝支付",
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            startPay(n, type, money);
                                        }
                                    }).create().show();
                } else if (code == 10777) {
                    startPay(n, type, money);
                } else if (code == 8888) {
                    ToastUtil.toast(mContext, "微信版本太低，或者请手动打开微信，返回再试一遍~");
                } else if (code == 6001) {
                    sendSuccessPost(0, orderId);
                } else {
                    ToastUtil.toast(mContext, "支付失败~");
                }
            }

            @Override
            public void unknow() {
                ToastUtil.toast(mContext, "支付结果未知,请稍后重启软件。");
            }
        });
    }

    void installBmobPayPlugin(String fileName) {
        try {
            InputStream is = getAssets().open(fileName);
            File file = new File(Environment.getExternalStorageDirectory()
                    + File.separator + fileName + ".apk");
            if (file.exists())
                file.delete();
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            byte[] temp = new byte[1024];
            int i = 0;
            while ((i = is.read(temp)) > 0) {
                fos.write(temp, 0, i);
            }
            fos.close();
            is.close();

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.parse("file://" + file),
                    "application/vnd.android.package-archive");
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendSuccessPost(final int n, final String orderId) {
        RequestParams params = ac.getRequestParams();
        params.put("orderId", orderId);
        ac.httpClient.post(URLS.PAY_MONEY, params, new JsonHttpResponseHandler(mContext, "正在完成充值") {

            @Override
            public void onStart() {
                super.onStart();
                UnSuccessOrder order = DBHelper.orderDao.getOrder(orderId);
                if (order == null) {
                    order = new UnSuccessOrder();
                    order.orderId = orderId;
                    DBHelper.orderDao.create(order);
                }
            }

            @Override
            public void onSuccess(JSONObject jo) {
                super.onSuccess(jo);
                DBHelper.orderDao.delete(orderId);
            }

            @Override
            public void onSuccessCode(JSONObject jo) throws Exception {
                super.onSuccessCode(jo);
                ToastUtil.toast(mContext, "充值成功");
                refreshAccountUI(Utils.jsonToBean(jo.getString(ResultCode.CONTENT), Account.class));
            }

            @Override
            public void onFailure() {
                if (n > 3) {
                    ToastUtil.toast(mContext, "算了，稍后重启会自动重试");
                    return;
                }
                ToastUtil.toast(mContext, "网络连接失败，正在重试第" + (n + 1) + "次");
                sendSuccessPost(n + 1, orderId);
            }
        });
    }

    @Click(R.id.detail)
    public void detailClick() {
        PayDetailActivity_.intent(this).start();
    }

    @Click(R.id.tixian)
    public void tixianClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MoneyActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_pay_tixian, null);
        final EditText money = (EditText) view.findViewById(R.id.money);
        final EditText zhifubao = (EditText) view.findViewById(R.id.zhifubao);
        builder.setTitle("请填写提现信息").setView(view)
                .setPositiveButton("提交",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                final String moneyStr = money.getText().toString();
                                final String zhifubaoStr = zhifubao.getText().toString();
                                if (TextUtils.isEmpty(moneyStr) || TextUtils.isEmpty(zhifubaoStr)) {
                                    return;
                                } else {
                                    int money = Integer.valueOf(moneyStr);
                                    if (money < 100) {
                                        ToastUtil.toast(mContext, "最低100块钱，谢谢");
                                        return;
                                    }
                                    RequestParams params = ac.getRequestParams();
                                    params.put("zhifubao", zhifubaoStr);
                                    params.put("money", money);
                                    ac.httpClient.post(mContext, URLS.PAY_TIXIAN, params, new JsonHttpResponseHandler(mContext, getString(R.string.loading)) {
                                        @Override
                                        public void onSuccessCode(JSONObject jo) throws Exception {
                                            super.onSuccessCode(jo);
                                            ToastUtil.toast(mContext, "提交成功,等待审核");
                                            refreshAccountUI(Utils.jsonToBean(jo.getString(ResultCode.CONTENT), Account.class));
                                        }
                                    });
                                }
                            }
                        }).setNegativeButton("取消", null);
        AlertDialog dialog1 = builder.create();
        dialog1.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog1.show();
    }
}
