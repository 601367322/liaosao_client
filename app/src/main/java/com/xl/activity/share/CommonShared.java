package com.xl.activity.share;

import android.content.Context;

public class CommonShared {
    private SharedDataUtil sp;
    private SharedDataUtil.SharedDataEditor editor;
    private final String LOCATION = "location";// 定位的城市

    public CommonShared(Context context) {
        sp = SharedDataUtil.getInstance(context);
        editor = sp.getSharedDataEditor();
    }

    public void setLocation(String str) {
        editor.putString(LOCATION, str);
        editor.commit();
    }

    public String getLocation() {
        return sp.getString(LOCATION, "北京");
    }
}
