package org.voiddog.mblog.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.voiddog.mblog.R;
import org.voiddog.mblog.adapter.PhotoViewPagerAdapter;

/**
 * 支持图片缩放的view pager
 * Created by Dog on 2015/5/16.
 */
@EFragment(R.layout.fragment_photo_view_pager)
public class PhotoViewPagerFragment extends Fragment {
    @ViewById
    ViewPager vp_photo;

    //文件路径
    String[] photoPaths;
    //是否本地加载
    boolean isLocal;
    int index;
    //适配器
    PhotoViewPagerAdapter adapter;
    ViewPager.OnPageChangeListener onPageChangeListener;

    /**
     * 设置页面滚动监听
     * @param onPageChangeListener 滚动监听函数
     */
    public void setOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener){
        this.onPageChangeListener = onPageChangeListener;
        if(vp_photo != null){
            vp_photo.setOnPageChangeListener(onPageChangeListener);
        }
    }

    /**
     * 转到第index页
     * @param index 需要转到的页数
     */
    public void changePageToIndex(int index){
        if(index > 0 && index < adapter.getCount()) {
            vp_photo.setCurrentItem(index);
        }
    }

    @AfterViews
    void init(){
        Bundle bundle = getArguments();
        if(bundle == null){
            return;
        }
        photoPaths = bundle.getStringArray("photoPaths");
        isLocal = bundle.getBoolean("isLocal", false);
        index = bundle.getInt("index", 0);
        if(photoPaths == null){
            return;
        }

        if(onPageChangeListener != null){
            vp_photo.setOnPageChangeListener(onPageChangeListener);
        }
        //开始初始化适配器，加载数据
        adapter = new PhotoViewPagerAdapter(getActivity(), photoPaths, isLocal);
        vp_photo.setAdapter(adapter);
        changePageToIndex(index);
    }
}
