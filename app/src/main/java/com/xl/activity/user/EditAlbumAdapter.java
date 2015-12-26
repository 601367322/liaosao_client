package com.xl.activity.user;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xl.activity.R;
import com.xl.activity.base.BaseAdapter;
import com.xl.bean.ImageBean;
import com.xl.util.StaticFactory;
import com.xl.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import butterknife.Bind;

/**
 * Created by Shen on 2015/12/13.
 */
public class EditAlbumAdapter extends BaseAdapter<ImageBean> {

    private AtomicBoolean editing = new AtomicBoolean(false);

    public void setEditing(AtomicBoolean editing) {
        this.editing = editing;
        notifyDataSetChanged();
    }

    public EditAlbumAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseHolder getHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public int getConvertView(int position) {
        return R.layout.edit_album_item;
    }

    class ViewHolder extends BaseHolder {

        @Bind(R.id.img)
        ImageView img;
        @Bind(R.id.photo_select)
        CheckBox checkBox;

        public ViewHolder(View view) {
            super(view);
        }

        @Override
        public void bind(ImageBean bean, int position) {
            String path = bean.getPath();
            if (!path.startsWith("http://")) {
                path = "file://" + path;
            }else{
                path += StaticFactory._320x320;
            }
            ImageLoader.getInstance().displayImage(path, img, Utils.options_default_logo);
            if (editing.get()) {
                checkBox.setVisibility(View.VISIBLE);
            } else {
                checkBox.setVisibility(View.GONE);
            }
            if (bean.isChecked()) {
                checkBox.setChecked(true);
            } else {
                checkBox.setChecked(false);
            }
        }
    }

    public void removeByIds(List<Integer> deleteList) {
        List<ImageBean> tmp = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if(deleteList.contains(list.get(i).getId())){
                tmp.add(list.get(i));
            }
        }
        removeAll(tmp);
        notifyDataSetChanged();
    }
}
