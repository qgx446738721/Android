package org.voiddog.mblog.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.util.Size;
import android.view.View;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.voiddog.lib.util.ImageUtil;
import org.voiddog.lib.util.SizeUtil;
import org.voiddog.lib.util.ToastUtil;
import org.voiddog.mblog.Const;
import org.voiddog.mblog.R;
import org.voiddog.mblog.ui.ClipImageLayout;
import org.voiddog.mblog.ui.TitleBar;
import org.voiddog.mblog.util.DDFileUtil;

/**
 * 图片裁剪
 * Created by Dog on 2015/5/16.
 */
@EActivity(R.layout.activity_crop)
public class CropActivity extends AppCompatActivity {
    static public final int CROP_SUCCESS = 1;
    static public final int CROP_FAILURE = 0;

    @ViewById
    TitleBar title_bar;
    @ViewById
    ClipImageLayout cil_crop;

    @Extra
    String filePath = "";

    Bitmap mBitmap;

    @AfterViews
    void init(){
        setUpTitle();
        loadBitmap();
    }

    @Background
    void loadBitmap(){
        mBitmap = ImageUtil.getLocalBitmap(filePath, SizeUtil.getScreenWidth(), SizeUtil.getScreenHeight());
        if(mBitmap == null){
            ToastUtil.showToast("没有找到图片");
            return;
        }
        setCropBitmap(mBitmap);
    }

    @UiThread
    void setCropBitmap(Bitmap bitmap){
        cil_crop.setBitmap(bitmap);
    }

    void setUpTitle(){
        title_bar.setOnLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.alpha_in, R.anim.slide_out_to_bottom);
            }
        });
        title_bar.setOnRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropImage();
            }
        });
    }

    /**
     * 裁剪图片并返回
     */
    @Background
    void cropImage(){
        String path = null;
        if(mBitmap != null) {
            Bitmap bitmap = cil_crop.clip();
            path = DDFileUtil.getLocalStorePath("tmp") + System.currentTimeMillis() + ".jpg";
            if(DDFileUtil.saveImg(bitmap, path,
                    Const.MAX_SIZE, Const.MAX_SIZE, 100) == null){
                path = null;
            }
            if(!bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
        onCropImageComplete(path);
    }

    @UiThread
    void onCropImageComplete(String path){
        if(path != null){
            Intent intent = new Intent();
            intent.putExtra("path", path);
            setResult(CROP_SUCCESS, intent);
        }
        else{
            setResult(CROP_FAILURE);
        }
        finish();
        overridePendingTransition(R.anim.alpha_in, R.anim.slide_out_to_bottom);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if(mBitmap != null && !mBitmap.isRecycled()) {
            mBitmap.recycle();
            System.gc();
            mBitmap = null;
        }
    }
}
