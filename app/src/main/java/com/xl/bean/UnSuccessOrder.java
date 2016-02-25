package com.xl.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by Shen on 2016/2/25.
 */
@DatabaseTable(tableName = "UnSuccessOrder")
public class UnSuccessOrder implements Serializable {

    @DatabaseField(generatedId = true)
    public int id;
    @DatabaseField
    public String orderId;
}
