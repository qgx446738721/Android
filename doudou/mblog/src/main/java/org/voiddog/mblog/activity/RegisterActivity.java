package org.voiddog.mblog.activity;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.voiddog.mblog.R;
import org.voiddog.mblog.ui.TitleBar;

/**
 * 注册页面
 * Created by Dog on 2015/5/12.
 */
@EActivity(R.layout.activity_register)
public class RegisterActivity extends AppCompatActivity {
    final public static int REGISTER_SUCCESS = 2;
    @ViewById
    TitleBar title_bar;

    @AfterViews
    void init(){
        //一体化色彩
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        setUpTitle();
    }

    /**
     * 设置头部bar
     */
    void setUpTitle(){
        title_bar.setOnLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        title_bar.setOnRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO 请求注册
            }
        });
    }
}
