package com.xl.activity.share;

import java.io.Serializable;
import java.util.Date;

/**
 * <b>数据存储对象</b></br>
 *
 * @author Jony
 * @13-5-24
 * @下午4:28
 */
public class SharedData implements Serializable, Cloneable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private long id;

    /**
     * <br>数据对应唯一Key值<br/>
     * <br>如果数据库已存在Key,则会覆盖之前的数据<br/>
     */
    private String key;

    /**
     * 默认String类型值
     */
    private String mStr;

    /**
     * 默认boolean类型值 code值: 0(true) ,1(false)
     */
    private boolean mBoolean;

    /**
     * 默认int类型值
     */
    private int mInt;

    /**
     * 默认Date类型值
     */
    private Date mDate;

    /**
     * 默认long类型数据
     */
    private long mLong;

    /**
     * 默认float类型数据
     */
    private float mFloat;

    /**
     * 当前数据类型
     */
    private DataType dataType;

    /**
     * *****************************
     * 预留字段 end
     * *****************************
     */

    /**
     * 数据库对应字段名称
     */
    public static final String ID = "id";

    /**
     * 唯一key对应数据库字段名称
     */
    public static final String KEY = "key";

    /**
     * 默认String类型数据对应数据库字段名称
     */
    public static final String M_STR = "m_str";

    /**
     * 默认boolean对应数据库字段名称
     */
    public static final String M_BOOLEAN = "m_boolean";

    /**
     * 默认int对应数据库字段名称
     */
    public static final String M_INT = "m_int";

    /**
     * 默认Date对应数据库字段名称
     */
    public static final String M_DATE = "m_date";

    /**
     * 默认long对应数据库字段名称
     */
    public static final String M_LONG = "m_long";

    /**
     * 默认float对应数据库字段名称
     */
    public static final String M_FLOAT = "m_float";

    /**
     * 数据类型数据库字段名称
     */
    public static final String DATA_TYPE = "data_type";

    /**
     * 表名
     */
    public static final String TABLE_NAME = "t_global_data";

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getmStr() {
        return mStr;
    }

    public void setmStr(String mStr) {
        this.mStr = mStr;
    }

    public boolean ismBoolean() {
        return mBoolean;
    }

    public void setmBoolean(boolean mBoolean) {
        this.mBoolean = mBoolean;
    }

    public int getmInt() {
        return mInt;
    }

    public void setmInt(int mInt) {
        this.mInt = mInt;
    }

    public Date getmDate() {
        return mDate;
    }

    public void setmDate(Date mDate) {
        this.mDate = mDate;
    }

    public long getmLong() {
        return mLong;
    }

    public void setmLong(long mLong) {
        this.mLong = mLong;
    }

    public float getmFloat() {
        return mFloat;
    }

    public void setmFloat(float mFloat) {
        this.mFloat = mFloat;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }


    @Override
    public String toString() {
        return "SharedData{" +
                "id=" + id +
                ", key='" + key + '\'' +
                ", mStr='" + mStr + '\'' +
                ", mBoolean=" + mBoolean +
                ", mInt=" + mInt +
                ", mDate=" + mDate +
                ", mLong=" + mLong +
                ", mFloat=" + mFloat +
                ", dataType=" + dataType +
                '}';
    }
}