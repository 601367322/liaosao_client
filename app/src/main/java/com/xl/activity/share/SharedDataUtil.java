package com.xl.activity.share;


import android.content.Context;
import android.text.TextUtils;

import java.util.Date;
import java.util.HashMap;

/**
 * <b>数据操作工具类</b></br>
 *
 * @author Jony
 * @13-5-24
 * @下午5:59
 */
public class SharedDataUtil {
    /**
     * 数据库操作助手
     */
    private SharedDataSqLiteHelper sharedDataSqLiteHelper;

    private static SharedDataUtil sharedDataUtil;

    private SharedDataEditor sharedDataEditor;

    private SharedDataUtil(Context context) {
        if (sharedDataSqLiteHelper == null || sharedDataSqLiteHelper.isClosed()) {
            this.sharedDataSqLiteHelper = new SharedDataSqLiteHelper(context);
        }
    }

    public static synchronized SharedDataUtil getInstance(Context context) {
        if (context == null) {
            return null;
        }

        if (sharedDataUtil == null) {
            sharedDataUtil = new SharedDataUtil(context);
        }

        return sharedDataUtil;
    }

    /**
     * 根据key获取String类型数据
     *
     * @param key
     * @param defaultValue 默认值，无值时返回传入的默认值
     * @return
     */
    public String getString(String key, String defaultValue) {
        if (TextUtils.isEmpty(key))
            return defaultValue;

        SharedData sharedData = this.sharedDataSqLiteHelper.getGlobalDataByKey(key);

        if (sharedData == null || sharedData.getDataType() != DataType.STRING) {
            return defaultValue;
        }

        return sharedData.getmStr();
    }

    /**
     * 根据key获取boolean类型数据
     *
     * @param key
     * @param defaultValue 默认值，无值时返回传入的默认值
     * @return
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        if (TextUtils.isEmpty(key))
            return defaultValue;

        SharedData sharedData = this.sharedDataSqLiteHelper.getGlobalDataByKey(key);

        if (sharedData == null || sharedData.getDataType() != DataType.BOOLEAN) {
            return defaultValue;
        }

        return sharedData.ismBoolean();
    }

    /**
     * 根据key获取int类型数据
     *
     * @param key
     * @param defaultValue 默认值，无值时返回传入的默认值
     * @return
     */
    public int getInt(String key, int defaultValue) {
        if (TextUtils.isEmpty(key))
            return defaultValue;

        SharedData sharedData = this.sharedDataSqLiteHelper.getGlobalDataByKey(key);

        if (sharedData == null || sharedData.getDataType() != DataType.INT) {
            return defaultValue;
        }

        return sharedData.getmInt();
    }

    /**
     * 根据key获取Date类型数据
     *
     * @param key
     * @param defaultValue 默认值，无值时返回传入的默认值
     * @return
     */
    public Date getDate(String key, Date defaultValue) {
        if (TextUtils.isEmpty(key))
            return defaultValue;

        SharedData sharedData = this.sharedDataSqLiteHelper.getGlobalDataByKey(key);

        if (sharedData == null || sharedData.getDataType() != DataType.DATA) {
            return defaultValue;
        }

        return sharedData.getmDate();
    }

    /**
     * 根据key获取long类型数据
     *
     * @param key
     * @param defaultValue 默认值，无值时返回传入的默认值
     * @return
     */
    public long getLong(String key, long defaultValue) {
        if (TextUtils.isEmpty(key))
            return defaultValue;

        SharedData sharedData = this.sharedDataSqLiteHelper.getGlobalDataByKey(key);

        if (sharedData == null || sharedData.getDataType() != DataType.LONG) {
            return defaultValue;
        }

        return sharedData.getmLong();
    }

    /**
     * 根据key获取float类型数据
     *
     * @param key
     * @param defaultValue 默认值，无值时返回传入的默认值
     * @return
     */
    public float getFloat(String key, float defaultValue) {
        if (TextUtils.isEmpty(key))
            return defaultValue;

        SharedData sharedData = this.sharedDataSqLiteHelper.getGlobalDataByKey(key);

        if (sharedData == null || sharedData.getDataType() != DataType.FLOAT) {
            return defaultValue;
        }

        return sharedData.getmFloat();
    }


    /**
     * 是否存在该key
     *
     * @param key
     * @return
     */
    public boolean contains(String key) {
        if (TextUtils.isEmpty(key))
            return false;

        return this.sharedDataSqLiteHelper.contains(key);
    }

    /**
     * 删除该key对应的数据
     *
     * @param key
     * @return
     */
    public boolean remove(String key) {
        if (TextUtils.isEmpty(key))
            return false;

        return this.sharedDataSqLiteHelper.remove(key);
    }

    /**
     * 清除所有数据
     *
     * @return
     */
    public boolean clear() {
        return this.sharedDataSqLiteHelper.clearAll();
    }


    /**
     * 获取编辑器
     *
     * @return
     */
    public SharedDataEditor getSharedDataEditor() {
        if (sharedDataEditor == null) {
            sharedDataEditor = new SharedDataEditor();
        }

        sharedDataEditor.clearDatas();

        return sharedDataEditor;
    }

    /**
     * <br>数据编辑器<br/>
     * <br>最后使用commit()提交数据保存<br/>
     */
    public class SharedDataEditor {
        private HashMap<String, SharedData> sharedDataHashMap;

        public SharedDataEditor() {
            if (sharedDataHashMap == null) {
                sharedDataHashMap = new HashMap<String, SharedData>();
            } else {
                sharedDataHashMap.clear();
            }
        }

        /**
         * 放入String类型数据
         *
         * @param key
         * @param value
         * @return
         */
        public SharedDataEditor putString(String key, String value) {
            if (TextUtils.isEmpty(key)) {
                return this;
            }

            SharedData sharedData = getDefaultData();
            sharedData.setKey(key);
            sharedData.setmStr(value);
            sharedData.setDataType(DataType.STRING);

            sharedDataHashMap.put(key, sharedData);

            return this;
        }

        /**
         * 放入boolean类型数据
         *
         * @param key
         * @param value
         * @return
         */
        public SharedDataEditor putBoolean(String key, boolean value) {
            if (TextUtils.isEmpty(key))
                return this;

            SharedData sharedData = getDefaultData();
            sharedData.setKey(key);
            sharedData.setmBoolean(value);
            sharedData.setDataType(DataType.BOOLEAN);
            sharedDataHashMap.put(key, sharedData);

            return this;
        }

        /**
         * 放入int类型数据
         *
         * @param key
         * @param value
         * @return
         */
        public SharedDataEditor putInt(String key, int value) {
            if (TextUtils.isEmpty(key))
                return this;

            SharedData sharedData = getDefaultData();
            sharedData.setKey(key);
            sharedData.setmInt(value);
            sharedData.setDataType(DataType.INT);

            sharedDataHashMap.put(key, sharedData);

            return this;
        }

        /**
         * 放入Date类型数据
         *
         * @param key
         * @param date
         * @return
         */
        public SharedDataEditor putDate(String key, Date date) {
            if (TextUtils.isEmpty(key) || date == null)
                return this;

            SharedData sharedData = getDefaultData();
            sharedData.setKey(key);
            sharedData.setmDate(date);
            sharedData.setDataType(DataType.DATA);

            sharedDataHashMap.put(key, sharedData);

            return this;
        }

        /**
         * 放入Long类型数据
         *
         * @param key
         * @param value
         * @return
         */
        public SharedDataEditor putLong(String key, long value) {
            if (TextUtils.isEmpty(key))
                return this;

            SharedData sharedData = getDefaultData();
            sharedData.setKey(key);
            sharedData.setmLong(value);
            sharedData.setDataType(DataType.LONG);
            sharedDataHashMap.put(key, sharedData);

            return this;
        }

        /**
         * 放入float类型数据
         *
         * @param key
         * @param value
         * @return
         */
        public SharedDataEditor putFloat(String key, float value) {
            if (TextUtils.isEmpty(key))
                return this;

            SharedData sharedData = getDefaultData();
            sharedData.setKey(key);
            sharedData.setmFloat(value);
            sharedData.setDataType(DataType.FLOAT);
            sharedDataHashMap.put(key, sharedData);

            return this;
        }

        /**
         * 保存数据
         *
         * @return
         */
        public synchronized boolean commit() {
            boolean isSuccess = false;

            SharedDataSqLiteHelper sharedDataSqLiteHelper1 = sharedDataSqLiteHelper;
            HashMap<String, SharedData> copyOfsharedDataHashMap = (HashMap<String, SharedData>) sharedDataHashMap.clone();
            for (SharedData sharedData : copyOfsharedDataHashMap.values()) {
                if (sharedData != null) {
                    long id = sharedDataSqLiteHelper1.putData(sharedData);

                    isSuccess = id > 0;
                }
            }

            sharedDataHashMap.clear();

            return isSuccess;
        }

        /**
         * 清除数据
         */
        public void clearDatas() {
            if (sharedDataHashMap != null)
                sharedDataHashMap.clear();
        }
    }


    /**
     * 获取一个默认数据对象
     *
     * @return
     */
    private SharedData getDefaultData() {
        SharedData sharedData = new SharedData();
        sharedData.setmStr(null);
        sharedData.setmBoolean(false);
        sharedData.setmDate(null);
        sharedData.setmInt(-1);
        sharedData.setmLong(-1);
        sharedData.setmFloat(-1f);
        sharedData.setDataType(null);
        return sharedData;
    }
}
