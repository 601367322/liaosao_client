package com.xl.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.xl.bean.BlackUser;
import com.xl.bean.ChatListBean;
import com.xl.bean.MessageBean;
import com.xl.bean.SharedBean;
import com.xl.bean.UserTable;
import com.xl.bean.UserTable_6;
import com.xl.util.LogUtil;

import java.sql.SQLException;
import java.util.List;

public class DBHelper extends OrmLiteSqliteOpenHelper {
    private static final String TAG = "DBHelper";
    private static final String DATABASE_NAME = "XL.db";
    private static final int DATABASE_VERSION = 7;

    private Dao<SharedBean, Integer> sharedDao;

    public static UserTableDao userDao;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void init(Context context){
        userDao = UserTableDao.getInstance(context);
    }

    /**
     * @param context
     * @param databaseName
     * @param factory
     * @param databaseVersion
     */
    public DBHelper(Context context, String databaseName,
                    CursorFactory factory, int databaseVersion) {
        super(context, databaseName, factory, databaseVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase,
                         ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, SharedBean.class);
            TableUtils.createTable(connectionSource, MessageBean.class);
            TableUtils.createTable(connectionSource, ChatListBean.class);
            TableUtils.createTable(connectionSource, BlackUser.class);
            TableUtils.createTable(connectionSource, UserTable_6.class);
        } catch (Exception e) {
            LogUtil.e(TAG, e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase,
                          ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            switch (oldVersion) {
                case 4:
                    updateDB_5();
                case 5:
                    updateDB_6(connectionSource);
                case 6:
                    updateDB_7(connectionSource);
                    break;
            }
        } catch (Exception e) {
            LogUtil.e(TAG, e.toString());
            e.printStackTrace();
        }
    }

    public void updateDB_5() {
        try {
            TableUtils.createTable(connectionSource, UserTable.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateDB_6(ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, UserTable_6.class);
            RuntimeExceptionDao<UserTable, Integer> dao = getRuntimeExceptionDao(UserTable.class);
            List<UserTable> list = dao.queryForAll();
            RuntimeExceptionDao<UserTable_6, Integer> daoNew = getRuntimeExceptionDao(UserTable_6.class);
            for (UserTable ut : list) {
                UserTable_6 u6 = new UserTable_6();
                u6.setId(ut.getId());
                u6.setDetail(ut.getDetail());
                u6.setDeviceId(ut.getDeviceId());
                daoNew.create(u6);
            }
            TableUtils.dropTable(connectionSource, UserTable.class, true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void updateDB_7(ConnectionSource connectionSource){
        try {
            TableUtils.clearTable(connectionSource,UserTable_6.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        super.close();
        sharedDao = null;
    }

    public SharedBean getShared() {
        try {
            if (sharedDao == null) {
                sharedDao = getDao(SharedBean.class);
            }
            List<SharedBean> list = sharedDao.queryForAll();
            if (list.size() > 0) {
                return list.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new SharedBean();
    }

    public static <D extends RuntimeExceptionDao<T, ?>, T> D getDao_(Context context, Class<T> clazz) {
        return OpenHelperManager.getHelper(context, DBHelper.class).getRuntimeExceptionDao(clazz);
    }

    public static RuntimeExceptionDao getChatListDao(Context context) {
        return getDao_(context, ChatListBean.class);
    }

    public static RuntimeExceptionDao getChatDao(Context context) {
        return getDao_(context, MessageBean.class);
    }

    public static void clearTable(Context context, Class clazz) {
        try {
            ConnectionSource source = OpenHelperManager.getHelper(context, DBHelper.class).getConnectionSource();
            TableUtils.clearTable(source, clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static RuntimeExceptionDao getUserTableDao(Context context) {
        return getDao_(context, UserTable_6.class);
    }
}
