package com.xl.activity.share;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <b>SharedData对象数据库操作助手</b></br>
 *
 * @author Jony
 * @13-5-24
 * @下午5:13
 * @since xCloud4.1
 */
public class SharedDataSqLiteHelper {

    /**
     * 数据库存放code值 1 - 表示 boolean 中的 true
     */
    public static final int TRUE_CODE = 1;

    public static final int VERSION = 1;

    public static final String DB_NAME = "myTest.db";

    /**
     * 时间格式
     */
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 本地数据库助手
     */
    private LocalSqLiteHelper localSqLiteHelper;

    private SQLiteDatabase sqLiteDatabase;

    private boolean isClosed;

    /**
     * 时间格式化对象
     */
    private SimpleDateFormat sDateFormat;

    /**
     * 默认创建可写数据库
     *
     * @param context
     */
    public SharedDataSqLiteHelper(Context context) {
        localSqLiteHelper = new LocalSqLiteHelper(context,
                DB_NAME, null, VERSION);
        sDateFormat = new SimpleDateFormat(DATE_FORMAT);

        try {
            sqLiteDatabase = localSqLiteHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存数据
     *
     * @param sharedData
     * @return
     */
    public long putData(SharedData sharedData) {
        long id = -1;
        if (sharedData == null || TextUtils.isEmpty(sharedData.getKey()) || sharedData.getDataType() == null) {
            return id;
        }

        long dataId = hasData(sharedData);

        if (dataId > 0) {
            sharedData.setId(dataId);
            int row = updateDataById(sharedData);

            if (row > 0) {
                return dataId;
            }
        } else {
            id = saveData(sharedData);
        }

        return id;
    }

    /**
     * 根据Key 删除数据
     *
     * @param key
     * @return
     */
    public boolean remove(String key) {
        if (TextUtils.isEmpty(key)) {
            return false;
        }

        long rows = -1;

        try {
            rows = sqLiteDatabase.delete(SharedData.TABLE_NAME,
                    SharedData.KEY + " = ?", new String[]
                            {
                                    key
                            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rows > 0;
    }

    /**
     * 根据Key获取相应的数据
     *
     * @param key
     * @return
     */
    public SharedData getGlobalDataByKey(String key) {
        if (TextUtils.isEmpty(key)) {
            return null;
        }

        SharedData sharedData = null;
        String sql = "select * from " + SharedData.TABLE_NAME + " where "
                + SharedData.KEY + "=\"" + key + "\" order by "
                + SharedData.ID + " desc";
        Cursor cursor = null;
        try {
            cursor = sqLiteDatabase.rawQuery(sql, null);
            if (cursor.moveToFirst()) {
                long id = cursor.getLong(cursor.getColumnIndex(SharedData.ID));

                String mKey = cursor.getString(cursor
                        .getColumnIndex(SharedData.KEY));

                String mStr = cursor.getString(cursor
                        .getColumnIndex(SharedData.M_STR));

                int boolCode = cursor.getInt(cursor
                        .getColumnIndex(SharedData.M_BOOLEAN));

                int mInt = cursor.getInt(cursor
                        .getColumnIndex(SharedData.M_INT));

                String dataStr = cursor.getString(cursor
                        .getColumnIndex(SharedData.M_DATE));
                Date mData = null;
                int trueCode = TRUE_CODE;

                boolean mBoolean = boolCode == trueCode;

                long mLong = cursor.getLong(cursor.getColumnIndex(SharedData.M_LONG));

                float mFloat = cursor.getFloat(cursor.getColumnIndex(SharedData.M_FLOAT));

                int mTypeCode = cursor.getInt(cursor.getColumnIndex(SharedData.DATA_TYPE));

                DataType dataType = DataType.getDataTypeByValue(mTypeCode);

                try {
                    if (!TextUtils.isEmpty(dataStr))
                        mData = sDateFormat.parse(dataStr);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                sharedData = new SharedData();
                sharedData.setId(id);
                sharedData.setKey(mKey);
                sharedData.setmStr(mStr);
                sharedData.setmBoolean(mBoolean);
                sharedData.setmInt(mInt);
                sharedData.setmDate(mData);
                sharedData.setmLong(mLong);
                sharedData.setmFloat(mFloat);
                sharedData.setDataType(dataType);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != cursor && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return sharedData;
    }

    /**
     * 保存数据
     *
     * @param sharedData
     * @return
     */
    private long saveData(SharedData sharedData) {
        long id = -1;

        if (sharedData == null || TextUtils.isEmpty(sharedData.getKey()) || sharedData.getDataType() == null) {
            return id;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(SharedData.KEY, sharedData.getKey());
        contentValues.put(SharedData.M_STR, sharedData.getmStr());
        contentValues.put(SharedData.M_BOOLEAN, sharedData.ismBoolean());
        contentValues.put(SharedData.M_INT, sharedData.getmInt());
        contentValues.put(SharedData.M_LONG, sharedData.getmLong());
        contentValues.put(SharedData.M_FLOAT, sharedData.getmFloat());
        contentValues.put(SharedData.DATA_TYPE, sharedData.getDataType().getValue());

        String mTime = null;

        if (sharedData.getmDate() != null) {
            mTime = sDateFormat.format(sharedData.getmDate());
        }

        contentValues.put(SharedData.M_DATE, mTime);

        try {
            id = sqLiteDatabase.insert(SharedData.TABLE_NAME, null, contentValues);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return id;
    }


    /**
     * 根据ID更新数据
     *
     * @param sharedData
     * @return
     */
    private int updateDataById(SharedData sharedData) {
        int rows = 0;

        if (sharedData == null || TextUtils.isEmpty(sharedData.getKey())
                || sharedData.getId() <= 0 || sharedData.getDataType() == null) {
            return -1;
        }

        // 更新保存
        ContentValues contentValues = new ContentValues();
        contentValues.put(SharedData.KEY, sharedData.getKey());
        contentValues.put(SharedData.M_STR, sharedData.getmStr());
        contentValues.put(SharedData.M_BOOLEAN, sharedData.ismBoolean());
        contentValues.put(SharedData.M_INT, sharedData.getmInt());
        contentValues.put(SharedData.M_LONG, sharedData.getmLong());
        contentValues.put(SharedData.M_FLOAT, sharedData.getmFloat());
        contentValues.put(SharedData.DATA_TYPE, sharedData.getDataType().getValue());

        String mTime = null;

        if (sharedData.getmDate() != null) {
            mTime = sDateFormat.format(sharedData.getmDate());
        }

        contentValues.put(SharedData.M_DATE, mTime);

        try {
            rows = sqLiteDatabase.update(SharedData.TABLE_NAME,
                    contentValues, SharedData.ID + "= ? ", new String[]
                            {
                                    String.valueOf(sharedData.getId())
                            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rows;
    }

    /**
     * 是否存在key值
     *
     * @param key
     * @return
     */
    public boolean contains(String key) {
        if (TextUtils.isEmpty(key)) {
            return false;
        }

        String sql = "select count(*) from " + SharedData.TABLE_NAME
                + " where " + SharedData.KEY + "=\""
                + key + "\"";

        Cursor cursor = null;
        try {
            cursor = sqLiteDatabase.rawQuery(sql, null);
            cursor.moveToFirst();
            long count = cursor.getLong(0);
            if (count > 0) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != cursor && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return false;
    }


    /**
     * 是否存在数据，存在则返回相应ID
     *
     * @param sharedData
     * @return
     */
    private long hasData(SharedData sharedData) {
        if (sharedData == null || TextUtils.isEmpty(sharedData.getKey())) {
            return -1;
        }

        String sql = "select * from " + SharedData.TABLE_NAME
                + " where " + SharedData.KEY + "=\""
                + sharedData.getKey() + "\"";

        Cursor cursor = null;
        try {
            cursor = sqLiteDatabase.rawQuery(sql, null);

            if (cursor.moveToFirst()) {
                long id = cursor.getLong(cursor.getColumnIndex(SharedData.ID));

                if (id > 0) {
                    return id;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != cursor && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return -1;
    }


    public boolean isClosed() {
        return isClosed;
    }

    public void close() {
        if (!isClosed) {
            try {
                if (null != localSqLiteHelper) {
                    localSqLiteHelper.close();
                }

                if (null != sqLiteDatabase) {
                    sqLiteDatabase.close();
                }
                isClosed = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 查询记录的总数
     */
    public long getCount() {
        String sql = "select count(*) from " + SharedData.TABLE_NAME;
        Cursor cursor = null;
        long length = 0;
        try {
            cursor = sqLiteDatabase.rawQuery(sql, null);
            cursor.moveToFirst();
            length = cursor.getLong(0);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != cursor && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return length;
    }

    /**
     * 删除表中所有数据
     */
    public boolean clearAll() {
        String sql = "delete from " + SharedData.TABLE_NAME;

        Cursor cursor = null;
        Boolean hasData = false;

        try {
            sqLiteDatabase.execSQL(sql);
            cursor = sqLiteDatabase.query(SharedData.TABLE_NAME, null, null, null,
                    null, null, null);
            hasData = cursor.moveToFirst();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != cursor && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return !hasData;
    }
}
