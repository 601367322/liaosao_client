package com.xl.post;

import android.content.Context;

import com.xl.application.AppClass;
import com.xl.util.JsonHttpResponseHandler;
import com.xl.util.URLS;

import org.json.JSONObject;

/**
 * Created by Shen on 2015/12/13.
 */
public class GetUserAccount {

    public GetUserAccount(Context context, final JsonHttpResponseHandler handler) {
        AppClass ac = (AppClass) context.getApplicationContext();
        ac.httpClient.post(URLS.GETACCOUNT, ac.getRequestParams(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccessCode(JSONObject jo) throws Exception {
                if (handler != null) {
                    handler.onSuccessCode(jo);
                }
            }
        });
    }
}
