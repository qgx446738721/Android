package org.voiddog.mblog.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;

import org.voiddog.lib.util.ImageUtil;
import org.voiddog.lib.util.SizeUtil;
import org.voiddog.lib.util.StringUtil;
import org.voiddog.mblog.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 图片预览适配器
 * Created by Dog on 2015/5/16.
 */
public class PhotoViewPagerAdapter extends PagerAdapter {

    List<View> mPhotoList = new ArrayList<>();
    Context mContext;

    public PhotoViewPagerAdapter(Context context, String[] imagePaths, boolean isLocal){
        mContext = context;
        setImagePath(imagePaths, isLocal);
    }

    void setImagePath(String[] imagePaths, boolean isLocal){
        mPhotoList.clear();
        for (String imagePath : imagePaths) {
                SimpleDraweeView draweeView = (SimpleDraweeView) View.inflate(mContext, R.layout.ui_photo_loader_view, null);
                Uri uri;
            if(isLocal){
                uri = StringUtil.getUriFromFilePath(imagePath);
            } else {
                uri = Uri.parse(imagePath);
            }
                draweeView.setController(ImageUtil.getControllerWithSize(draweeView.getController(),
                        uri, SizeUtil.getScreenWidth() >> 1, SizeUtil.getScreenHeight() >> 1));
            mPhotoList.add(draweeView);
        }
    }

    @Override
    public int getCount() {
        return mPhotoList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mPhotoList.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(mPhotoList.get(position));
        return mPhotoList.get(position);
    }
}
