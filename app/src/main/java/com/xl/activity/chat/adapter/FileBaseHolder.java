package com.xl.activity.chat.adapter;

import android.view.View;

import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.xl.bean.MessageBean;
import com.xl.db.ChatDao;

import org.apache.http.Header;

import java.io.File;



/**
 * Created by Shen on 2015/9/13.
 */
public class FileBaseHolder extends BaseHolder{

    public FileBaseHolder(View itemView) {
        super(itemView);
    }

    @Override
    protected void bind(MessageBean bean) {
        super.bind(bean);
    }

    protected class fileDownloader extends FileAsyncHttpResponseHandler {

        MessageBean messageBean = null;

        public fileDownloader(File file, MessageBean mb) {
            super(file);
            this.messageBean = mb;
        }

        @Override
        public void onStart() {
            messageBean.setLoading(MessageBean.LOADING_DOWNLOADING);
            ChatDao.getInstance(context.getApplicationContext()).updateMessage(messageBean);
            getHandler().post(new Runnable() {
                @Override
                public void run() {
                    getAdapter().notifyDataSetChanged();
                }
            });
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
            messageBean.setLoading(MessageBean.LOADING_DOWNLOADFAIL);
            adapter.downloading.remove(messageBean);
            ChatDao.getInstance(context.getApplicationContext()).updateMessage(messageBean);
            getHandler().post(new Runnable() {
                @Override
                public void run() {
                    getAdapter().notifyDataSetChanged();
                }
            });
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, File file) {
            messageBean.setLoading(MessageBean.LOADING_DOWNLOADED);
            messageBean.setContent(file.getPath());
            adapter.downloading.remove(messageBean);
            ChatDao.getInstance(context.getApplicationContext()).updateMessage(messageBean);
            getHandler().post(new Runnable() {
                @Override
                public void run() {
                    getAdapter().notifyDataSetChanged();
                }
            });
        }
    }
}
