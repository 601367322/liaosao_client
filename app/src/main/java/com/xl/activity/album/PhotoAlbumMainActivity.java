package com.xl.activity.album;

import android.content.Intent;
import android.view.MenuItem;

import com.xl.activity.R;
import com.xl.activity.base.BaseBackActivity;
import com.xl.bean.PhotoAibum;
import com.xl.bean.PhotoItem;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;

import java.util.ArrayList;

@EActivity(R.layout.activity_only_fragment)
@OptionsMenu(R.menu.album_menu)
public class PhotoAlbumMainActivity extends BaseBackActivity {

    @Extra
    int maxNum = 0;
    @Extra
    ArrayList<String> imgPath = new ArrayList<>();

    @OptionsMenuItem
    MenuItem ok;

    PhotoAlbumGridFragment pa;

    @Override
    protected void init() {
        getSupportActionBar().setTitle("相册");
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, PhotoAlbumListFragment_.builder().build()).commitAllowingStateLoss();
    }

    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        changeNum();
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 打开某个相册
     * @param aibum
     */
    public void replaceFragment(PhotoAibum aibum) {

        for (PhotoItem item : aibum.getBitList()) {
            if (imgPath.contains(item.getPath())) {
                item.setSelect(true);
            }
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, pa = PhotoAlbumGridFragment_.builder().aibum(aibum).build())
                .addToBackStack(null).commitAllowingStateLoss();
    }

    public void homeClick() {
        if (pa != null && pa.isVisible()) {
            onBackPressed();
        } else {
            finish();
        }
    }

    @OptionsItem(R.id.ok)
    public void rightClick() {
        Intent i = new Intent();
        i.putStringArrayListExtra("images", imgPath);
        setResult(RESULT_OK, i);
        finish();
    }

    public void changeNum() {
        ok.setTitle("确定(" + imgPath.size() + "/" + maxNum + ")");
    }

    public void addPath(String path) {
        imgPath.add(path);
        changeNum();
    }

    public void removePath(String path) {
        imgPath.remove(path);
        changeNum();
    }
}