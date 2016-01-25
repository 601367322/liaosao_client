package com.xl.util;

import android.app.ProgressDialog;
import android.content.Context;

import com.xl.activity.R;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;


public class JsonHttpResponseHandler extends
        com.loopj.android.http.JsonHttpResponseHandler {

    private ProgressDialog dialog = null;
    private Context context;

    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        // TODO Auto-generated method stub
        onSuccess(response);
    }

    public JsonHttpResponseHandler() {
        super();
    }

    public JsonHttpResponseHandler(Context context) {
        super();
        this.context = context;
    }

    public JsonHttpResponseHandler(Context context, String progress) {
        super();
        this.context = context;
        dialog = Utils.progress(context, progress);
    }

    public JsonHttpResponseHandler(Context context, String progress, boolean cancleable) {
        super();
        this.context = context;
        dialog = Utils.progress(context, progress);
        dialog.setCancelable(cancleable);
        dialog.setCanceledOnTouchOutside(cancleable);
    }

    public void onSuccess(JSONObject jo) {
        try {
            LogUtil.d(jo.toString());
            int status = jo.optInt(ResultCode.STATUS);
            switch (status) {
                case ResultCode.SUCCESS:
                    onSuccessCode(jo);
                    break;
                default:
                    onFailCode(jo);
                    break;
            }
        } catch (Exception e) {
            onFailure();
            e.printStackTrace();
        }
    }

    public void onSuccessCode(JSONObject jo) throws Exception {

    }

    public void onFailCode(JSONObject jo) {
        try {
            if (context != null) {
                if (jo == null) {
                    ToastUtil.toast(context, context.getString(R.string.error_net));
                } else if (jo.has(ResultCode.INFO)) {
                    ToastUtil.toast(context, jo.getString(ResultCode.INFO));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(int statusCode, Header[] headers,
                          String responseString, Throwable throwable) {
        // TODO Auto-generated method stub
        onFailure();
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
        onFailure();
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
        onFailure();
    }

    public void onFailure() {
        onFailCode(null);
    }

    @Override
    public void onFinish() {
        super.onFinish();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
