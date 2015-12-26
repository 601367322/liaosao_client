package com.xl.post;

import android.content.Context;

import com.xl.application.AppClass;
import com.xl.bean.UserTable_6;
import com.xl.db.DBHelper;
import com.xl.util.JsonHttpResponseHandler;
import com.xl.util.ResultCode;
import com.xl.util.URLS;
import com.xl.util.Utils;

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
                UserTable_6 ut = Utils.jsonToBean(jo.optString(ResultCode.CONTENT), UserTable_6.class);

                DBHelper.userDao.updateUser(ut);

                if (handler != null) {
                    handler.onSuccessCode(jo);
                }
            }
        });
    }
}
