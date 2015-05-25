package org.voiddog.mblog.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.voiddog.mblog.R;
import org.voiddog.mblog.fragment.PhotoViewPagerFragment;
import org.voiddog.mblog.fragment.PhotoViewPagerFragment_;
import org.voiddog.mblog.ui.TitleBar;

/**
 * 照片预览页面
 * Created by Dog on 2015/5/16.
 */
@EActivity(R.layout.activity_photo_preview)
public class PhotoPreviewActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener{
    @ViewById
    TitleBar title_bar;

    @Extra
    String[] photoPaths = new String[0];
    @Extra
    boolean isLocal = false;
    @Extra
    int index = 0;

    PhotoViewPagerFragment fragment;

    @AfterViews
    void init(){
        if(photoPaths != null && fragment == null){
            fragment = new PhotoViewPagerFragment_();
            fragment.setOnPageChangeListener(this);
        }
        Bundle bundle = new Bundle();
        bundle.putStringArray("photoPaths", photoPaths);
        bundle.putBoolean("isLocal", isLocal);
        bundle.putInt("index", index);
        fragment.setArguments(bundle);
        changeFragment(fragment);
        setUpTitle();
    }

    void setUpTitle(){
        title_bar.setTitle(String.format("%d/%d", photoPaths.length == 0 ? 0 : index + 1, photoPaths.length));
        title_bar.setOnLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    void changeFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction()
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.fra_content, fragment)
                .commit();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public void onPageSelected(int position) {
        title_bar.setTitle(String.format("%d/%d", photoPaths.length == 0 ? 0 : position + 1, photoPaths.length));
    }

    @Override
    public void onPageScrollStateChanged(int state) {}
}
