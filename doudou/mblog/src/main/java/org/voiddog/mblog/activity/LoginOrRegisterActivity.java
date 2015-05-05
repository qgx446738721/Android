package org.voiddog.mblog.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.facebook.drawee.view.SimpleDraweeView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.voiddog.lib.util.ImageUtil;
import org.voiddog.mblog.R;
import org.voiddog.mblog.ui.TitleBar;

/**
 * 选择登录还是注册
 * Created by Dog on 2015/5/2.
 */
@EActivity(R.layout.active_login_or_register)
public class LoginOrRegisterActivity extends Activity {
    @ViewById
    SimpleDraweeView sdv_login_or_register_head;
    @ViewById
    TitleBar title_bar;
    @ViewById
    ImageView iv_blur_bg;

    final int REQUEST_LOGIN = 0;

    @AfterViews
    void init(){
        //一体化色彩
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        setUpTitle();
        startBlur();
        sdv_login_or_register_head.setAspectRatio(1.33f);
    }

    void setUpTitle(){
        title_bar.setOnLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Click({R.id.ct_tv_login, R.id.ct_tv_register})
    void onStartClick(View view){
        switch (view.getId()){
            case R.id.ct_tv_login:{
                LoginActivity_.intent(LoginOrRegisterActivity.this).startForResult(REQUEST_LOGIN);
                break;
            }
            case R.id.ct_tv_register:{
                // TODO 注册
                break;
            }
        }
    }

    @Background
    void startBlur(){
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.login_bg);
        Bitmap newBitmap = ImageUtil.getBlurImage(bitmap);
        setBlur(newBitmap);
        bitmap.recycle();
    }

    @UiThread
    void setBlur(Bitmap blur){
        iv_blur_bg.setImageBitmap(blur);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_LOGIN && resultCode == LoginActivity.LOGIN_SUCCESS){
            setResult(resultCode, data);
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
