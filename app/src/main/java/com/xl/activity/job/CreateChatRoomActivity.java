package com.xl.activity.job;

import android.content.Intent;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.loopj.android.http.RequestParams;
import com.xl.activity.R;
import com.xl.activity.base.BaseBackActivity;
import com.xl.bean.ChatRoom;
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
 * Created by Shen on 2015/12/26.
 */
@EActivity(R.layout.activity_create_room)
public class CreateChatRoomActivity extends BaseBackActivity implements RadioGroup.OnCheckedChangeListener {

    public static final int MaxTime = 60;

    @ViewById(R.id.price)
    EditText priceEdit;
    @ViewById(R.id.boy_radio)
    RadioButton boyRadio;
    @ViewById(R.id.girl_radio)
    RadioButton girlRadio;
    @ViewById(R.id.all_radio)
    RadioButton allRadio;
    @ViewById(R.id.sex)
    RadioGroup sexGroup;
    @ViewById(R.id.min_time)
    EditText minTimeEdit;
    @ViewById(R.id.max_time)
    EditText maxTimeEdit;
    @ViewById(R.id.create)
    Button create;

    int chooseSex = 2;//默认男

    @Override
    protected void init() {
        sexGroup.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.boy_radio:
                chooseSex = 2;
                break;
            case R.id.girl_radio:
                chooseSex = 1;
                break;
            case R.id.all_radio:
                chooseSex = -1;
                break;
        }
    }

    @Click(R.id.create)
    public void createBtnClick() {
        String price = priceEdit.getText().toString();
        String minTime = minTimeEdit.getText().toString();
        String maxTime = maxTimeEdit.getText().toString();
        if (TextUtils.isEmpty(price)) {
            ToastUtil.toast(this, "不要钱了？不要钱也要写个0！！！");
            priceEdit.requestFocus();
            Utils.openSoftKeyboard(priceEdit);
            return;
        }
        if (TextUtils.isEmpty(minTime)) {
            ToastUtil.toast(this, "为了你好，请填写最短时间！！！！");
            minTimeEdit.requestFocus();
            Utils.openSoftKeyboard(minTimeEdit);
            return;
        }
        if (TextUtils.isEmpty(maxTime)) {
            ToastUtil.toast(this, "为了你好，请填写最长时间！！！！");
            maxTimeEdit.requestFocus();
            Utils.openSoftKeyboard(maxTimeEdit);
            return;
        }
        if (Integer.valueOf(minTime) > Integer.valueOf(maxTime)) {
            ToastUtil.toast(this, "最短时间大于最长时间？");
            maxTimeEdit.requestFocus();
            Utils.openSoftKeyboard(maxTimeEdit);
            return;
        }
        if (Integer.valueOf(maxTime) > MaxTime) {
            ToastUtil.toast(this, "最多一个小时！！！");
            maxTimeEdit.requestFocus();
            Utils.openSoftKeyboard(maxTimeEdit);
            return;
        }
        if (Float.valueOf(price) < 0.1f) {
            ToastUtil.toast(this, "最少1毛！！！");
            priceEdit.requestFocus();
            Utils.openSoftKeyboard(priceEdit);
            return;
        }
        java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
        RequestParams params = ac.getRequestParams();
        params.put("price", df.format(price));
        params.put("sex", chooseSex);
        params.put("minTime", minTime);
        params.put("maxTime", maxTime);
        ac.httpClient.post(URLS.CREATE_CHAT_ROOM, params, new JsonHttpResponseHandler(this, getString(R.string.loading)) {
            @Override
            public void onSuccessCode(JSONObject jo) throws Exception {
                ChatRoom room = Utils.jsonToBean(jo.getString(ResultCode.CONTENT), ChatRoom.class);
                Intent intent = new Intent();
                intent.putExtra("bean", room);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
