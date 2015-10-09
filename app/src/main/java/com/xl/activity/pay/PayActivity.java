package com.xl.activity.pay;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;
import android.widget.Toast;

import com.bmob.pay.tool.BmobPay;
import com.bmob.pay.tool.PayListener;
import com.xl.activity.MainActivity;
import com.xl.activity.R;
import com.xl.activity.base.BaseBackActivity;
import com.xl.util.LogUtil;
import com.xl.util.MD5;
import com.xl.util.ToastUtil;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Created by Shen on 2015/6/22.
 */
@EActivity(R.layout.activity_pay)
public class PayActivity extends BaseBackActivity {


    @Override
    protected void init() {
        MD5.GetMD5Code(ac.deviceId).substring(8, 24);
    }

    @Click
    public void copy1() {
        new BmobPay(PayActivity.this).pay(0.01, "一个月会员", new PayListener() {

            // 因为网络等原因,支付结果未知(小概率事件),出于保险起见稍后手动查询
            @Override
            public void unknow() {
                LogUtil.d("unknow");
                ToastUtil.toast(mContext, "支付结果未知,请稍后手动查询");
            }

            // 支付成功,如果金额较大请手动查询确认
            @Override
            public void succeed() {
                LogUtil.d("succeed");
                ToastUtil.toast(mContext, "支付成功!");
            }

            // 无论成功与否,返回订单号
            @Override
            public void orderId(String orderId) {
                // 此处应该保存订单号,比如保存进数据库等,以便以后查询
                LogUtil.d("orderId:\t" + orderId);
            }

            // 支付失败,原因可能是用户中断支付操作,也可能是网络原因
            @Override
            public void fail(int code, String reason) {
                LogUtil.d("fail:\t" + code + "\t" + reason);
                ToastUtil.toast(mContext, "支付中断!");
            }
        });
    }

    @Click
    public void copy2() {
        new BmobPay(PayActivity.this).payByWX(0.01, "一个月会员", new PayListener() {

            // 因为网络等原因,支付结果未知(小概率事件),出于保险起见稍后手动查询
            @Override
            public void unknow() {
                LogUtil.d("unknow");
                ToastUtil.toast(mContext, "支付结果未知,请稍后手动查询");
            }

            // 支付成功,如果金额较大请手动查询确认
            @Override
            public void succeed() {
                LogUtil.d("succeed");
                ToastUtil.toast(mContext, "支付成功!");
            }

            // 无论成功与否,返回订单号
            @Override
            public void orderId(String orderId) {
                LogUtil.d("orderId:\t" + orderId);
            }

            // 支付失败,原因可能是用户中断支付操作,也可能是网络原因
            @Override
            public void fail(int code, String reason) {
                LogUtil.d("fail:\t"+code+"\t"+reason);
                // 当code为-2,意味着用户中断了操作
                // code为-3意味着没有安装BmobPlugin插件
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
                }else if(code == 10077){
                    BmobPay.ForceFree();
                    copy2();
                } else {
                    ToastUtil.toast(mContext, "支付中断!");
                }
            }
        });
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
