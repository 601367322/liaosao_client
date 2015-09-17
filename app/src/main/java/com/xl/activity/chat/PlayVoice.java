package com.xl.activity.chat;

import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;

import com.xl.activity.chat.adapter.ChatAdapters;
import com.xl.bean.MessageBean;

import java.io.File;

/**
 * Created by Shen on 2015/9/13.
 */
public class PlayVoice {

    private static PlayVoice instance;

    private ChatAdapters adapter = null;

    private PlayVoice(ChatAdapters adapter) {
        this.adapter = adapter;
    }

    public static synchronized PlayVoice getInstance(ChatAdapters adapter) {
        if (instance == null) {
            instance = new PlayVoice(adapter);
        }
        return instance;
    }

    MediaPlayer splayer;
    MessageBean playingMsg;
    boolean playState;
    Handler handler = new Handler(Looper.getMainLooper());
    private static long lastClickTime;

    public static boolean isFastClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 1000) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    public void playVoice(final MessageBean msg) {
        if (isFastClick()) {
            return;
        }
        if (playState) {
            final MessageBean temp = playingMsg;
            stopArm(new StopListener() {
                @Override
                public void stoped() {
                    if (temp.getMsgId() != msg.getMsgId()) {
                        playArm(msg);
                    }
                }
            });
        } else {
            playArm(msg);
        }
    }

    public void playArm(final MessageBean dm) {
        File file = new File(dm.getContent());
        if (!file.exists()) {
            return;
        }
        splayer = new MediaPlayer();
        try {
            playingMsg = dm;
            splayer.setDataSource(dm.getContent());
            splayer.prepare();
            splayer.start();
            dm.setPlaying(true);
            adapter.notifyDataSetChanged();
            splayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                public void onCompletion(MediaPlayer mp) {
                    completion(dm);
                }
            });
            playState = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    interface StopListener {
        public void stoped();
    }

    public void stopArm(final StopListener listener) {
        if (splayer != null) {
            splayer.stop();
            splayer.release();
            splayer = null;
        }
        if (playingMsg != null) {
            playingMsg.setPlaying(false);
            adapter.notifyDataSetChanged();
        }
        playState = false;
        playingMsg = null;
        splayer = null;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (listener != null)
                    listener.stoped();
            }
        }, 1000);
    }

    public void replay() {
        final MessageBean temp = playingMsg;
        stopArm(new StopListener() {
            @Override
            public void stoped() {
                if (temp != null) {
                    playVoice(temp);
                }
            }
        });

    }

    void completion(MessageBean dm) {
        dm.setPlaying(false);
        playState = false;
        splayer = null;
        playingMsg = null;
        handler.post(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

}
