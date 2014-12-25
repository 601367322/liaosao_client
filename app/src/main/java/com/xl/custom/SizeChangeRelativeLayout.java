package com.xl.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class SizeChangeRelativeLayout extends RelativeLayout {

	private OnSizeChangedListener listener;

	public boolean isOpen=false;
	
	public SizeChangeRelativeLayout(Context context) {
		super(context);
	}
	public SizeChangeRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
	}

	int _oldh=-1;
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {

		super.onSizeChanged(w, h, oldw, oldh);
		if (listener != null&&oldh!=0) {
			
			if (_oldh == -1) {
				_oldh = oldh;
			}
			if (h >= _oldh) {
				listener.close();
				isOpen=false;
			} else if (h < _oldh) {
				listener.open();
				isOpen=true;
			}
		}
	}

	public void setOnSizeChangedListener(OnSizeChangedListener listener) {
		this.listener = listener;
	}

	public interface OnSizeChangedListener {
		public void open();
		public void close();
	}
}