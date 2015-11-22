package com.xl.activity.chat;

import android.content.Context;
import android.content.Intent;

import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.xl.application.AppClass;
import com.xl.bean.MessageBean;
import com.xl.db.ChatDao;
import com.xl.util.StaticFactory;
import com.xl.util.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import de.greenrobot.event.EventBus;

/**
 * Created by Shen on 2015/9/17.
 */
public class ChatDownLoadManager {

    private List<MessageBean> downlist = new ArrayList<>();

    private static ChatDownLoadManager instance;

    private Context context;

    private AppClass ac;

    private ChatDao messageDao;

    public static synchronized ChatDownLoadManager getInstance(Context context) {
        if (instance == null) {
            instance = new ChatDownLoadManager(context.getApplicationContext());
        }
        return instance;
    }

    public ChatDownLoadManager(Context context) {
        this.context = context;
        this.ac = (AppClass) context.getApplicationContext();
        this.messageDao = ChatDao.getInstance(context);
    }

    public void down(MessageBean msg) {
        for (int i = 0; i < downlist.size(); i++) {
            if (downlist.get(i).getMsgId().equals(msg.getMsgId())) {
                //如果正在下载则不执行
                return;
            }
        }
        downlist.add(msg);
        String url = null;
        switch (msg.getMsgType()) {
            case MessageBean.VOICE:
                url = msg.getContent();
                break;
            case MessageBean.RADIO_NEW:
                url = Utils.getDownloadFileUrl(ac.deviceId,msg.getRadioBean().file);
                break;
        }
        ac.httpClient.get(url, new fileDownload(msg, StaticFactory.APKCardPathChat + msg.getFromId() + "/" + msg.getContent().hashCode()));
    }

    class fileDownload extends FileAsyncHttpResponseHandler {
        MessageBean msg;

        public fileDownload(MessageBean msg, String filePath) {
            super(new File(filePath));
            this.msg = msg;
        }

        @Override
        public void onProgress(long bytesWritten, long totalSize) {
            super.onProgress(bytesWritten, totalSize);
            EventBus.getDefault().post(new ChatPlayVideoFragment.VideoDownBean(msg, (int) bytesWritten, (int) totalSize));
        }

        public void onStart() {
            File file = new File(this.file.getParent());
            if (!file.exists()) {
                file.mkdirs();
            }
            msg.setLoading(MessageBean.LOADING_DOWNLOADING);
            messageDao.update(msg);
            sendBoradcast(msg);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers,
                              Throwable throwable, File file) {
            // TODO Auto-generated method stub
            msg.setLoading(MessageBean.LOADING_DOWNLOADFAIL);
            messageDao.update(msg);
            sendBoradcast(msg);
            downlist.remove(msg);
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, File file) {
            msg.setLoading(MessageBean.LOADING_DOWNLOADED);
            switch (msg.getMsgType()) {
                case MessageBean.VOICE:
                    msg.setContent(file.getPath());
                    break;
                case MessageBean.RADIO_NEW:
                    MessageBean.RadioBean rb = msg.getRadioBean();
                    rb.file = file.getPath();
                    msg.setRadioBean(rb);
                    break;
            }
            messageDao.update(msg);
            sendBoradcast(msg);
            downlist.remove(msg);
        }

    }

    public void sendBoradcast(MessageBean bean) {
        Intent i = new Intent(ChatPlayVideoFragment.MESSAGE_DOWNLOAD);
        i.putExtra("bean", bean);
        context.sendBroadcast(i);
    }

}
