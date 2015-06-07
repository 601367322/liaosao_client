package com.xl.activity.chat;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gauss.recorder.SpeexPlayer;
import com.github.siyamed.shapeimageview.mask.PorterShapeImageView;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.xl.activity.R;
import com.xl.application.AppClass;
import com.xl.bean.MessageBean;
import com.xl.custom.MyImageView;
import com.xl.db.ChatDao;
import com.xl.util.GifDrawableCache;
import com.xl.util.LogUtil;
import com.xl.util.StaticFactory;
import com.xl.util.StaticUtil;
import com.xl.util.URLS;
import com.xl.util.Utils;

import org.apache.http.Header;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.FindView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import pl.droidsonroids.gif.GifDrawable;

public class ChatAdapters extends RecyclerView.Adapter<ChatAdapters.ViewHolder> {

    private Context context;
    private List<MessageBean> list;
    private AppClass ac;
    private List<MessageBean> downloading = new ArrayList<>();
    private DisplayImageOptions options_default = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.aio_image_default)
            .showImageOnFail(R.drawable.aio_image_default).showImageOnLoading(R.drawable.aio_image_default)
            .cacheInMemory(true).cacheOnDisk(true).build();

    public static final int LEFT_TEXT = 0, LEFT_VOICE = 1, LEFT_IMG = 2, LEFT_FACE = 3;
    public static final int RIGHT_TEXT = 4, RIGHT_VOICE = 5, RIGHT_IMG = 6, RIGHT_FACE = 7;

    public ChatAdapters(Context context, List list) {
        this.list = list;
        this.context = context;
        this.ac = (AppClass) context.getApplicationContext();
    }

    public List<MessageBean> getList() {
        return list;
    }

    public void addFirst(MessageBean bean) {
        list.add(0, bean);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int type) {
        View view = null;
        LogUtil.d("onCreateViewHolder");
        switch (type) {
            case LEFT_TEXT:
                view = LayoutInflater.from(context).inflate(R.layout.chat_left_text_layout, viewGroup, false);
                break;
            case LEFT_VOICE:
                view = LayoutInflater.from(context).inflate(R.layout.chat_left_voice_layout, viewGroup, false);
                break;
            case LEFT_IMG:
                view = LayoutInflater.from(context).inflate(R.layout.chat_left_img_layout, viewGroup, false);
                break;
            case LEFT_FACE:
                view = LayoutInflater.from(context).inflate(R.layout.chat_left_face_layout, viewGroup, false);
                break;
            case RIGHT_TEXT:
                view = LayoutInflater.from(context).inflate(R.layout.chat_right_text_layout, viewGroup, false);
                break;
            case RIGHT_VOICE:
                view = LayoutInflater.from(context).inflate(R.layout.chat_right_voice_layout, viewGroup, false);
                break;
            case RIGHT_IMG:
                view = LayoutInflater.from(context).inflate(R.layout.chat_right_img_layout, viewGroup, false);
                break;
            case RIGHT_FACE:
                view = LayoutInflater.from(context).inflate(R.layout.chat_right_face_layout, viewGroup, false);
                break;
        }
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        LogUtil.d("onBindViewHolder");
        MessageBean mb = list.get(position);
        switch (getItemViewType(position)) {
            case LEFT_TEXT:
            case RIGHT_TEXT:
                if (holder.content != null)
                    holder.content.setText(mb.getContent().toString());
                break;
            case LEFT_VOICE:
            case RIGHT_VOICE:
                float scale = (float) mb.getVoiceTime() / 60f;
                float width = 150f * scale;
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.voice.getLayoutParams();
                layoutParams.width = Utils.dip2px(context, 60 + (int) width);
                holder.voice.setLayoutParams(layoutParams);
                holder.voice.setTag(mb);
                try {
                    switch (getItemViewType(position)) {
                        case LEFT_VOICE:
                            holder.voice_img.setImageResource(R.drawable.chat_left_animation_png);
                            break;
                        case RIGHT_VOICE:
                            holder.voice_img.setImageResource(R.drawable.chat_right_animation_png);
                            break;
                    }
                    if (mb.getLoading() == MessageBean.LOADING_NODOWNLOAD && getItemViewType(position) == LEFT_VOICE && !downloading.contains(mb)) {
                        downloading.add(mb);
                        File file = new File(StaticFactory.APKCardPathChat + mb.getFromId());
                        if (!file.exists()) {
                            file.mkdirs();
                        }
                        file = new File(file, mb.getContent());
                        ac.httpClient.get(URLS.DOWNLOADFILE + ac.deviceId + "/" + mb.getContent() + URLS.LAST, new fileDownloader(file, mb));
                    } else if (mb.getLoading() == MessageBean.LOADING_DOWNLOADFAIL) {

                    } else {
                        if (mb.isPlaying()) {
                            switch (getItemViewType(position)) {
                                case LEFT_VOICE:
                                    holder.voice_img.setImageDrawable(new GifDrawable(context.getResources(), R.drawable.chat_left_animation));
                                    break;
                                case RIGHT_VOICE:
                                    holder.voice_img.setImageDrawable(new GifDrawable(context.getResources(), R.drawable.chat_right_animation));
                                    break;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case LEFT_IMG:
//                setImageSize(mb, holder.img);

                if (mb.getLoading() == MessageBean.LOADING_NODOWNLOAD && getItemViewType(position) == LEFT_IMG && !downloading.contains(mb)) {
                    downloading.add(mb);
                    File file = new File(StaticFactory.APKCardPathChat + mb.getFromId());
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    file = new File(file, mb.getContent());
                    ac.httpClient.get(URLS.DOWNLOADFILE + ac.deviceId + "/" + mb.getContent() + URLS.LAST, new fileDownloader(file, mb));
                } else if (mb.getLoading() == MessageBean.LOADING_DOWNLOADFAIL) {
                    holder.img.setImageResource(R.drawable.aio_image_default);
                } else {
                    ImageLoader.getInstance().displayImage(StaticUtil.FILE + mb.getContent(), holder.img, options_default, new imgListener(mb));
                }
                holder.img.setTag(StaticUtil.FILE + mb.getContent());
                holder.img.setOnClickListener(clickListener);
                break;
            case RIGHT_IMG:
//                setImageSize(mb, holder.img);
                ImageLoader.getInstance().displayImage(StaticUtil.FILE + mb.getContent(), holder.img, options_default, new imgListener(mb));
                holder.img.setTag(StaticUtil.FILE + mb.getContent());
                holder.img.setOnClickListener(clickListener);
                break;
            case LEFT_FACE:
            case RIGHT_FACE:
                GifDrawable drawable = GifDrawableCache.getInstance().getDrawable((long) context.getResources().getIdentifier(mb.getContent(), "drawable", context.getPackageName()), context);
                int img_width = drawable.getMinimumWidth();
                int max_widht = Utils.dip2px(context, 80);
                float scale1 = (float) img_width / (float) max_widht;
                int new_height = (int) ((float) drawable.getMinimumHeight() / scale1);
                RelativeLayout.LayoutParams layoutParams1 = (RelativeLayout.LayoutParams) holder.face.getLayoutParams();
                layoutParams1.width = max_widht;
                layoutParams1.height = new_height;
                holder.face.setLayoutParams(layoutParams1);
                holder.face.setImageGifDrawable(drawable);
                holder.face.setStart(true);
                break;
        }
        if (mb.getLoading() == MessageBean.LOADING_DOWNLOADING) {
            holder.progress.setVisibility(View.VISIBLE);
        } else {
            holder.progress.setVisibility(View.GONE);
        }
        if (mb.getLoading() == MessageBean.LOADING_DOWNLOADFAIL) {
            holder.error.setVisibility(View.VISIBLE);
            holder.error.setTag(mb);
        } else {
            holder.error.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        MessageBean mb = list.get(position);
        int type = LEFT_TEXT;
        if (mb.getToId().equals(ac.deviceId)) {
            type = mb.getMsgType();
        } else {
            switch (mb.getMsgType()) {
                case LEFT_TEXT:
                    type = RIGHT_TEXT;
                    break;
                case LEFT_VOICE:
                    type = RIGHT_VOICE;
                    break;
                case LEFT_IMG:
                    type = RIGHT_IMG;
                    break;
                case LEFT_FACE:
                    type = RIGHT_FACE;
                    break;
            }
        }
        LogUtil.d(type + "");
        return type;
    }

//    void setImageSize(MessageBean mb, ImageView view) {
//        RelativeLayout.LayoutParams layoutParams2 = (RelativeLayout.LayoutParams) view.getLayoutParams();
//        if (mb.imageSize == null) {
//            layoutParams2.width = (int) ((float) context.getResources().getDisplayMetrics().widthPixels / 3f);
//        } else {
//            layoutParams2.width = mb.imageSize.getWidth();
//            layoutParams2.height = mb.imageSize.getHeight();
//        }
//        view.setLayoutParams(layoutParams2);
//    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @Nullable
        @FindView(R.id.content)
        TextView content;

        @Nullable
        @FindView(R.id.voice_img)
        MyImageView voice_img;

        @Nullable
        @FindView(R.id.voice)
        View voice;

        @Nullable
        @FindView(R.id.progress)
        View progress;

        @Nullable
        @FindView(R.id.img)
        PorterShapeImageView img;

        @Nullable
        @FindView(R.id.face)
        MyImageView face;

        @Nullable
        @FindView(R.id.error)
        View error;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Nullable
        @OnClick(R.id.voice)
        void voiceClick(View view) {
            MessageBean mb = (MessageBean) view.getTag();
            if (mb.getLoading() == MessageBean.LOADING_DOWNLOADED) {
                playVoice(mb);
            }
        }

        @Nullable
        @OnClick(R.id.error)
        void errorClick(View view) {
            MessageBean mb = (MessageBean) view.getTag();
            if (mb != null) {
                EventBus.getDefault().post(mb);
            }
        }
    }

    SpeexPlayer splayer;
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

    interface StopListener {
        public void stoped();
    }

    public void stopArm(final StopListener listener) {
        if (splayer != null) {
            splayer.stopPlay();
        }
        if (playingMsg != null) {
            playingMsg.setPlaying(false);
            notifyDataSetChanged();
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
                notifyDataSetChanged();
            }
        });
    }

    class fileDownloader extends FileAsyncHttpResponseHandler {

        MessageBean messageBean = null;

        public fileDownloader(File file, MessageBean mb) {
            super(file);
            this.messageBean = mb;
        }

        @Override
        public void onStart() {
            messageBean.setLoading(MessageBean.LOADING_DOWNLOADING);
            ChatDao.getInstance(context.getApplicationContext()).updateMessage(messageBean);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
            messageBean.setLoading(MessageBean.LOADING_DOWNLOADFAIL);
            downloading.remove(messageBean);
            ChatDao.getInstance(context.getApplicationContext()).updateMessage(messageBean);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, File file) {
            messageBean.setLoading(MessageBean.LOADING_DOWNLOADED);
            messageBean.setContent(file.getPath());
            downloading.remove(messageBean);
            ChatDao.getInstance(context.getApplicationContext()).updateMessage(messageBean);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            });
        }
    }

    class imgListener extends SimpleImageLoadingListener {

        private MessageBean mb;

        public imgListener(MessageBean mb) {
            this.mb = mb;
        }

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//            if (mb.imageSize == null) {
//                int screen_width = (int) ((float) context.getResources().getDisplayMetrics().widthPixels / 3f);
//                int img_width = loadedImage.getWidth();
//                int max_widht = img_width > screen_width ? screen_width : img_width;
//                float scale = (float) img_width / (float) max_widht;
//                int new_height = (int) ((float) loadedImage.getHeight() / scale);
//                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
//                layoutParams.width = max_widht;
//                layoutParams.height = new_height;
//                view.setLayoutParams(layoutParams);
//                if (mb != null) {
//                    mb.imageSize = new MessageBean.ImageSize(max_widht, new_height);
//                }
//            }
        }

    }


    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ImageViewActivity_.intent(context).imageUrl(v.getTag().toString()).start();
        }
    };
}
