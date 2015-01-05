package com.xl.activity.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xl.activity.R;
import com.xl.activity.base.BaseAdapterListView;
import com.xl.bean.MessageBean;
import com.xl.custom.MyImageView;
import com.xl.util.Utils;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;
import pl.droidsonroids.gif.GifDrawable;

public class ChatAdapters extends BaseAdapterListView<MessageBean> {

	public ChatAdapters(Context context, List list) {
		super(list,context);
	}

	@Override
	public int getItemViewType(int position) {
		MessageBean mb = (MessageBean) getList().get(position);
		if (mb.getToId().equals(ac.deviceId)) {
            switch (mb.getMsgType()){
                case 0:
                    return 0;
                case 1:
                    return 1;
            }
		} else {
            switch (mb.getMsgType()){
                case 0:
                    return 10;
                case 1:
                    return 11;
            }
		}
        return -1;
	}

	@Override
	public int getViewTypeCount() {
		return 20;
	}

	@Override
	public View getView(int position, View view, ViewGroup viewGroup) {
		ViewHolder holder;
		if (view == null) {
            switch (getItemViewType(position)){
                case 0:
                    view=LayoutInflater.from(context).inflate(R.layout.chat_left_text_layout,viewGroup,false);
                    break;
                case 1:
                    view=LayoutInflater.from(context).inflate(R.layout.chat_left_voice_layout,viewGroup,false);
                    break;
                case 10:
                    view=LayoutInflater.from(context).inflate(R.layout.chat_right_text_layout,viewGroup,false);
                    break;
                case 11:
                    view=LayoutInflater.from(context).inflate(R.layout.chat_right_voice_layout,viewGroup,false);
                    break;
            }
			holder = new ViewHolder(view);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		MessageBean mb = (MessageBean) getItem(position);
        switch (getItemViewType(position)){
            case 0:
            case 10:
                holder.content.setText(mb.getContent().toString());
                break;
            case 1:
            case 11:
                float scale = (float)mb.getVoiceTime()/60f;
                float width = 150f * scale;
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.voice.getLayoutParams();
                layoutParams.width= Utils.dip2px(context,60 + (int) width);
                holder.voice.setLayoutParams(layoutParams);

                try {
                    GifDrawable temp1=null;
                    if(holder.voice_img.getDrawable()==null) {
                        switch (getItemViewType(position)){
                            case 1:
                                temp1 = new GifDrawable(context.getResources(), R.drawable.chat_left_animation);
                                break;
                            case 11:
                                temp1 = new GifDrawable(context.getResources(), R.drawable.chat_right_animation);
                                break;
                        }
                        holder.voice_img.setImageGifDrawable(temp1);
                    }else{
                        temp1=holder.voice_img.getGifDrawable();
                    }
                    if(mb.isPlaying()){
                        temp1.start();
                    }else{
                        temp1.pause();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }

        return view;
	}

	static class ViewHolder {

        @Optional
		@InjectView(R.id.content)
		TextView content;

        @Optional
        @InjectView(R.id.voice_img)
        MyImageView voice_img;

        @Optional
        @InjectView(R.id.voice)
        View voice;

        @Optional
        @InjectView(R.id.progress)
        View progress;

		public ViewHolder(View view) {
			ButterKnife.inject(this, view);
		}
	}

}
