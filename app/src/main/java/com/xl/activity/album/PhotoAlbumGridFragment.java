package com.xl.activity.album;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.xl.activity.R;
import com.xl.activity.base.BaseFragment;
import com.xl.bean.PhotoAibum;
import com.xl.bean.PhotoItem;
import com.xl.util.ToastUtil;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

@EFragment(R.layout.photoalbum_gridview)
public class PhotoAlbumGridFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    @ViewById(R.id.photo_gridview)
    GridView gridview;

    @FragmentArg
    PhotoAibum aibum;

    PhotoAlbumGridAdappter adapter;

    PhotoAlbumMainActivity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context != null && context instanceof PhotoAlbumMainActivity) {
            activity = (PhotoAlbumMainActivity) context;
        }
    }

    public void init() {
        adapter = new PhotoAlbumGridAdappter(getActivity());
        adapter.setList(aibum.getBitList());
        gridview.setAdapter(adapter);
        gridview.setOnItemClickListener(this);
        activity.getSupportActionBar().setTitle(R.string.chose_picture);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        PhotoItem gridItem = adapter.getItem(position);
        if (gridItem.isSelect()) {
            gridItem.setSelect(false);
            activity.removePath(gridItem.getPath());
        } else {
            if (activity.imgPath.size() < activity.maxNum) {
                gridItem.setSelect(true);
                activity.addPath(gridItem.getPath());
            } else {
                ToastUtil.toast(getActivity(), "最多只能添加"
                        + activity.maxNum
                        + "张图片");
            }
        }
        adapter.notifyDataSetChanged();
    }

}