package com.xl.activity.chat;

import android.content.Context;
import android.view.View;

import com.xl.activity.R;
import com.xl.activity.base.BaseAdapter;
import com.xl.custom.MyImageView;
import com.xl.util.GifDrawableCache;

import butterknife.Bind;


/**
 * Created by Administrator on 2015/2/21.
 */
public class FaceAdapter extends BaseAdapter<Integer> {

    public FaceAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseHolder getHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public int getConvertView(int position) {
        return R.layout.face_item;
    }

    class ViewHolder extends BaseHolder{

        @Bind(R.id.face_img)
        MyImageView face_img;

        public ViewHolder(View view) {
            super(view);
        }

        @Override
        public void bind(Integer bean, int position) {
            face_img.setImageGifDrawable(GifDrawableCache.getInstance().getDrawable(Long.valueOf(bean), context));
        }

    }
}
