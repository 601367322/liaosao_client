package com.xl.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.xl.activity.R;
import com.xl.util.Utils;

public class PhotoGridItem extends RelativeLayout implements Checkable {

    private Context mContext;
    private boolean mCheck;
    private ImageView mImageView;
    private RoundProgressBar progress;

    public RoundProgressBar getProgress() {
        return progress;
    }

    public ImageView getmImageView() {
        return mImageView;
    }

    private ImageView mSelect;

    public PhotoGridItem(Context context) {
        this(context, null, 0);
    }

    public PhotoGridItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PhotoGridItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        LayoutInflater.from(mContext).inflate(R.layout.photoalbum_gridview_item, this);
        mImageView = (ImageView) findViewById(R.id.photo_img_view);

        int width = (int) ((float) (context.getResources().getDisplayMetrics().widthPixels - Utils.dip2px(context, 16)) / 3.0);
        LayoutParams lp = new LayoutParams(width, width);
        mImageView.setLayoutParams(lp);

        mSelect = (ImageView) findViewById(R.id.photo_select);
        progress = (RoundProgressBar) findViewById(R.id.loadProgressBar);
    }

    @Override
    public void setChecked(boolean checked) {
        mCheck = checked;
        mSelect.setImageDrawable(checked ? getResources().getDrawable(R.drawable.invite_checked) : getResources().getDrawable(R.drawable.invite_unchecked));
    }

    @Override
    public boolean isChecked() {
        return mCheck;
    }

    @Override
    public void toggle() {
        setChecked(!mCheck);
    }

    public void setImgResID(int id) {
        if (mImageView != null) {
            mImageView.setBackgroundResource(id);
        }
    }

    public void SetBitmap(Bitmap bit) {
        if (mImageView != null) {
            mImageView.setImageBitmap(bit);
        }
    }

    public void hideSelect() {
        mSelect.setVisibility(View.GONE);
    }

    public void showSelect() {
        mSelect.setVisibility(View.VISIBLE);
    }
}