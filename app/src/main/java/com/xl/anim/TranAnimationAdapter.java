package com.xl.anim;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;

import com.nhaarman.listviewanimations.appearance.AnimationAdapter;

/**
 * Created by sbb on 2014/12/26.
 */
public class TranAnimationAdapter extends AnimationAdapter {
    /**
     * Creates a new AnimationAdapter, wrapping given BaseAdapter.
     *
     * @param baseAdapter the BaseAdapter to wrap.
     */
    public TranAnimationAdapter(@NonNull BaseAdapter baseAdapter,@NonNull AbsListView listView) {
        super(baseAdapter);
        setAbsListView(listView);
    }

    @NonNull
    @Override
    public Animator[] getAnimators(@NonNull ViewGroup parent, @NonNull View view) {
        ObjectAnimator tranY = ObjectAnimator.ofFloat(view, "translationY", parent.getHeight() , 0);
        return new ObjectAnimator[]{tranY};
    }

}
