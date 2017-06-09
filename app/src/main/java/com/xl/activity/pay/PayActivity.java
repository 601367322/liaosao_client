package com.xl.activity.pay;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;
import com.xl.activity.R;
import com.xl.activity.base.BaseBackActivity;
import com.xl.bean.VipCoin;
import com.xl.util.JsonHttpResponseHandler;
import com.xl.util.ResultCode;
import com.xl.util.ToastUtil;
import com.xl.util.URLS;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import c.b.BP;
import c.b.PListener;

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

        BP.init("2c9f0c5fbeb32f1b1bce828d29514f5d");

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
        if (coin != null && checkApp(PayType.ZHIFUBAO))
            BP.pay(coin.getName(), ac.deviceId, coin.getPrice(), true, new MyPayListener(mContext, PayType.ZHIFUBAO));
    }

    @Click
    public void copy2() {
        if (coin != null && checkApp(PayType.WEIXIN))
            BP.pay(coin.getName(), ac.deviceId, coin.getPrice(), false, new MyPayListener(mContext, PayType.WEIXIN));
    }

    public boolean checkApp(PayType payType) {
        if (payType == PayType.ZHIFUBAO) {
            if (!checkPackageInstalled("com.eg.android.AlipayGphone",
                    "https://www.alipay.com")) { // 支付宝支付要求用户已经安装支付宝客户端
                Toast.makeText(PayActivity.this, "请安装支付宝客户端", Toast.LENGTH_SHORT)
                        .show();
                return false;
            }
        } else {
            if (checkPackageInstalled("com.tencent.mm", "http://weixin.qq.com")) {// 需要用微信支付时，要安装微信客户端，然后需要插件
                // 有微信客户端，看看有无微信支付插件，170602更新了插件，这里可检查可不检查
                if (!BP.isAppUpToDate(this, "cn.bmob.knowledge", 8)) {
                    Toast.makeText(
                            PayActivity.this,
                            "监测到本机的支付插件不是最新版,最好进行更新,请先更新插件(无流量消耗)",
                            Toast.LENGTH_SHORT).show();
                    installApk("bp.db");
                    return false;
                }
            } else {// 没有安装微信
                Toast.makeText(PayActivity.this, "请安装微信客户端", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    /**
     * 检查某包名应用是否已经安装
     *
     * @param packageName 包名
     * @param browserUrl  如果没有应用市场，去官网下载
     * @return
     */
    private boolean checkPackageInstalled(String packageName, String browserUrl) {
        try {
            // 检查是否有支付宝客户端
            getPackageManager().getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            // 没有安装支付宝，跳转到应用市场
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=" + packageName));
                startActivity(intent);
            } catch (Exception ee) {// 连应用市场都没有，用浏览器去支付宝官网下载
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(browserUrl));
                    startActivity(intent);
                } catch (Exception eee) {
                    Toast.makeText(PayActivity.this,
                            "您的手机上没有没有应用市场也没有浏览器，我也是醉了，你去想办法安装支付宝/微信吧",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
        return false;
    }

    private static final int REQUESTPERMISSION = 101;

    private void installApk(String s) {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //申请权限
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUESTPERMISSION);
        } else {
            installBmobPayPlugin(s);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUESTPERMISSION) {
            if (permissions[0].equals(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    installBmobPayPlugin("bp.db");
                } else {
                    //提示没有权限，安装不了
                    Toast.makeText(PayActivity.this, "您拒绝了权限，这样无法安装支付插件", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public enum PayType {
        WEIXIN, ZHIFUBAO
    }

    class MyPayListener implements PListener {

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
            sendSuccessPost(0);
        }

        @Override
        public void fail(int code, String s) {
            if (code == 6001) {
                sendSuccessPost(0);
            } else {
                ac.cs.setVipOrder(null);
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
                                            installBmobPayPlugin("bp.db");
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
                } else if (code == 7777) {
                    ToastUtil.toast(context, "请安装微信");
                } else if (code == 8888) {
                    ToastUtil.toast(context, "微信版本太低，或者请手动打开微信，返回再试一遍~");
                } else {
                    ToastUtil.toast(context, "支付失败~");
                }
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

            File parentFile = new File(getDiskFileDir(getApplicationContext())).getParentFile();
            File updateDir = new File(parentFile, "apks");

            if (!updateDir.exists()) {
                updateDir.mkdirs();
            }

            File file = new File(updateDir, fileName + ".apk");
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

            if (Build.VERSION.SDK_INT >= 24) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri contentUri = FileProvider.getUriForFile(PayActivity.this, "com.xl.activity.fileprovider", file);
                intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
                startActivity(intent);
            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(Uri.parse("file://" + file),
                        "application/vnd.android.package-archive");
                startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getDiskFileDir(Context context) {
        String cachePath = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalFilesDir(null).getPath();
        } else {
            cachePath = context.getFilesDir().getPath();
        }
        return cachePath;
    }

    @UiThread(delay = 1000l)
    public void sendSuccessPost(final int n) {
        RequestParams params = ac.getRequestParams();
        params.put("orderId", ac.cs.getVipOrder());
        ac.httpClient.post(URLS.PAY, params, new JsonHttpResponseHandler(mContext, getString(R.string.loading)) {

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

            @Override
            public void onFailure() {
                if (n > 3) {
                    ToastUtil.toast(mContext, "算了，稍后重启会自动重试");
                    return;
                }
                ToastUtil.toast(mContext, "网络连接失败，正在重试第" + (n + 1) + "次");
                sendSuccessPost(n + 1);
            }
        });
    }
}
