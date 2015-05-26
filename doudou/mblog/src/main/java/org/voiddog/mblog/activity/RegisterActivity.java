package org.voiddog.mblog.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;
import org.voiddog.mblog.R;
import org.voiddog.mblog.fragment.RegisterStep1Fragment_;
import org.voiddog.mblog.fragment.RegisterStep2Fragment;
import org.voiddog.mblog.fragment.RegisterStep2Fragment_;
import org.voiddog.mblog.ui.TitleBar;
import org.voiddog.mblog.util.DialogUtil;

/**
 * 注册页面
 * Created by Dog on 2015/5/12.
 */
@EActivity(R.layout.activity_empty)
public class RegisterActivity extends AppCompatActivity {
    final public static int REGISTER_SUCCESS = 2;
    final public static int REQUEST_TAKE_OR_SELECT = 3;
    final public int REQUEST_CROP_PHOTO = 4;

    int currentPage = 0;
    Fragment[] fragments = new Fragment[2];
    Dialog progressDialog;

    //数据区
    public String email, password;

    @AfterViews
    void init(){
        //一体化色彩
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        progressDialog = DialogUtil.createProgressDialog(this);
        changeToFragmentByIndex(0, -1, -1);
    }

    /**
     * 显示圆形进度条
     */
    public void showDialog(){
        if(!progressDialog.isShowing()){
            progressDialog.show();
        }
    }

    /**
     * 隐藏圆形进度条
     */
    public void hideDialog(){
        if(progressDialog.isShowing()){
            progressDialog.cancel();
        }
    }

    /**
     * 前一步
     */
    public void nextPage(){
        if(currentPage < fragments.length){
            currentPage++;
            changeToFragmentByIndex(currentPage, R.anim.slide_in_from_right, R.anim.slide_out_to_left);
        }
    }

    /**
     * 下一步
     */
    public void prePage(){
        if(currentPage > 0){
            currentPage--;
            changeToFragmentByIndex(currentPage, R.anim.slide_in_from_left, R.anim.slide_out_to_right);
        }
        else{
            finish();
        }
    }

    @OnActivityResult(REQUEST_TAKE_OR_SELECT)
    void onGetPhoto(int result, Intent data){
        if(result == RESULT_OK){
            String[] paths = data.getStringArrayExtra("paths");
            CropActivity_.intent(this)
                    .extra("filePath", paths[0])
                    .startForResult(REQUEST_CROP_PHOTO);
            overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.alpha_out);
        }
    }

    @OnActivityResult(REQUEST_CROP_PHOTO)
    void onCropPhoto(int result, Intent data){
        if(result == CropActivity.CROP_SUCCESS){
            String path = data.getStringExtra("path");
            ((RegisterStep2Fragment) fragments[1]).setImageHeadPath(path);
        }
    }

    /**
     * 却换fragment
     * @param index fragment的标号
     * @param customAnimIn 自定义进入动画效果，-1代表FADE，直接显示没有动画效果
     * @param customAnimOut 自定义退出动画效果，-1代表FADE，直接显示没有动画效果
     */
    void changeToFragmentByIndex(int index, int customAnimIn, int customAnimOut){
        if(index < 0 || index >= fragments.length){
            return;
        }
        if(fragments[index] == null){
            switch (index){
                case 0:{
                    fragments[0] = RegisterStep1Fragment_.builder().build();
                    break;
                }
                case 1:{
                    fragments[1] = RegisterStep2Fragment_.builder().build();
                    break;
                }
            }
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if(customAnimIn != -1 && customAnimOut != -1){
            transaction.setCustomAnimations(customAnimIn, customAnimOut);
        }
        transaction.replace(R.id.fra_content, fragments[index]);
        transaction.commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            prePage();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
