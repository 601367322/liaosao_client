package com.xl.activity.chat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.xl.activity.R;
import com.xl.application.AppClass;
import com.xl.bean.MessageBean;
import com.xl.bean.UserTable_6;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapters extends RecyclerView.Adapter<BaseHolder> {

    private Context context;
    private List<MessageBean> list;
    private AppClass ac;

    public static final int LEFT_TEXT = 0, LEFT_VOICE = 1, LEFT_IMG = 2, LEFT_FACE = 3, LEFT_RADIO = 8, LEFT_RADIO_NEW = 10;
    public static final int RIGHT_TEXT = 4, RIGHT_VOICE = 5, RIGHT_IMG = 6, RIGHT_FACE = 7, RIGHT_RADIO = 9, RIGHT_RADIO_NEW = 11;

    public List<MessageBean> downloading = new ArrayList<>();

    public UserTable_6 friend;

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
    public BaseHolder onCreateViewHolder(ViewGroup viewGroup, int type) {
        BaseHolder holder = null;
        switch (type) {
            case LEFT_TEXT:
                holder = new TextViewHolder(LayoutInflater.from(context).inflate(R.layout.chat_left_text_layout, viewGroup, false));
                break;
            case LEFT_VOICE:
                holder = new VoiceViewHolder(LayoutInflater.from(context).inflate(R.layout.chat_left_voice_layout, viewGroup, false));
                break;
            case LEFT_IMG:
                holder = new ImageViewHolder(LayoutInflater.from(context).inflate(R.layout.chat_left_img_layout, viewGroup, false));
                break;
            case LEFT_FACE:
                holder = new FaceViewHolder(LayoutInflater.from(context).inflate(R.layout.chat_left_face_layout, viewGroup, false));
                break;
            case RIGHT_TEXT:
                holder = new TextViewHolder(LayoutInflater.from(context).inflate(R.layout.chat_right_text_layout, viewGroup, false));
                break;
            case RIGHT_VOICE:
                holder = new VoiceViewHolder(LayoutInflater.from(context).inflate(R.layout.chat_right_voice_layout, viewGroup, false));
                break;
            case RIGHT_IMG:
                holder = new ImageViewHolder(LayoutInflater.from(context).inflate(R.layout.chat_right_img_layout, viewGroup, false));
                break;
            case RIGHT_FACE:
                holder = new FaceViewHolder(LayoutInflater.from(context).inflate(R.layout.chat_right_face_layout, viewGroup, false));
                break;
            case LEFT_RADIO:
                holder = new VideoViewHolder(LayoutInflater.from(context).inflate(R.layout.chat_left_radio_layout, viewGroup, false));
                break;
            case RIGHT_RADIO:
                holder = new VideoViewHolder(LayoutInflater.from(context).inflate(R.layout.chat_right_radio_layout, viewGroup, false));
                break;
            case LEFT_RADIO_NEW:
                holder = new VideoViewHolderNew(LayoutInflater.from(context).inflate(R.layout.chat_left_radio_layout_new, viewGroup, false));
                break;
            case RIGHT_RADIO_NEW:
                holder = new VideoViewHolderNew(LayoutInflater.from(context).inflate(R.layout.chat_right_radio_layout_new, viewGroup, false));
                break;
        }
        holder.setMstType(type);
        holder.setAdapter(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(BaseHolder holder, int position) {
        holder.bind(list.get(position));
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
            switch (mb.getMsgType()) {
                case MessageBean.TEXT:
                    type = LEFT_TEXT;
                    break;
                case MessageBean.VOICE:
                    type = LEFT_VOICE;
                    break;
                case MessageBean.IMAGE:
                    type = LEFT_IMG;
                    break;
                case MessageBean.FACE:
                    type = LEFT_FACE;
                    break;
                case MessageBean.RADIO:
                    type = LEFT_RADIO;
                    break;
                case MessageBean.RADIO_NEW:
                    type = LEFT_RADIO_NEW;
                    break;

            }
        } else {
            switch (mb.getMsgType()) {
                case MessageBean.TEXT:
                    type = RIGHT_TEXT;
                    break;
                case MessageBean.VOICE:
                    type = RIGHT_VOICE;
                    break;
                case MessageBean.IMAGE:
                    type = RIGHT_IMG;
                    break;
                case MessageBean.FACE:
                    type = RIGHT_FACE;
                    break;
                case MessageBean.RADIO:
                    type = RIGHT_RADIO;
                    break;
                case MessageBean.RADIO_NEW:
                    type = RIGHT_RADIO_NEW;
                    break;
            }
        }
        return type;
    }

    public UserTable_6 getFriend() {
        return friend;
    }

    public void setFriend(UserTable_6 friend) {
        this.friend = friend;
    }
}
