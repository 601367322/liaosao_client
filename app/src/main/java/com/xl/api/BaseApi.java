package com.xl.api;

import android.content.Context;

import com.loopj.android.http.RequestParams;
import com.xl.application.AppClass;
import com.xl.util.Utils;

import java.io.Serializable;

/**
 * Created by Shen on 2015/5/30.
 */
public abstract class BaseApi implements Serializable {

    public Context context;
    public AppClass ac;
    private RequestParams params;

    public int currentPage = 0;

    public BaseApi(Context context) {
        this.context = context;
        if (context != null) {
            ac = (AppClass) context.getApplicationContext();
            params = ac.getRequestParams();
        }
    }

    public abstract String getUrl();

    public RequestParams getParams() {
        return params;
    }

    public void setPage(int currentPage) {
        params.put("page", currentPage);
        this.currentPage = currentPage;
    }

    public void initParam(Object... obj) {
    }

}