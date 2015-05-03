package org.voiddog.mblog.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.voiddog.lib.http.DJsonObjectResponse;
import org.voiddog.lib.http.HttpNetWork;
import org.voiddog.lib.ui.CircleNetworkImageView;
import org.voiddog.lib.ui.CustomFontTextView;
import org.voiddog.lib.util.ImageCacheManager;
import org.voiddog.lib.util.ImageUtil;
import org.voiddog.lib.util.SizeUtil;
import org.voiddog.lib.util.ToastUtil;
import org.voiddog.mblog.Const;
import org.voiddog.mblog.MyApplication;
import org.voiddog.mblog.R;
import org.voiddog.mblog.fragment.MainListFragment_;
import org.voiddog.mblog.http.MyHttpRequest;
import org.voiddog.mblog.preference.Config_;
import org.voiddog.mblog.util.DialogUtil;

@EActivity(R.layout.activity_main)
@OptionsMenu(R.menu.menu_main)
public class MainActivity extends ActionBarActivity {
    @ViewById
    Toolbar tool_bar;
    @ViewById
    DrawerLayout dl_main;
    @ViewById
    CustomFontTextView ct_tv_home, ct_tv_user_name, ct_tv_auth;
    @ViewById
    CircleNetworkImageView cn_iv_user_head;
    @ViewById
    ImageView iv_blur_bg;
    @Pref
    Config_ config;

    private MainActivity mainActivity;
    private View activeView = null;
    int defaultColor, activeColor;
    //判断用户是否登录
    boolean isUserLogin = false;

    final int REQUEST_AUTH = 0;

    @AfterViews
    void init(){
        //一体化色彩
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        mainActivity = this;

        tool_bar.setTitle("主页");
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
                break;
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
        final Dialog dialog = DialogUtil.createProgressDialog(mainActivity);
        dialog.show();
        dialog.setCancelable(false);
        MyHttpRequest.UserLogout userLogout = new MyHttpRequest.UserLogout();
        HttpNetWork.getInstance().request(userLogout, new DJsonObjectResponse() {
            @Override
            public void onSuccess(int statusCode, DResponse response) {
                dialog.cancel();
                if (response.code == 0) {
                    ToastUtil.showToast("logout success");
                } else if (response.code == Const.NOT_LOGIN) {
                    ToastUtil.showToast("not login");
                } else {
                    ToastUtil.showToast(response.message);
                    return;
                }
                config.clear();
                clearInfo();
                isUserLogin = false;
            }

            @Override
            public void onFailure(int statusCode, Throwable throwable) {
                dialog.cancel();
                ToastUtil.showToast("logout failure 'error code' : " + statusCode);
            }
        });
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
            loadProfile(config.head_image().get(), config.user_name().get());
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
        cn_iv_user_head.setLocalBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.no_head));
    }

    void loadProfile(String head, String name){
        ct_tv_auth.setText("LOGOUT");
        name = name.toUpperCase();
        ct_tv_user_name.setText(name);
        ImageLoader.ImageListener imageListener = new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                Bitmap bitmap = response.getBitmap();
                if(bitmap != null){
                    startBlur(bitmap);
                    cn_iv_user_head.setLocalBitmap(bitmap);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {}
        };
        ImageCacheManager.getInstacne().getImageLoader().get(MyApplication.getImageHost(head),
                imageListener, SizeUtil.getScreenWidth(), SizeUtil.getScreenHeight());
    }

    @Background
    void startBlur(Bitmap bitmap){
        Bitmap newBitmap = ImageUtil.getBlurImage(bitmap, mainActivity, 8, 2);
        applyBlur(newBitmap);
    }

    @UiThread
    void applyBlur(Bitmap bitmap){
        iv_blur_bg.setImageBitmap(bitmap);
        iv_blur_bg.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_AUTH){
            if(resultCode == LoginActivity.LOGIN_SUCCESS){
                isUserLogin = true;
                loadProfile(data.getStringExtra("head"), data.getStringExtra("nickname"));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
