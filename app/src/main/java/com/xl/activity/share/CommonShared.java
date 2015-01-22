package com.xl.activity.share;

import android.content.Context;

public class CommonShared {
    private SharedDataUtil sp;
    private SharedDataUtil.SharedDataEditor editor;
    private final String SOUND = "sound";
    private final String VIBRATION = "vibration";

    public static final int ON = 1;
    public static final int OFF = 0;

    public CommonShared(Context context) {
        sp = SharedDataUtil.getInstance(context);
        editor = sp.getSharedDataEditor();
    }

    public void setSound(int s){
        editor.putInt(SOUND,s);
        editor.commit();
    }

    public int getSound(){
        return sp.getInt(SOUND,ON);
    }

    public void setVibration(int s){
        editor.putInt(VIBRATION,s);
        editor.commit();
    }

    public int getVibration(){
        return sp.getInt(VIBRATION,ON);
    }
}
