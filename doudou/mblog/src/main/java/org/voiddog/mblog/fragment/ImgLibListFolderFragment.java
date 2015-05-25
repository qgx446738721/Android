package org.voiddog.mblog.fragment;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.View;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.voiddog.lib.ui.BounceListView;
import org.voiddog.lib.util.FileUtil;
import org.voiddog.lib.util.ToastUtil;
import org.voiddog.mblog.R;
import org.voiddog.mblog.activity.ChoseImgFromLibActivity;
import org.voiddog.mblog.adapter.ImageFolderAdapter;
import org.voiddog.mblog.data.ImageFolder;
import org.voiddog.mblog.ui.TitleBar;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

/**
 * 选取相册
 * Created by Dog on 2015/5/15.
 */
@EFragment(R.layout.fragment_img_folder_list)
public class ImgLibListFolderFragment extends Fragment {
    @ViewById
    TitleBar title_bar;
    @ViewById
    BounceListView bou_lv_folder;

    ChoseImgFromLibActivity mActivity;
    List<ImageFolder> mImageFolders = new ArrayList<>();
    /**
     * 临时的辅助类，用于防止同一个文件夹的多次扫描
     */
    HashSet<String> mDirPaths = new HashSet<>();
    //适配器
    ImageFolderAdapter mImageFolderAdapter;


    @AfterViews
    void init(){
        mActivity = (ChoseImgFromLibActivity) getActivity();
        if(mImageFolderAdapter == null){
            mImageFolderAdapter = new ImageFolderAdapter(mActivity);
        }

        setUpTitle();
        if(mImageFolders.size() > 0){
            //已经扫过了
            onScanImgFinished();
        }
        else {
            getImages();
        }
    }

    /**
     * 设置头部 bar
     */
    void setUpTitle(){
        title_bar.setOnRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.exitActivity();
            }
        });
    }
    /**
     * 利用ContentProvider扫描手机中的图片，扫描运行在子线程中 完成图片的扫描，最终获得jpg最多的那个文件夹
     */
    void getImages(){
        if(!FileUtil.isExternalStorageExist()){
            ToastUtil.showToast("暂无外部存储");
            mActivity.exitActivity();
        }
        //显示进度条
        mActivity.showProgressDialog();
        scanImage();
    }

    @ItemClick(R.id.bou_lv_folder)
    void onFolderClick(ImageFolder imageFolder){
        Bundle bundle = new Bundle();
        bundle.putString("folderName", imageFolder.getDir());
        mActivity.nextPage(bundle);
    }

    /**
     * 扫描图片
     */
    @Background
    void scanImage(){
        String firstImage = null;

        Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver mContentResolver = mActivity.getContentResolver();

        // 只查询jpeg和png的图片
        Cursor mCursor = mContentResolver.query(mImageUri, null,
                MediaStore.Images.Media.MIME_TYPE + "=? or "
                        + MediaStore.Images.Media.MIME_TYPE + "=?",
                new String[] {"image/jpeg"},
                MediaStore.Images.Media.DATE_MODIFIED);
        while(mCursor.moveToNext()){
            // 获取图片的路径
            String path = mCursor.getString(mCursor
                    .getColumnIndex(MediaStore.Images.Media.DATA));

            // 拿到第一张图片的路径
            if (firstImage == null)
                firstImage = path;
            // 获取该图片的父路径名
            File parentFile = new File(path).getParentFile();
            if (parentFile == null)
                continue;
            String dirPath = parentFile.getAbsolutePath();
            ImageFolder imageFolder;
            // 利用一个HashSet防止多次扫描同一个文件夹（不加这个判断，图片多起来还是相当恐怖的
            if (mDirPaths.contains(dirPath)){
                continue;
            }
            else{
                mDirPaths.add(dirPath);
                // 初始化image Folder
                imageFolder = new ImageFolder();
                imageFolder.setDir(dirPath);
                imageFolder.setFirstImagePath(path);
            }

            int picSize = parentFile.list(new FilenameFilter(){
                @Override
                public boolean accept(File dir, String filename){
                    return filename.endsWith(".jpg")
                            || filename.endsWith(".jpeg");
                }
            }).length;

            imageFolder.setCount(picSize);
            mImageFolders.add(imageFolder);
        }
        mCursor.close();
        Collections.sort(mImageFolders, new CompareFolder());
        onScanImgFinished();
    }

    /**
     * 扫描图片结束
     */
    @UiThread
    void onScanImgFinished(){
        mActivity.cancelProgressDialog();
        mImageFolderAdapter.setmImageFolders(mImageFolders);
        bou_lv_folder.setAdapter(mImageFolderAdapter);
    }

    /**
     * 文件夹根据图片数排序
     */
    class CompareFolder implements Comparator<ImageFolder> {

        @Override
        public int compare(ImageFolder lhs, ImageFolder rhs) {
            if(lhs.getCount() == rhs.getCount()){
                return 0;
            }
            else if(lhs.getCount() < rhs.getCount()){
                return  1;
            }
            return -1;
        }
    }
}
