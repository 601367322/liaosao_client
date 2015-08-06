package com.xl.activity.pay;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.widget.EditText;

import com.xl.activity.R;
import com.xl.activity.base.BaseBackActivity;
import com.xl.util.MD5;
import com.xl.util.ToastUtil;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

/**
 * Created by Shen on 2015/6/22.
 */
@EActivity(R.layout.activity_pay)
public class PayActivity extends BaseBackActivity {

    @ViewById
    EditText md5;

    @Override
    protected void init() {
        md5.setText(MD5.GetMD5Code(ac.deviceId).substring(8, 24));
    }

    @Click
    public void copy1() {
        ClipboardManager clipboard = (ClipboardManager)
                getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("帐号", "bjshenbingbing17@163.com");
        clipboard.setPrimaryClip(clip);
        ToastUtil.toast(this, "已复制");
    }

    @Click
    public void copy2() {
        ClipboardManager clipboard = (ClipboardManager)
                getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("支付码", MD5.GetMD5Code(ac.deviceId).substring(8, 24));
        clipboard.setPrimaryClip(clip);
        ToastUtil.toast(this, "已复制");
    }

}
