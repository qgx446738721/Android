package org.voiddog.mblog.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.voiddog.mblog.R;
import org.voiddog.mblog.fragment.ImgLibListFolderFragment_;
import org.voiddog.mblog.fragment.ImgSelectorFragment_;
import org.voiddog.mblog.util.DialogUtil;

/**
 * 从相册中选取图片
 * Created by Dog on 2015/5/15.
 */
@EActivity(R.layout.activity_empty)
public class ChoseImgFromLibActivity extends AppCompatActivity{

    int currentPage = 0;
    //最多选择的图片数量, 默认一张
    @Extra
    int maxChoseNumber = 1;
    Dialog progressDialog;
    // 0 ImgLibListFolderFragment, 1 img select
    Fragment[] fragment = new Fragment[2];

    /**
     * 显示环形进度条
     */
    public void showProgressDialog(){
        if(!progressDialog.isShowing()){
            progressDialog.show();
        }
    }

    public void cancelProgressDialog(){
        if(progressDialog.isShowing()){
            progressDialog.cancel();
        }
    }

    /**
     * 取消选择，结束activity
     */
    public void exitActivity(){
        finish();
        overridePendingTransition(R.anim.alpha_in, R.anim.slide_out_to_bottom_slow);
    }

    /**
     * 前一页
     * @param bundle 传输给fragment的数据
     */
    public void prePage(Bundle bundle){
        if(currentPage > 0){
            currentPage--;
            switchFragment(currentPage, bundle, R.anim.slide_in_from_left, R.anim.slide_out_to_right);
        }
        else if(currentPage == 0){
            exitActivity();
        }
    }

    /**
     * 后一页
     * @param bundle 传输给fragment的数据
     */
    public void nextPage(Bundle bundle){
        if(currentPage < fragment.length){
            currentPage++;
            switchFragment(currentPage, bundle, R.anim.slide_in_from_right, R.anim.slide_out_to_left);
        }
    }

    /**
     * 最多可以选取的图片数量
     * @return 选择图片的上限
     */
    public int getMaxImageChose(){
        return maxChoseNumber;
    }

    @AfterViews
    void init(){
        progressDialog = DialogUtil.createProgressDialog(this);

        switchFragment(0, null, 0, 0);
        currentPage = 0;
    }

    /**
     * 却换fragment
     * @param index 0 文件夹选择，1 图片选择
     * @param bundle 要传递的参数
     * @param animIn fragment进入动画 0 为FADE
     * @param animOut fragment退出动画 0 为 FADE
     */
    void switchFragment(int index, Bundle bundle, int animIn, int animOut){
        if(fragment[index] == null){
            switch (index){
                case 0:{
                    fragment[index] = new ImgLibListFolderFragment_();
                    break;
                }
                case 1:{
                    fragment[index] = new ImgSelectorFragment_();
                    break;
                }
            }
        }
        if(bundle != null){
            fragment[index].setArguments(bundle);
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if(animIn != 0 && animOut != 0){
            transaction.setCustomAnimations(animIn, animOut);
        }
        else{
            transaction.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        }
        transaction.replace(R.id.fra_content, fragment[index])
                .commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            prePage(null);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}