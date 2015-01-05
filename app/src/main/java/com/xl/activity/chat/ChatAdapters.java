package com.xl.activity.chat;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gauss.recorder.SpeexPlayer;
import com.xl.activity.R;
import com.xl.activity.base.BaseAdapterListView;
import com.xl.bean.MessageBean;
import com.xl.custom.MyImageView;
import com.xl.util.Utils;

import java.io.File;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;
import pl.droidsonroids.gif.GifDrawable;

public class ChatAdapters extends BaseAdapterListView<MessageBean> {

	public ChatAdapters(Context context, List list) {
		super(list,context);
	}

	@Override
	public int getItemViewType(int position) {
		MessageBean mb = getList().get(position);
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
		MessageBean mb = getItem(position);
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
                holder.voice.setTag(mb);
                try {
                    GifDrawable temp1=null;
                    if(holder.voice_img.getGifDrawable()==null) {
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
                        temp1.reset();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }

        return view;
	}

	class ViewHolder {

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

        @Optional
        @OnClick(R.id.voice)
        void voiceClick(View view){
            MessageBean mb= (MessageBean) view.getTag();
            playVoice(mb);
        }
	}

    SpeexPlayer splayer;
    MessageBean playingMsg;
    boolean playState;
    Handler handler = new Handler(Looper.getMainLooper());

    public void playVoice(MessageBean msg) {
        if (playState) {
            stopArm();
            if (playingMsg.getMsgId() != msg.getMsgId()) {
                playArm(msg);
            }
        } else {
            playArm(msg);
        }
    }

    public void playArm(final MessageBean dm) {
        File file = new File(dm.getContent());
        if (!file.exists()) {
            return;
        }
        splayer = new SpeexPlayer(dm.getContent());
        try {
            playingMsg = dm;

            dm.setPlaying(true);
            notifyDataSetChanged();
            splayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                public void onCompletion(MediaPlayer mp) {
                    completion(dm);
                }
            });
            splayer.startPlay();
            playState = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopArm() {
        if(splayer!=null){
            splayer.stopPlay();
        }
        if (playingMsg != null) {
            playingMsg.setPlaying(false);
            notifyDataSetChanged();
        }
        playState = false;
        playingMsg=null;
        splayer = null;
    }

    public void replay(){
        MessageBean temp = playingMsg;
        stopArm();
        if(temp!=null){
            playVoice(temp);
        }
    }

    void completion(MessageBean dm){
        dm.setPlaying(false);
        playState = false;
        splayer = null;
        playingMsg=null;
        handler.post(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

}
