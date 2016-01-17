package com.xl.activity.job;

import android.content.Context;

import com.xl.api.BaseApi;
import com.xl.util.URLS;

/**
 * Created by Shen on 2016/1/16.
 */
public class ChaterListApi extends BaseApi {

    public ChaterListApi(Context context) {
        super(context);
    }

    @Override
    public String getUrl() {
        return URLS.GET_CHAT_ROOM_LIST;
    }
}
