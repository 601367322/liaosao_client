package com.xl.bean;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.xl.db.DBHelper;

import java.io.Serializable;
import java.sql.SQLException;

@DatabaseTable(tableName = "shared_table")
public class SharedBean implements Serializable {

	@DatabaseField(generatedId = true)
	private int id;

	public SharedBean() {
		super();
	}

	@DatabaseField(dataType = DataType.SERIALIZABLE)
	private Bean bean;

	public Bean getBean() {
		if (bean == null) {
			bean = new Bean();
		}
		return bean;
	}

	public void setBean(Bean bean) {
		this.bean = bean;
	}

	public class Bean implements Serializable {

		private int number = 0;

		private String deviceId = "";

		public int getNumber() {
			return number;
		}

		public Bean setNumber(int number) {
			this.number = number;
			return this;
		}

		public String getDeviceId() {
			return deviceId;
		}

		public Bean setDeviceId(String deviceId) {
			this.deviceId = deviceId;
			return this;
		}

		public void commit(Context context) {
			try {
				OpenHelperManager.getHelper(context, DBHelper.class)
						.getDao(SharedBean.class)
						.createOrUpdate(SharedBean.this);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

}
