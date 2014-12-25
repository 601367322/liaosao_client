package com.xl.activity.base;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.xl.application.AppClass_;

public abstract class BaseAdapter extends android.widget.BaseAdapter {

	private List<Object> list;
	protected Context context;
	protected AppClass_ ac;

	public BaseAdapter(Context context, List<Object> list) {
		super();
		this.list = list;
		this.context = context;
		this.ac=(AppClass_) context.getApplicationContext();
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		if(arg0<0||arg0>=list.size()){
			return null;
		}
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public abstract View getView(int position, View view, ViewGroup viewGroup);

	
	public List<Object> getList() {
		return list;
	}

	public void setList(List<Object> list){
		this.list=list;
	}
}
