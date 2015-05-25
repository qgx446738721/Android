package org.voiddog.mblog.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.voiddog.mblog.data.ImageFolder;
import org.voiddog.mblog.ui.ImageFolderListViewItem;
import org.voiddog.mblog.ui.ImageFolderListViewItem_;

import java.util.ArrayList;
import java.util.List;

/**
 * 图片文件夹适配器
 * Created by Dog on 2015/5/15.
 */
public class ImageFolderAdapter extends BaseAdapter {

    List<ImageFolder> mImageFolders = new ArrayList<>();
    Context mContext;

    public ImageFolderAdapter(Context context){
        mContext = context;
    }

    public void setmImageFolders(List<ImageFolder> imageFolders){
        mImageFolders = imageFolders;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mImageFolders.size();
    }

    @Override
    public Object getItem(int position) {
        return mImageFolders.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageFolderListViewItem viewItem;
        if(convertView == null){
            viewItem = ImageFolderListViewItem_.build(mContext);
        }
        else{
            viewItem = (ImageFolderListViewItem) convertView;
        }

        viewItem.bindData((ImageFolder) getItem(position));

        return viewItem;
    }
}
