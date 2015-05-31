package org.voiddog.mblog.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.UiThread;
import org.voiddog.lib.util.FileUtil;
import org.voiddog.lib.util.ToastUtil;
import org.voiddog.mblog.Const;
import org.voiddog.mblog.R;
import org.voiddog.mblog.util.DDFileUtil;

/**
 * 拍照activity
 * Created by Dog on 2015/5/30.
 */
@EActivity(R.layout.activity_take_photo)
public class TakePhotoActivity extends AppCompatActivity{
    static final int REQUEST_CODE = 0;
    String path;

    @AfterViews
    void init(){
        if(FileUtil.ExistSDCard()){
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            startActivityForResult(intent, REQUEST_CODE);
        }
        else{
            ToastUtil.showToast("没有内存卡");
            finish();
        }
    }

    @OnActivityResult(REQUEST_CODE)
    void onTakePhotoBack(int resultCode, Intent intent){
        if(resultCode == RESULT_OK){
            Uri uri = intent.getData();
            if(uri != null){
                path = FileUtil.getFilePathByUri(TakePhotoActivity.this, uri);
                onTakePhotoComplete();
            }
            else {
                progressData(intent);
            }
        }
        else{
            finish();
        }
    }

    @Background
    void progressData(Intent intent){
        try {
            Bundle bundle = intent.getExtras();
            Bitmap bitmap = (Bitmap) bundle.get("data");
            if (bitmap != null) {
                path = DDFileUtil.getLocalStorePath("tmp") + System.currentTimeMillis() + ".jpg";
                if (DDFileUtil.saveImg(bitmap, path, 100) == null) {
                    path = null;
                }
                if (!bitmap.isRecycled()) {
                    bitmap.recycle();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        onTakePhotoComplete();
    }

    @UiThread
    void onTakePhotoComplete(){
        if(path != null){
            Intent intent = new Intent();
            String[] paths = new String[1];
            paths[0] = path;
            intent.putExtra("paths", paths);
            setResult(RESULT_OK, intent);
        }
        else{
            setResult(RESULT_CANCELED);
        }
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return keyCode == KeyEvent.KEYCODE_BACK || super.onKeyDown(keyCode, event);
    }
}
