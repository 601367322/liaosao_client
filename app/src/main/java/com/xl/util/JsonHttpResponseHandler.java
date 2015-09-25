package com.xl.util;

import android.app.ProgressDialog;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class JsonHttpResponseHandler extends
        com.loopj.android.http.JsonHttpResponseHandler {

    private ProgressDialog dialog = null;

    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        // TODO Auto-generated method stub
        onSuccess(response);
    }

    public JsonHttpResponseHandler() {
        super();
    }

    public JsonHttpResponseHandler(Context context, String progress) {
        super();
        dialog = Utils.progress(context, progress);
    }

    public JsonHttpResponseHandler(Context context, String progress,boolean cancleable) {
        super();
        dialog = Utils.progress(context, progress);
        dialog.setCancelable(cancleable);
        dialog.setCanceledOnTouchOutside(cancleable);
    }

    public void onSuccess(JSONObject jo) {
        int status = jo.optInt(ResultCode.STATUS);
        switch (status) {
            case ResultCode.SUCCESS:
                onSuccessCode(jo);
                break;
            case ResultCode.FAIL:
                onFailCode();
                break;
        }
    }

    public void onSuccessCode(JSONObject jo){

    }

    public void onFailCode(){

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
        onFailCode();
    }

    @Override
    public void onFinish() {
        super.onFinish();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
