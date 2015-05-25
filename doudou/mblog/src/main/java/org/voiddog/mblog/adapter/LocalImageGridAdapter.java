package org.voiddog.mblog.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.voiddog.lib.util.ToastUtil;
import org.voiddog.mblog.ui.ImageSelectGridViewItem;
import org.voiddog.mblog.ui.ImageSelectGridViewItem_;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 本地图片grid view 适配器
 * Created by Dog on 2015/5/15.
 */
public class LocalImageGridAdapter extends BaseAdapter {

    String dirPath;
    List<String> fileNameList = new ArrayList<>();
    Set<String> mSelectImages = new HashSet<>();
    Context mContext;
    int maxSelectNum = 1, itemSize = 0;
    OnSelectChangedListener selectChanged;

    /**
     * @param context 上下文
     * @param itemSize danger item的大小
     */
    public LocalImageGridAdapter(Context context, int itemSize){
        mContext = context;
        this.itemSize = itemSize;
    }

    /**
     * 绑定数据到adapter
     * @param dirPath 文件夹路径
     * @param fileNameList 文件名
     */
    public void setData(String dirPath, List<String> fileNameList, int maxSelectNum){
        this.dirPath = dirPath;
        this.fileNameList = fileNameList;
        this.maxSelectNum = maxSelectNum;
        notifyDataSetChanged();
    }

    public void setOnSelectChangedListener(OnSelectChangedListener selectChanged){
        this.selectChanged = selectChanged;
    }

    /**
     * 获取选择的集合
     */
    public Set<String> getSelectSet(){
        return mSelectImages;
    }

    @Override
    public int getCount() {
        return fileNameList.size();
    }

    @Override
    public Object getItem(int position) {
        return fileNameList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageSelectGridViewItem viewItem;
        if(convertView == null){
            viewItem = ImageSelectGridViewItem_.build(mContext);
            viewItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageSelectGridViewItem clickView = (ImageSelectGridViewItem) v;
                    if(clickView.isSelect()){
                        clickView.click();
                        if(mSelectImages.contains(clickView.getFilePath())) {
                            mSelectImages.remove(clickView.getFilePath());
                        }
                        if(selectChanged != null){
                            selectChanged.onSelectChanged(mSelectImages);
                        }
                    }
                    else if(mSelectImages.size() >= maxSelectNum){
                        ToastUtil.showToast(String.format("最多只能选择%d张", maxSelectNum));
                    }
                    else{
                        clickView.click();
                        mSelectImages.add(clickView.getFilePath());
                        if(selectChanged != null){
                            selectChanged.onSelectChanged(mSelectImages);
                        }
                    }
                }
            });
        }
        else{
            viewItem = (ImageSelectGridViewItem) convertView;
        }

        String filePath = dirPath + "/" + getItem(position);
        if(mSelectImages.contains(filePath)){
            viewItem.bindData(filePath, true, itemSize);
        }
        else{
            viewItem.bindData(filePath, false, itemSize);
        }

        return viewItem;
    }

    public static interface OnSelectChangedListener{
        void onSelectChanged(Set<String> selectSet);
    }
}
