package com.xl.game;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.xl.bean.PhotoAibum;
import com.xl.bean.PhotoItem;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class PhotoAlbumActivity {

    private Context context;

    public PhotoAlbumActivity(Context context) {
        this.context = context;
    }

    private final String[] STORE_IMAGES = {
            MediaStore.Images.Media.DISPLAY_NAME, // 显示的名
            MediaStore.Images.Media.DATA, MediaStore.Images.Media.LONGITUDE, // 经度
            MediaStore.Images.Media._ID, // id
            MediaStore.Images.Media.BUCKET_ID, // dir id 目录
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME // dir name 目录名字

    };

    /**
     * 方法描述：按相册获取图片信息
     */
    public String getPhotoAlbum() {
        List<PhotoAibum> aibumList = new ArrayList<PhotoAibum>();
        Cursor cursor = MediaStore.Images.Media.query(context
                        .getContentResolver(),
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, STORE_IMAGES);
        LinkedHashMap<String, PhotoAibum> countMap = new LinkedHashMap<String, PhotoAibum>();
        PhotoAibum pa = null;
        while (cursor.moveToNext()) {
            String path = cursor.getString(1);
            String id = cursor.getString(3);
            String dir_id = cursor.getString(4);
            String dir = cursor.getString(5);
            if (!countMap.containsKey(dir_id)) {
                pa = new PhotoAibum();
                pa.setName(dir);
                pa.setBitmap(Integer.parseInt(id));
                pa.setCount("1");
                pa.setPath(path);
                pa.getBitList().add(new PhotoItem(Integer.valueOf(id), path));
                countMap.put(dir_id, pa);
            } else {
                pa = countMap.get(dir_id);
                pa.setCount(String.valueOf(Integer.parseInt(pa.getCount()) + 1));
                pa.getBitList().add(new PhotoItem(Integer.valueOf(id), path));
            }
        }
        cursor.close();
        Iterable<String> it = countMap.keySet();
        for (String key : it) {
            aibumList.add(countMap.get(key));
        }

        if (aibumList.size() > 0) {
            int index = (int)(Math.random() * (aibumList.size() - 1));
            PhotoAibum photoAibum = aibumList.get(index);
            return photoAibum.getPath();
        }
        return null;
    }

}
