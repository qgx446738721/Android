package org.voiddog.mblog.fragment;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.voiddog.lib.ui.BounceGridView;
import org.voiddog.lib.util.SizeUtil;
import org.voiddog.mblog.R;
import org.voiddog.mblog.activity.ChoseImgFromLibActivity;
import org.voiddog.mblog.activity.PhotoPreviewActivity_;
import org.voiddog.mblog.adapter.LocalImageGridAdapter;
import org.voiddog.mblog.ui.TitleBar;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * 图片选择
 * Created by Dog on 2015/5/15.
 */
@EFragment(R.layout.fragment_img_select)
public class ImgSelectorFragment extends Fragment {
    @ViewById
    TitleBar title_bar;
    @ViewById
    BounceGridView bou_gv_image;
    @ViewById
    Button btn_preview;

    LocalImageGridAdapter mAdapter;
    String mFolderName;
    File mImgDir;
    List<String> mImgs;
    ChoseImgFromLibActivity mActivity;

    @AfterViews
    void init(){
        mActivity = (ChoseImgFromLibActivity) getActivity();
        if(mAdapter == null) {
            mAdapter = new LocalImageGridAdapter(mActivity,
                    SizeUtil.getScreenWidth() >> 2);
        }
        mFolderName = getArguments().getString("folderName", "");
        mImgDir = new File(mFolderName);

        bou_gv_image.setAdapter(mAdapter);

        if(mImgDir.exists() && mImgDir.isDirectory()){
            mActivity.showProgressDialog();
            scanImage();
        }

        setUpTitle();
        setUpSelectListener();
    }

    @Click({R.id.btn_preview, R.id.tv_sure})
    void onBtnClick(View view){
        switch (view.getId()){
            case R.id.btn_preview:{
                String[] paths = new String[mAdapter.getSelectSet().size()];
                mAdapter.getSelectSet().toArray(paths);
                PhotoPreviewActivity_.intent(mActivity)
                        .extra("photoPaths", paths)
                        .extra("isLocal", true)
                        .start();
                break;
            }
            case R.id.tv_sure:{
                String[] paths = new String[mAdapter.getSelectSet().size()];
                mAdapter.getSelectSet().toArray(paths);
                Intent intent = new Intent();
                intent.putExtra("paths", paths);
                mActivity.setResult(Activity.RESULT_OK, intent);
                mActivity.finish();
                break;
            }
        }
    }

    /**
     * 设置头部bar
     */
    void setUpTitle(){
        title_bar.setTitle(mFolderName.substring(mFolderName.lastIndexOf("/") + 1));
        title_bar.setOnLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.prePage(null);
            }
        });
        title_bar.setOnRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.exitActivity();
            }
        });
    }

    /**
     * 设置选择更改监听事件
     */
    void setUpSelectListener(){
        if(mAdapter.getSelectSet().size() > 0){
            enabledPreviewBtn();
            btn_preview.setText(String.format("预览(%d)",
                    mAdapter.getSelectSet().size()));
        }
        else{
            disabledPreviewBtn();
        }

        mAdapter.setOnSelectChangedListener(new LocalImageGridAdapter.OnSelectChangedListener() {
            @Override
            public void onSelectChanged(Set<String> selectSet) {
                if(selectSet.size() == 0){
                    disabledPreviewBtn();
                }
                else {
                    enabledPreviewBtn();
                    btn_preview.setText(String.format("预览(%d)", selectSet.size()));
                }
            }
        });
    }

    void disabledPreviewBtn(){
        btn_preview.setBackgroundResource(R.drawable.btn_blue_bg_disable);
        btn_preview.setTextColor(getResources().getColor(R.color.white));
        btn_preview.setEnabled(false);
        btn_preview.setText("预览");
    }

    void enabledPreviewBtn(){
        btn_preview.setBackgroundResource(R.drawable.btn_blue_bg);
        btn_preview.setEnabled(true);
    }

    /**
     * 扫描图片并设置数据到grid view
     */
    @Background
    void scanImage(){
        mImgs = Arrays.asList(mImgDir.list(new FilenameFilter(){
            @Override
            public boolean accept(File dir, String filename){
                return filename.endsWith(".jpg") ||
                        filename.endsWith(".jpeg");
            }
        }));
        scanImageFinished();
    }

    @UiThread
    void scanImageFinished(){
        mActivity.cancelProgressDialog();
        mAdapter.setData(mFolderName, mImgs, mActivity.getMaxImageChose());
    }
}
