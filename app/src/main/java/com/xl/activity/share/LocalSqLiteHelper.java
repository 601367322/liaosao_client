package com.xl.activity.share;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * <b>数据库操作助手</b></br>
 *
 * @author Jony
 * @12-7-12
 * @上午10:53
 */
public class LocalSqLiteHelper extends SQLiteOpenHelper {
    public LocalSqLiteHelper(Context context, String name,
                             SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //创建表
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS "
                + SharedData.TABLE_NAME + "(" + SharedData.ID
                + " integer primary key autoincrement,"
                + SharedData.KEY + " varchar,"
                + SharedData.M_STR + " varchar,"
                + SharedData.M_BOOLEAN + " integer(1),"
                + SharedData.M_DATE + " datetime,"
                + SharedData.M_INT + " integer,"
                + SharedData.M_LONG + " int8, "
                + SharedData.M_FLOAT + " float,"
                + SharedData.DATA_TYPE + " integer" + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion,
                          int newVersion) {
        onCreate(sqLiteDatabase);
    }
}
