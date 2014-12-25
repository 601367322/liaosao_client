package com.xl.activity.base;

import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.xl.application.AppClass;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EFragment;

@EFragment
public abstract class BaseFragment extends Fragment {

    @App
    protected AppClass ac;

    @AfterViews
    public abstract void init();

    Toast toast;
    public void toast(String str){
        toast=Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT);
        toast.show();
    }
}
