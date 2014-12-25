package com.xl.db;

import java.util.List;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.xl.bean.MessageBean;
import com.xl.bean.SharedBean;
import com.xl.util.LogUtil;

public class DBHelper extends OrmLiteSqliteOpenHelper {
	private static final String TAG = "DBHelper";
	private static final String DATABASE_NAME = "XL.db";
	private static final int DATABASE_VERSION = 1;
	
	private Dao<SharedBean,Integer> sharedDao;

	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
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
		} catch (Exception e) {
			LogUtil.e(TAG, e.toString());
			e.printStackTrace();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase,
			ConnectionSource connectionSource, int oldVersion, int newVersion) {
		try {
		} catch (SQLException e) {
			LogUtil.e(TAG, e.toString());
			e.printStackTrace();
		}
	}

	@Override
	public void close() {
		super.close();
		sharedDao=null;
	}
	
	public SharedBean getShared(){
		try {
			if(sharedDao==null){
				sharedDao=getDao(SharedBean.class);
			}
			List<SharedBean> list=sharedDao.queryForAll();
			if(list.size()>0){
				return list.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new SharedBean();
	}
}
