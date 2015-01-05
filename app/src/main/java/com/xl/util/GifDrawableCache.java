package com.xl.util;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import java.io.IOException;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.Hashtable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import pl.droidsonroids.gif.GifDrawable;

public class GifDrawableCache {
    static private GifDrawableCache cache;
    /**
     * 用于Chche内容的存储
     */
    private Hashtable<Long, MySoftRef> hashRefs;
    /**
     * 垃圾Reference的队列（所引用的对象已经被回收，则将该引用存入队列中）
     */
    private ReferenceQueue<GifDrawable> q;

    /**
     * 继承SoftReference，使得每一个实例都具有可识别的标识。
     */
    private class MySoftRef extends SoftReference<GifDrawable> {
        private Long _key = 0l;

        public MySoftRef(GifDrawable bmp, ReferenceQueue<GifDrawable> q, Long key) {
            super(bmp, q);
            _key = key;
        }
    }

    private GifDrawableCache() {
        hashRefs = new Hashtable<Long, MySoftRef>();
        q = new ReferenceQueue<GifDrawable>();
    }

    /**
     * 取得缓存器实例
     */
    public static GifDrawableCache getInstance() {
        if (cache == null) {
            cache = new GifDrawableCache();
        }
        return cache;
    }

    /**
     * 以软引用的方式对一个Drawable对象的实例进行引用并保存该引用
     */
    private void addCacheDrawable(GifDrawable bmp, Long key) {
        cleanCache();// 清除垃圾引用
        MySoftRef ref = new MySoftRef(bmp, q, key);
        hashRefs.put(key, ref);
    }

    /**
     * 依据所指定的drawable下的图片资源ID号（可以根据自己的需要从网络或本地path下获取），重新获取相应Drawable对象的实例
     */
    public GifDrawable getDrawable(Long resId, Context context) {
        GifDrawable bmp = null;
        if (hashRefs.containsKey(Long.valueOf(resId))) {
            MySoftRef ref =  hashRefs.get(resId);
            bmp =  ref.get();
        }
        if (bmp == null) {
            try {
                bmp = new GifDrawable(context.getResources(), Integer.valueOf(resId.toString()));
            } catch (NotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.addCacheDrawable(bmp, Long.valueOf(resId));
        }
        return bmp;
    }

    ExecutorService executorService = Executors.newCachedThreadPool();

    public void displayDrawable(final ImageView imageview, final String imageUrl) {
        executorService.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    GifDrawable bmp = null;
                    if (hashRefs.containsKey(Long.valueOf(imageUrl.hashCode()))) {
                        MySoftRef ref = (MySoftRef) hashRefs.get(Long.valueOf(imageUrl.hashCode()));
                        bmp = (GifDrawable) ref.get();
                        if (bmp != null)
                            bmp.start();
                    }
                    GifDrawable b = bmp;
                    if (b == null) {
                        b = new GifDrawable(imageUrl);
                    }
                    addCacheDrawable(b, Long.valueOf(imageUrl.hashCode()));
                    hanlder.post(new postRunnable(imageview, b));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    class postRunnable implements Runnable {
        ImageView imageview;
        GifDrawable d;

        public postRunnable(ImageView imageview, GifDrawable d) {
            this.imageview = imageview;
            this.d = d;
        }

        public void run() {
//			if(imageview instanceof MyGifImageView){
//				((MyGifImageView)imageview).setImageDrawable(d);
//			}else{
            imageview.setImageDrawable(d);
//			}
        }
    }

    ;

    Handler hanlder = new Handler(Looper.getMainLooper());

    private void cleanCache() {
        MySoftRef ref = null;
        while ((ref = (MySoftRef) q.poll()) != null) {
            if (ref.get() != null) ref.get().stop();
            hashRefs.remove(ref._key);
        }
    }

    /**
     * 清除Cache内的全部内容
     */
    public void clearCache() {
        cleanCache();
        hashRefs.clear();
        System.gc();
        System.runFinalization();
    }
}