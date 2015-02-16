package com.xl.activity.share;

import android.content.Context;

public class CommonShared {
    private SharedDataUtil sp;
    private SharedDataUtil.SharedDataEditor editor;
    private final String SOUND = "sound";
    private final String VIBRATION = "vibration";
    private final String SEX = "sex";

    public static final int ON = 1;
    public static final int OFF = 0;

    private final String HADSETSEX = "had_set_sex";

    public CommonShared(Context context) {
        sp = SharedDataUtil.getInstance(context);
        editor = sp.getSharedDataEditor();
    }

    public void setSound(int s) {
        editor.putInt(SOUND, s);
        editor.commit();
    }

    public int getSound() {
        return sp.getInt(SOUND, ON);
    }

    public void setVibration(int s) {
        editor.putInt(VIBRATION, s);
        editor.commit();
    }

    public int getVibration() {
        return sp.getInt(VIBRATION, ON);
    }

    public void setSex(int s) {
        editor.putInt(SEX, s);
        editor.commit();
    }

    public int getSex() {
        return sp.getInt(SEX, 0);
    }

    public void setHadSex(int s) {
        editor.putInt(HADSETSEX, s);
        editor.commit();
    }

    public int getHadSex() {
        return sp.getInt(HADSETSEX, OFF);
    }
}
