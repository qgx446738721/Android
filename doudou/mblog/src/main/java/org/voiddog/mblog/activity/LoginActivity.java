package org.voiddog.mblog.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.gson.reflect.TypeToken;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.voiddog.lib.http.DJsonObjectResponse;
import org.voiddog.lib.http.HttpNetWork;
import org.voiddog.lib.util.StringUtil;
import org.voiddog.lib.util.ToastUtil;
import org.voiddog.mblog.R;
import org.voiddog.mblog.http.HttpStruct;
import org.voiddog.mblog.http.MyHttpRequest;
import org.voiddog.mblog.preference.Config_;
import org.voiddog.mblog.ui.TitleBar;
import org.voiddog.mblog.util.DialogUtil;

/**
 * 登录界面
 * Created by Dog on 2015/5/2.
 */
@EActivity(R.layout.activity_login)
public class LoginActivity extends Activity {
    @ViewById
    TitleBar title_bar;
    @ViewById
    EditText et_email, et_password;
    @Pref
    Config_ config;

    public static int LOGIN_SUCCESS = 1;

    @AfterViews
    void init(){
        setUpTitle();
    }

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
                startLogin();
            }
        });
    }

    /**
     * 开始登录
     */
    void startLogin(){
        String email = et_email.getText().toString();
        String password = et_password.getText().toString();
        if(!checkInput(email, password)){
            return;
        }
        MyHttpRequest.UserLogin userLogin = new MyHttpRequest.UserLogin(email, password);
        final Dialog dialog = DialogUtil.createProgressDialog(LoginActivity.this);
        dialog.show();
        dialog.setCancelable(false);
        HttpNetWork.getInstance().request(userLogin, new DJsonObjectResponse() {
            @Override
            public void onSuccess(int statusCode, DResponse response) {
                dialog.cancel();
                if(response.code == 0){
                    ToastUtil.showToast("login success");
                    Log.i("TAG", response.data);
                    try {
                        HttpStruct.User user = response.getData(new TypeToken<HttpStruct.User>() {
                        }.getType());
                        Intent intent = new Intent();
                        intent.putExtra("uid", user.uid);
                        intent.putExtra("nickname", user.nickname);
                        intent.putExtra("head", user.head);
                        config.edit()
                                .auto_login().put(true)
                                .user_name().put(user.nickname)
                                .head_image().put(user.head).apply();
                        setResult(LOGIN_SUCCESS, intent);
                        finish();
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
                else{
                    ToastUtil.showToast(response.message);
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable throwable) {
                dialog.cancel();
                ToastUtil.showToast("login failure, network error. error code: " + statusCode);
            }
        });
    }

    boolean checkInput(String email, String password){
        if(!StringUtil.isEmail(email)){
            ToastUtil.showToast("email address invalid");
            return false;
        }
        if(StringUtil.isEmpty(password)){
            ToastUtil.showToast("password empty!");
            return false;
        }
        return true;
    }
}
