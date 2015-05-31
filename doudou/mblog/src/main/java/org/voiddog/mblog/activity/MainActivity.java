package org.voiddog.mblog.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.BasePostprocessor;
import com.facebook.imagepipeline.request.Postprocessor;
import com.google.gson.reflect.TypeToken;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.voiddog.lib.http.DHttpRequestBase;
import org.voiddog.lib.http.HttpNetWork;
import org.voiddog.lib.http.HttpResponsePacket;
import org.voiddog.lib.ui.CustomFontTextView;
import org.voiddog.lib.util.ImageUtil;
import org.voiddog.lib.util.ToastUtil;
import org.voiddog.mblog.MyApplication;
import org.voiddog.mblog.R;
import org.voiddog.mblog.data.GetUserInfoResponseData;
import org.voiddog.mblog.db.model.UserModel;
import org.voiddog.mblog.fragment.MainListAttentionFragment;
import org.voiddog.mblog.fragment.MainListAttentionFragment_;
import org.voiddog.mblog.fragment.MainListFragment;
import org.voiddog.mblog.fragment.MainListFragment_;
import org.voiddog.mblog.fragment.TakeOrChoseDialogFragment;
import org.voiddog.mblog.http.GetUserInfoRequest;
import org.voiddog.mblog.preference.Config_;
import org.voiddog.mblog.util.DDImageUtil;

import java.sql.SQLException;

@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {
    final int REQUEST_AUTH = 0;

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
    TakeOrChoseDialogFragment dialogFragment;
    MainListFragment mainListFragment;
    MainListAttentionFragment mainListAttentionFragment;

    @AfterViews
    void init(){
        Log.i("TAG", "After Views");
        //一体化色彩
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        mainActivity = this;
        dialogFragment = new TakeOrChoseDialogFragment();

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

        setUpContent();
        setUpLeftListMenu();
        autoLogin();
    }

    void setUpLeftListMenu(){
        defaultColor = getResources().getColor(R.color.main_list_txt_normal);
        activeColor = getResources().getColor(R.color.main_list_txt_pressed);
        ct_tv_home.performClick();
    }

    @Click({R.id.ct_tv_home, R.id.ct_tv_attention, R.id.ct_tv_settings, R.id.sdv_iv_user_head,
    R.id.ct_tv_auth, R.id.ll_main_list})
    void onMenuClick(View view){
        dl_main.closeDrawers();
        switch (view.getId()){
            case R.id.ct_tv_home:{
                activeView(view);
                changeFragment(mainListFragment);
                break;
            }
            case R.id.ct_tv_attention:{
                if(isUserLogin && config.email().exists()) {
                    activeView(view);
                    changeFragment(mainListAttentionFragment);
                }
                else{
                    jumpToLogin();
                }
                break;
            }
            case R.id.sdv_iv_user_head:{
                if(isUserLogin && config.email().exists()) {
                    UserBlogActivity_.intent(this)
                            .tEmail(config.email().get())
                            .start();
                }
                else{
                    jumpToLogin();
                }
                break;
            }
            case R.id.ct_tv_settings:{
                if(isUserLogin && config.email().exists()){
                    // TODO 去设置页面
                }
                else{
                    jumpToLogin();
                }
                break;
            }
            case R.id.ct_tv_auth:{
                if(ct_tv_auth.getText().toString().equals("LOGIN")){
                    jumpToLogin();
                }
                else{
                    logout();
                }
                break;
            }
        }
    }

    @Click(R.id.cmb_plus)
    void onCircleMenuClick(){
        dialogFragment.show(getSupportFragmentManager(), "拍照或者选择图片");
    }

    void activeView(View view){
        if(activeView != null){
            activeView.setBackgroundColor(defaultColor);
        }
        view.setBackgroundColor(activeColor);
        activeView = view;
    }

    void setUpContent(){
        mainListFragment = new MainListFragment_();
        mainListAttentionFragment = new MainListAttentionFragment_();
    }

    void changeFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.alpha_in, R.anim.alpha_out)
                .replace(R.id.ll_main_fragment, fragment)
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    void jumpToLogin(){
        LoginOrRegisterActivity_.intent(mainActivity).startForResult(REQUEST_AUTH);
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
        GetUserInfoRequest request = new GetUserInfoRequest();
        request.email = config.email().get();
        HttpNetWork.getInstance().request(request, new HttpNetWork.NetResponseCallback() {
            @Override
            public void onResponse(DHttpRequestBase request, HttpResponsePacket response) {
                if(response.code == 0) {
                    try {
                        GetUserInfoResponseData userData = response.getData(new TypeToken<GetUserInfoResponseData>(){}.getType());
                        UserModel.Dao().createOrUpdate(userData.info);
                        config.edit()
                                .sex().put(userData.info.sex)
                                .nickname().put(userData.info.nickname)
                                .head().put(userData.info.head)
                                .apply();
                        loadProfile(userData.info.head, userData.info.nickname);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else{
                    ToastUtil.showToast(response.message);
                }
            }
        }, new HttpNetWork.NetErrorCallback() {
            @Override
            public void onError(DHttpRequestBase request, String errorMsg) {
                ToastUtil.showToast("网络错误，无法获取用户信息");
            }
        });
    }

    /**
     * 判断是否自动登录
     */
    void autoLogin(){
        boolean isAutoLogin = config.auto_login().get();
        if(isAutoLogin && config.email().exists()) {
            isUserLogin = true;
            loadProfile(config.head().get(), config.nickname().get());
            getUserInfo();
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
