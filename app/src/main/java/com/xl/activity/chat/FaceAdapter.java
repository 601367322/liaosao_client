package com.xl.activity.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xl.activity.R;
import com.xl.activity.base.BaseAdapterListView;
import com.xl.custom.MyImageView;
import com.xl.util.GifDrawableCache;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.FindView;


/**
 * Created by Administrator on 2015/2/21.
 */
public class FaceAdapter extends BaseAdapterListView<Integer> {

    public FaceAdapter(Context context, List list) {
        super(list, context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.face_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder= (ViewHolder) convertView.getTag();
        }
        Integer id = getItem(position);
        holder.face_img.setImageGifDrawable(GifDrawableCache.getInstance().getDrawable(Long.valueOf(id), context));
        return convertView;
    }

    class ViewHolder {

        @FindView(R.id.face_img)
        MyImageView face_img;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
