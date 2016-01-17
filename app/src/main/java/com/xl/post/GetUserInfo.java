package com.xl.post;

import android.content.Context;

import com.xl.application.AppClass;
import com.xl.bean.UserTable;
import com.xl.db.DBHelper;
import com.xl.util.JsonHttpResponseHandler;
import com.xl.util.ResultCode;
import com.xl.util.URLS;
import com.xl.util.Utils;

import org.json.JSONObject;

/**
 * Created by Shen on 2015/12/13.
 */
public class GetUserInfo {

    public GetUserInfo(Context context, final JsonHttpResponseHandler handler) {
        AppClass ac = (AppClass) context.getApplicationContext();
        ac.httpClient.post(URLS.GETUSERINFO, ac.getRequestParams(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccessCode(JSONObject jo) throws Exception {
                UserTable ut = null;
                try {
                    ut = Utils.jsonToBean(jo.optString(ResultCode.CONTENT), UserTable.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                DBHelper.userDao.updateUser(ut);

                if (handler != null) {
                    handler.onSuccessCode(jo);
                }
            }
        });
    }
}
