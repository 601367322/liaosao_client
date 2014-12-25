package com.xl.activity.chat;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.xl.activity.R;
import com.xl.activity.base.BaseAdapter;
import com.xl.bean.MessageBean;

public class ChatAdapters extends BaseAdapter {

	public ChatAdapters(Context context, List list) {
		super(context, list);
	}

	@Override
	public int getItemViewType(int position) {
		MessageBean mb = (MessageBean) getList().get(position);
		if (mb.getToId().equals(ac.deviceId)) {
			return 0;
		} else {
			return 1;
		}
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public View getView(int position, View view, ViewGroup viewGroup) {
		ViewHolder holder;
		if (view == null) {
			if (getItemViewType(position) == 0)
				view = View.inflate(context, R.layout.chat_left_layout, null);
			else {
				view = View.inflate(context, R.layout.chat_right_layout, null);
			}
			holder = new ViewHolder(view);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		MessageBean mb = (MessageBean) getItem(position);
		holder.content.setText(mb.getContent().toString());
		return view;
	}

	static class ViewHolder {

		@InjectView(R.id.content)
		TextView content;

		public ViewHolder(View view) {
			ButterKnife.inject(this, view);
		}
	}

}
