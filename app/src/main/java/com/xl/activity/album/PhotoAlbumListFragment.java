package com.xl.activity.album;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.xl.activity.R;
import com.xl.activity.base.BaseFragment;
import com.xl.bean.PhotoAibum;
import com.xl.bean.PhotoItem;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@EFragment(R.layout.photoalbum_listview)
public class PhotoAlbumListFragment extends BaseFragment implements OnItemClickListener{

    @ViewById(R.id.album_listview)
    ListView listview;

    private List<PhotoAibum> aibumList;

    PhotoAlbumMainActivity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context != null && context instanceof PhotoAlbumMainActivity) {
            activity = (PhotoAlbumMainActivity) context;
        }
    }

    // 设置获取图片的字段信
    private static final String[] STORE_IMAGES = {
            MediaStore.Images.Media.DISPLAY_NAME, // 显示的名
            MediaStore.Images.Media.DATA, MediaStore.Images.Media.LONGITUDE, // 经度
            MediaStore.Images.Media._ID, // id
            MediaStore.Images.Media.BUCKET_ID, // dir id 目录
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME // dir name 目录名字

    };

    public void init() {
        aibumList = getPhotoAlbum();

        PhotoAibumListAdapter adapter = new PhotoAibumListAdapter(getActivity());
        adapter.setList(aibumList);

        listview.setAdapter(adapter);
        listview.setOnItemClickListener(this);

        activity.getSupportActionBar().setTitle(getString(R.string.album));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ((PhotoAlbumMainActivity) getActivity()).replaceFragment(aibumList.get(position));
    }

    private List<PhotoAibum> getPhotoAlbum() {
        List<PhotoAibum> aibumList = new ArrayList<>();
        Cursor cursor = MediaStore.Images.Media.query(getActivity().getContentResolver(),
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, STORE_IMAGES);
        LinkedHashMap<String, PhotoAibum> countMap = new LinkedHashMap<>();
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
        return aibumList;
    }
}