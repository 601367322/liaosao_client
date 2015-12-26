package com.xl.activity.user;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;

import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.xl.activity.R;
import com.xl.activity.album.PhotoAlbumMainActivity_;
import com.xl.activity.base.BaseBackActivity;
import com.xl.bean.ImageBean;
import com.xl.bean.UserTable_6;
import com.xl.db.DBHelper;
import com.xl.util.JsonHttpResponseHandler;
import com.xl.util.ResultCode;
import com.xl.util.ToastUtil;
import com.xl.util.URLS;
import com.xl.util.Utils;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Shen on 2015/12/13.
 */
@EActivity(R.layout.activity_edit_album)
@OptionsMenu(R.menu.edit_album_menu)
public class EditAlbumActivity extends BaseBackActivity {

    public static final int CHOOSE_ALBUM = 1;

    EditAlbumAdapter adapter;
    @ViewById
    GridView gridview;
    @ViewById
    FloatingActionButton add;
    @OptionsMenuItem
    MenuItem ok, delete, edit;

    AtomicBoolean editing = new AtomicBoolean(false);

    UserTable_6 user;


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (editing.get()) {
            ok.setVisible(true);
            delete.setVisible(true);
            edit.setVisible(false);
        } else {
            ok.setVisible(false);
            delete.setVisible(false);
            edit.setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @OptionsItem
    public void edit() {
        refreshActionState(true);
    }

    @OptionsItem
    public void ok() {
        refreshActionState(false);
    }

    public void refreshActionState(boolean b) {
        editing.compareAndSet(!b, b);
        ActivityCompat.invalidateOptionsMenu(this);
        adapter.setEditing(editing);
    }

    @Override
    protected void init() {
        adapter = new EditAlbumAdapter(this);

        user = DBHelper.userDao.getUserTableByDeviceId(ac.deviceId);
        if (user != null && user.getBean() != null) {
            adapter.setList(user.getBean().getAlbum());
        }

        gridview.setAdapter(adapter);
    }

    @Click
    public void add() {
        if (user != null && user.getBean() != null) {
            ArrayList<String> paths = new ArrayList<>();
            for (ImageBean bean : user.getBean().getAlbum()) {
                paths.add(bean.getPath());
            }
            PhotoAlbumMainActivity_.intent(this).maxNum(8).imgPath(paths).startForResult(CHOOSE_ALBUM);
        }
    }

    @OnActivityResult(CHOOSE_ALBUM)
    public void onChooseAlbum(@OnActivityResult.Extra(value = "images") ArrayList<String> images) {
        List<String> newImg = new ArrayList<>();
        if (images != null) {
            for (int i = 0; i < images.size(); i++) {
                String str = images.get(i);
                AtomicBoolean exists = new AtomicBoolean(false);
                for (int j = 0; j < user.getBean().getAlbum().size(); j++) {
                    ImageBean bean = user.getBean().getAlbum().get(j);
                    if (str.equals(bean.getPath())) {
                        exists.compareAndSet(false, true);
                    }
                }
                if (!exists.get()) {
                    newImg.add(str);
                }
            }
            if (newImg.size() > 0) {
                uploadImage(newImg, 0);
            }
        }
    }

    @UiThread
    public void uploadImage(final List<String> imgs, final int position) {
        try {
            if (position >= imgs.size()) {
                ToastUtil.toast(this, "上传完成");
                return;
            }
            RequestParams param = ac.getRequestParams();
            param.put("file", new File(imgs.get(position)));
            ac.httpClient.post(URLS.UPLOADUSERALBUM, param, new JsonHttpResponseHandler(this, "正在上传第" + (position + 1) + "张图片") {
                @Override
                public void onSuccessCode(JSONObject jo) throws Exception {
                    super.onSuccessCode(jo);
                    ImageBean bean = Utils.jsonToBean(jo.optString(ResultCode.CONTENT), ImageBean.class);
                    adapter.add(bean);
                    adapter.notifyDataSetChanged();

                    user.getBean().setAlbum(adapter.getList());
                    DBHelper.userDao.updateUser(user);

                    uploadImage(imgs, position + 1);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @ItemClick
    public void gridview(int position) {
        if (editing.get()) {
            adapter.getItem(position).setChecked(!adapter.getItem(position).isChecked());
            adapter.notifyDataSetChanged();
        }
    }

    @OptionsItem
    public void delete() {
        final List<Integer> deleteList = new ArrayList<>();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).isChecked()) {
                deleteList.add(adapter.getItem(i).getId());
            }
        }

        if (deleteList.size() > 0) {
            RequestParams param = ac.getRequestParams();
            param.put("ids", new Gson().toJson(deleteList));
            ac.httpClient.post(this, URLS.DELETEALBUM, param, new JsonHttpResponseHandler(mContext, "正在删除") {
                @Override
                public void onSuccessCode(JSONObject jo) throws Exception {
                    adapter.removeByIds(deleteList);

                    user.getBean().setAlbum(adapter.getList());
                    DBHelper.userDao.updateUser(user);

                    ToastUtil.toast(mContext, "删除成功");
                }
            });
        }
    }
}
