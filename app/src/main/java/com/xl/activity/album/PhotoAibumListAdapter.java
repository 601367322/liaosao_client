package com.xl.activity.album;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xl.activity.R;
import com.xl.activity.base.BaseAdapter;
import com.xl.bean.PhotoAibum;
import com.xl.util.StaticUtil;

import butterknife.BindView;


public class PhotoAibumListAdapter extends BaseAdapter<PhotoAibum> {

    public PhotoAibumListAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseHolder getHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public int getConvertView(int position) {
        return R.layout.photoalbum_listview_item;
    }

    public static final DisplayImageOptions options_defalut = new DisplayImageOptions.Builder()
            .considerExifParams(true).cacheInMemory(true)
            .cacheOnDisk(false).build();


    class ViewHolder extends BaseHolder{
        @BindView(R.id.photoalbum_item_image)
        ImageView iv;
        @BindView(R.id.photoalbum_item_name)
        TextView tv;

        public ViewHolder(View view) {
            super(view);
        }

        @Override
        public void bind(PhotoAibum bean, int position) {
            ImageLoader.getInstance().displayImage(StaticUtil.FILE + bean.getPath(), iv, options_defalut);
            tv.setText(bean.getName() + " ( " + bean.getCount() + " )");
        }
    }

}