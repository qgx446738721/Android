package org.voiddog.mblog.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.BasePostprocessor;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.facebook.imagepipeline.request.Postprocessor;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.voiddog.lib.ui.CustomFontTextView;
import org.voiddog.lib.util.ImageUtil;
import org.voiddog.lib.util.SizeUtil;
import org.voiddog.mblog.MyApplication;
import org.voiddog.mblog.R;
import org.voiddog.mblog.fragment.MainListFragment_;
import org.voiddog.mblog.preference.Config_;
import org.voiddog.mblog.util.DDImageUtil;

@EActivity(R.layout.activity_main)
@OptionsMenu(R.menu.menu_main)
public class MainActivity extends AppCompatActivity {
    final int REQUEST_AUTH = 0;
    final int REQUEST_UPLOAD = 1;

    @ViewById
    Toolbar tool_bar;
    @ViewById
    DrawerLayout dl_main;
    @ViewById
    CustomFontTextView ct_tv_home, ct_tv_user_name, ct_tv_auth;
    @ViewById
    SimpleDraweeView sdv_iv_user_head;
    @ViewById
    ImageView iv_blur_bg;
    @ViewById
    LinearLayout ll_main_list;
    @Pref
    Config_ config;

    private MainActivity mainActivity;
    private View activeView = null;
    int defaultColor, activeColor;
    //判断用户是否登录
    boolean isUserLogin = false;

    @AfterViews
    void init(){
        //一体化色彩
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        mainActivity = this;

        tool_bar.setTitle("Home");
        tool_bar.setTitleTextColor(getResources().getColor(R.color.title_text));
        setSupportActionBar(tool_bar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        //创建返回键，并实现打开关/闭监听
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, dl_main, tool_bar, R.string.open, R.string.close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        mDrawerToggle.syncState();
        dl_main.setDrawerListener(mDrawerToggle);

        setUpLeftListMenu();
        setUpContent();
        autoLogin();
    }

    void setUpLeftListMenu(){
        defaultColor = getResources().getColor(R.color.main_list_txt_normal);
        activeColor = getResources().getColor(R.color.main_list_txt_pressed);
        ct_tv_home.performClick();
    }

    @Click({R.id.ct_tv_home, R.id.ct_tv_hot, R.id.ct_tv_timeline, R.id.ct_tv_settings,
    R.id.ct_tv_auth, R.id.ll_main_list})
    void onMenuClick(View view){
        switch (view.getId()){
            case R.id.ct_tv_home:{
                activeView(view);
                break;
            }
            case R.id.ct_tv_hot:{
                activeView(view);
            }
            case R.id.ct_tv_timeline:{
                activeView(view);
                break;
            }
            case R.id.ct_tv_settings:{
                break;
            }
            case R.id.ct_tv_auth:{
                if(ct_tv_auth.getText().toString().equals("LOGIN")){
                    LoginOrRegisterActivity_.intent(mainActivity).startForResult(REQUEST_AUTH);
                }
                else{
                    logout();
                }
                break;
            }
        }
    }

    @OptionsItem(R.id.menu_camera)
    void onTakePhoto(){
        UserBlogActivity_.intent(this)
                .extra("tEmail", "634771197@qq.com")
                .start();
    }

    @OptionsItem(R.id.menu_chose)
    void onChoseFromLibrary(){
        ChoseImgFromLibActivity_.intent(this)
                .extra("maxChoseNumber", 1)
                .startForResult(REQUEST_UPLOAD);
    }

    void activeView(View view){
        if(activeView != null){
            activeView.setBackgroundColor(defaultColor);
        }
        view.setBackgroundColor(activeColor);
        activeView = view;
    }

    void setUpContent(){
        MainListFragment_ fragment = new MainListFragment_();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.ll_main_fragment, fragment)
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    /**
     * 登出操作
     */
    void logout(){
        config.clear();
        clearInfo();
        isUserLogin = false;
    }

    /**
     * 获取最新的用户信息
     */
    void getUserInfo(){
        // TODO 网络登录 访问 获取用户信息
    }

    /**
     * 判断是否自动登录
     */
    void autoLogin(){
        boolean isAutoLogin = config.auto_login().get();
        if(isAutoLogin) {
            isUserLogin = true;
            loadProfile(config.head().get(), config.nickname().get());
        }
        else{
            isUserLogin = false;
            clearInfo();
        }
    }

    void clearInfo(){
        ct_tv_user_name.setText("NOT LOGIN");
        ct_tv_auth.setText("LOGIN");
        iv_blur_bg.setVisibility(View.INVISIBLE);
        sdv_iv_user_head.setImageURI(Uri.parse("android.resource://"+getPackageName()+"/"+R.mipmap.no_head));
        ll_main_list.setBackgroundResource(R.color.transparent);
    }

    void loadProfile(String head, String name){
        ct_tv_auth.setText("LOGOUT");
        name = name.toUpperCase();
        ct_tv_user_name.setText(name);
        Uri uri = MyApplication.getImageHostUri(head);
        Postprocessor postprocessor = new BasePostprocessor() {
            @Override
            public String getName() {
                return "postprocessor";
            }

            @Override
            public void process(Bitmap bitmap) {
                startBlur(bitmap);
            }
        };
        int size = getResources().getDimensionPixelSize(R.dimen.main_menu_head_img_size);
        sdv_iv_user_head.setController(
                DDImageUtil.getControllerBySize(sdv_iv_user_head, uri, size, size, postprocessor)
        );
    }

    @Background
    void startBlur(Bitmap bitmap){
        Bitmap newBitmap = ImageUtil.getBlurImage(bitmap, 2, 8);
        applyBlur(newBitmap);
    }

    @UiThread
    void applyBlur(Bitmap bitmap){
        ll_main_list.setBackgroundResource(R.color.main_list_bg);
        iv_blur_bg.setImageBitmap(bitmap);
        iv_blur_bg.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_AUTH){
            if(resultCode == LoginOrRegisterActivity.LOGIN_SUCCESS){
                isUserLogin = true;
                loadProfile(data.getStringExtra("head"), data.getStringExtra("nickname"));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
