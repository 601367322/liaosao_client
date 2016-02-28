package com.xl.activity.pay;

import android.content.Context;

import com.xl.api.BaseApi;
import com.xl.util.URLS;

/**
 * Created by Shen on 2016/2/28.
 */
public class PayDetailApi extends BaseApi {

    public PayDetailApi(Context context) {
        super(context);
    }

    @Override
    public String getUrl() {
        return URLS.GET_MY_PAY_DETAIL_LIST;
    }
}
