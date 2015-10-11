package com.xl.activity.pay;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.widget.Button;

import com.bmob.pay.tool.BmobPay;
import com.bmob.pay.tool.PayListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;
import com.xl.activity.R;
import com.xl.activity.base.BaseBackActivity;
import com.xl.bean.VipCoin;
import com.xl.util.JsonHttpResponseHandler;
import com.xl.util.MD5;
import com.xl.util.ResultCode;
import com.xl.util.ToastUtil;
import com.xl.util.URLS;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Created by Shen on 2015/6/22.
 */
@EActivity(R.layout.activity_pay)
public class PayActivity extends BaseBackActivity {


    @ViewById
    Button copy1, copy2;

    VipCoin coin = null;

    @Override
    protected void init() {
        RequestParams params = ac.getRequestParams();
        params.put("id", 1);
        ac.httpClient.post(URLS.VIPDETAIL, params, new JsonHttpResponseHandler(mContext, getString(R.string.loading)) {

            @Override
            public void onStart() {
                super.onStart();
                copy1.setEnabled(false);
                copy2.setEnabled(false);
            }

            @Override
            public void onSuccessCode(JSONObject jo) throws Exception {
                super.onSuccessCode(jo);
                copy1.setEnabled(true);
                copy2.setEnabled(true);
                coin = new Gson().fromJson(jo.getString(ResultCode.INFO), new TypeToken<VipCoin>() {
                }.getType());
            }
        });
    }

    @Click
    public void copy1() {
        if (coin != null)
            new BmobPay(PayActivity.this).pay(coin.getPrice(), coin.getName(), MD5.GetMD5Code(ac.deviceId).substring(8, 24), new MyPayListener(mContext, PayType.ZHIFUBAO));
    }

    @Click
    public void copy2() {
        if (coin != null)
            new BmobPay(PayActivity.this).payByWX(coin.getPrice(), coin.getName(), MD5.GetMD5Code(ac.deviceId).substring(8, 24), new MyPayListener(mContext, PayType.WEIXIN));
    }

    public enum PayType {
        WEIXIN, ZHIFUBAO
    }

    class MyPayListener implements PayListener {

        private PayType type;
        private Context context;

        public MyPayListener(Context context, PayType type) {
            this.context = context;
            this.type = type;
        }

        @Override
        public void orderId(String orderId) {
            ac.cs.setVipOrder(orderId);
        }

        @Override
        public void succeed() {
            RequestParams params = ac.getRequestParams();
            params.put("orderId", ac.cs.getVipOrder());
            ac.httpClient.post(URLS.PAY, params, new JsonHttpResponseHandler(mContext, "正在完成充值") {

                @Override
                public void onSuccess(JSONObject jo) {
                    super.onSuccess(jo);

                    ac.cs.setVipOrder(null);
                }

                @Override
                public void onSuccessCode(JSONObject jo) throws Exception {
                    super.onSuccessCode(jo);

                    ToastUtil.toast(mContext, "充值成功，请重启软件");
                }

            });
        }

        @Override
        public void fail(int code, String s) {
            ac.cs.setVipOrder(null);
            BmobPay.ForceFree();
            if (code == -3) {
                new AlertDialog.Builder(mContext)
                        .setMessage(
                                "监测到你尚未安装支付插件,无法进行微信支付,请选择安装插件(已打包在本地,无流量消耗)或是用支付宝支付")
                        .setPositiveButton("安装",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(
                                            DialogInterface dialog,
                                            int which) {
                                        installBmobPayPlugin("BmobPayPlugin.apk");
                                    }
                                })
                        .setNegativeButton("支付宝支付",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(
                                            DialogInterface dialog,
                                            int which) {
                                        copy1();
                                    }
                                }).create().show();
            } else if (code == 10777) {
                switch (type) {
                    case WEIXIN:
                        copy2();
                        break;
                    case ZHIFUBAO:
                        copy1();
                        break;
                }
            } else {
                ToastUtil.toast(context, "支付失败~");
            }
        }

        @Override
        public void unknow() {
            ToastUtil.toast(mContext, "支付结果未知,请稍后重启软件。");
        }
    }

    void installBmobPayPlugin(String fileName) {
        try {
            InputStream is = getAssets().open(fileName);
            File file = new File(Environment.getExternalStorageDirectory()
                    + File.separator + fileName);
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
}
