package com.xl.activity.album;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xl.activity.R;
import com.xl.activity.base.BaseAdapter;
import com.xl.bean.PhotoItem;
import com.xl.util.StaticUtil;

import butterknife.BindView;

public class PhotoAlbumGridAdappter extends BaseAdapter<PhotoItem> {

    public PhotoAlbumGridAdappter(Context context) {
        super(context);
    }

    @Override
    public BaseHolder getHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public int getConvertView(int position) {
        return R.layout.photoalbum_gridview_item;
    }

    class ViewHolder extends BaseHolder{

        @BindView(R.id.photo_img_view)
        ImageView img;
        @BindView(R.id.photo_select)
        CheckBox select;

        public ViewHolder(View view) {
            super(view);
        }

        @Override
        public void bind(PhotoItem bean, int position) {
            ImageLoader.getInstance().displayImage(StaticUtil.FILE + bean.getPath(), img, options_defalut);
            boolean flag = bean.isSelect();
            select.setChecked(flag);
        }
    }

    public static final DisplayImageOptions options_defalut = new DisplayImageOptions.Builder()
            .considerExifParams(true).cacheInMemory(true)
            .cacheOnDisk(false).build();

}