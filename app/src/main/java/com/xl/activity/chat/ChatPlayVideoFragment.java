package com.xl.activity.chat;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xl.activity.R;
import com.xl.application.AppClass;
import com.xl.bean.MessageBean;
import com.xl.custom.RoundProgressBar;
import com.xl.util.Utils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.Receiver;
import org.androidannotations.annotations.ViewById;

import java.io.Serializable;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

/**
 * Created by Shen on 2015/9/17.
 */
@EFragment(R.layout.fragment_chat_play_video)
public class ChatPlayVideoFragment extends DialogFragment implements TextureView.SurfaceTextureListener
        , View.OnClickListener, MediaPlayer.OnCompletionListener {

    public static final String MESSAGE_DOWNLOAD = "com.xl.activity.chat.ChatPlayVideoFragment.messagedownload";

    @ViewById(R.id.video_textureview)
    TextureView video_textureview;
    @FragmentArg
    MessageBean bean;
    @ViewById(R.id.loadProgressBar)
    RoundProgressBar loadProgressBar;
    @ViewById(R.id.thumb_image)
    ImageView thumb_image;

    @App
    AppClass ac;
    private MediaPlayer mediaPlayer;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // request a window without the title
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() == null) {
            return;
        }
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int dialogWidth = dm.widthPixels; // specify a value here
        int dialogHeight = dm.widthPixels; // specify a value here
        getDialog().getWindow().setLayout(dialogWidth, dialogHeight);
    }

    @AfterViews
    public void init() {

        EventBus.getDefault().register(this);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) video_textureview
                .getLayoutParams();
        layoutParams.width = displaymetrics.widthPixels;
        layoutParams.height = displaymetrics.widthPixels;
        video_textureview.setLayoutParams(layoutParams);

        video_textureview.setSurfaceTextureListener(this);
        video_textureview.setOnClickListener(this);

    }

    @Subscribe
    public void onEvent(VideoDownBean vdb) {
        if (vdb.msg.getMsgId().equals(bean.getMsgId())) {
            if (vdb.current == vdb.total) {
                loadProgressBar.setVisibility(View.GONE);
            } else {
                loadProgressBar.setVisibility(View.VISIBLE);
                loadProgressBar.setMax(vdb.total);
                loadProgressBar.setProgress(vdb.current);
            }
        }
    }

    public static class VideoDownBean implements Serializable {
        public MessageBean msg;
        public int current;
        public int total;

        public VideoDownBean() {
            super();
        }

        public VideoDownBean(MessageBean msg, int current, int total) {
            this.msg = msg;
            this.current = current;
            this.total = total;
        }
    }

    @Receiver(actions = MESSAGE_DOWNLOAD)
    public void onMessageChanged(Intent intent) {
        MessageBean msg = (MessageBean) intent.getSerializableExtra("bean");
        if (msg.getMsgId().equals(bean.getMsgId())) {
            bean.setLoading(msg.getLoading());
            bean.setContent(msg.getContent());
            if (bean.getLoading() == MessageBean.LOADING_DOWNLOADED) {
                prepare(mSurface);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
                mediaPlayer.release();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
        EventBus.getDefault().unregister(this);
    }

    private void prepare(Surface surface) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.setOnCompletionListener(null);
                mediaPlayer.release();
            }
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.reset();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            // 设置需要播放的视频

            MessageBean.RadioBean rb = bean.getRadioBean();
            boolean b = false;
            if (rb.file.startsWith("/")) {
                b = true;
                thumb_image.setVisibility(View.GONE);
                mediaPlayer.setDataSource(rb.file);
            } else {
                thumb_image.setVisibility(View.VISIBLE);
                ImageLoader.getInstance().displayImage(Utils.getDownloadFileUrl(ac.deviceId,rb.thumb), thumb_image);
                ChatDownLoadManager.getInstance(getActivity()).down(bean);
            }
            // 把视频画面输出到Surface
            mediaPlayer.setSurface(surface);
            mediaPlayer.setLooping(true);
            mediaPlayer.prepare();
            mediaPlayer.seekTo(0);
            if (b)
                mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    Surface mSurface = null;

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        prepare(mSurface = new Surface(surface));
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Click(R.id.video_textureview)
    public void onTextureviewClick() {
        dismissAllowingStateLoss();
    }
}
