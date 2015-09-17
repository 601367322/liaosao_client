package com.xl.activity.chat.adapter;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.RelativeLayout;

import com.xl.activity.R;
import com.xl.bean.MessageBean;
import com.xl.custom.MyImageView;
import com.xl.util.GifDrawableCache;
import com.xl.util.Utils;

import butterknife.Bind;
import pl.droidsonroids.gif.GifDrawable;

/**
 * Created by Shen on 2015/9/13.
 */
public class FaceViewHolder extends BaseHolder{

    @Nullable
    @Bind(R.id.face)
    MyImageView face;

    public FaceViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    protected void bind(MessageBean bean) {
        super.bind(bean);
        GifDrawable drawable = GifDrawableCache.getInstance().getDrawable((long) context.getResources().getIdentifier(bean.getContent(), "drawable", context.getPackageName()), context);
        int img_width = drawable.getMinimumWidth();
        int max_widht = Utils.dip2px(context, 80);
        float scale1 = (float) img_width / (float) max_widht;
        int new_height = (int) ((float) drawable.getMinimumHeight() / scale1);
        RelativeLayout.LayoutParams layoutParams1 = (RelativeLayout.LayoutParams) face.getLayoutParams();
        layoutParams1.width = max_widht;
        layoutParams1.height = new_height;
        face.setLayoutParams(layoutParams1);
        face.setImageGifDrawable(drawable);
        face.setStart(true);
    }
}
