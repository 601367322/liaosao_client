package com.xl.activity.chat.adapter;

import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.xl.activity.R;
import com.xl.activity.chat.PlayVoice;
import com.xl.bean.MessageBean;
import com.xl.custom.MyImageView;
import com.xl.util.StaticFactory;
import com.xl.util.URLS;
import com.xl.util.Utils;

import java.io.File;

import butterknife.Bind;
import butterknife.OnClick;
import pl.droidsonroids.gif.GifDrawable;

/**
 * Created by Shen on 2015/9/13.
 */
public class VoiceViewHolder extends FileBaseHolder{

    @Nullable
    @Bind(R.id.voice_img)
    MyImageView voice_img;

    @Nullable
    @Bind(R.id.voice)
    View voice;

    public VoiceViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    protected void bind(MessageBean bean) {
        super.bind(bean);

        float scale = (float) bean.getVoiceTime() / 60f;
        float width = 150f * scale;
        ViewGroup.LayoutParams layoutParams = voice.getLayoutParams();
        layoutParams.width = Utils.dip2px(context, 60 + (int) width);
        voice.setLayoutParams(layoutParams);
        voice.setTag(bean);
        try {
            switch (getMstType()) {
                case ChatAdapters.LEFT_VOICE:
                    voice_img.setImageResource(R.drawable.chat_left_animation_png);
                    break;
                case ChatAdapters.RIGHT_VOICE:
                    voice_img.setImageResource(R.drawable.chat_right_animation_png);
                    break;
            }
            if (bean.getLoading() == MessageBean.LOADING_NODOWNLOAD && getMstType() == ChatAdapters.LEFT_VOICE && !adapter.downloading.contains(bean)) {
                adapter.downloading.add(bean);
                File file = new File(StaticFactory.APKCardPathChat + bean.getFromId());
                if (!file.exists()) {
                    file.mkdirs();
                }
                file = new File(file, bean.getContent());
                ac.httpClient.get(URLS.DOWNLOADFILE + ac.deviceId + "/" + bean.getContent() + URLS.LAST, new fileDownloader(file, bean));
            } else if (bean.getLoading() == MessageBean.LOADING_DOWNLOADFAIL) {

            } else {
                if (bean.isPlaying()) {
                    switch (getMstType()) {
                        case ChatAdapters.LEFT_VOICE:
                            voice_img.setImageDrawable(new GifDrawable(context.getResources(), R.drawable.chat_left_animation));
                            break;
                        case ChatAdapters.RIGHT_VOICE:
                            voice_img.setImageDrawable(new GifDrawable(context.getResources(), R.drawable.chat_right_animation));
                            break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @OnClick(R.id.voice)
    void voiceClick(View view) {
        MessageBean mb = (MessageBean) view.getTag();
        if (mb.getLoading() == MessageBean.LOADING_DOWNLOADED) {
            PlayVoice.getInstance(adapter).playVoice(mb);
        }
    }


}
