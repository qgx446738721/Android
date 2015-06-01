package org.voiddog.mblog.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;

import com.google.gson.reflect.TypeToken;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.voiddog.lib.http.DHttpRequestBase;
import org.voiddog.lib.http.HttpNetWork;
import org.voiddog.lib.http.HttpResponsePacket;
import org.voiddog.lib.util.StringUtil;
import org.voiddog.lib.util.ToastUtil;
import org.voiddog.mblog.R;
import org.voiddog.mblog.data.UserData;
import org.voiddog.mblog.db.model.UserModel;
import org.voiddog.mblog.http.LoginRequest;
import org.voiddog.mblog.preference.Config_;
import org.voiddog.mblog.receiver.UpdateUserInfoReceiver;
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
        final Dialog dialog = DialogUtil.createProgressDialog(LoginActivity.this);
        dialog.show();
        dialog.setCancelable(false);
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.email = email;
        loginRequest.password = password;
        HttpNetWork.getInstance().request(loginRequest, new HttpNetWork.NetResponseCallback() {
            @Override
            public void onResponse(DHttpRequestBase request, HttpResponsePacket response) {
                dialog.cancel();
                if(response.code == 0){
                    UserModel user = response.getData(
                            new TypeToken<UserModel>(){}.getType()
                    );
                    config.edit()
                            .email().put(user.email)
                            .head().put(user.head)
                            .moving_num().put(user.moving_num)
                            .nickname().put(user.nickname)
                            .sex().put(user.sex)
                            .age().put(user.age)
                            .auto_login().put(true)
                            .apply();
                    Intent intent = new Intent();
                    intent.setAction(UpdateUserInfoReceiver.UPDATE_USER_INFO);
                    intent.putExtra("COMMAND", UpdateUserInfoReceiver.NEED_UPDATE);
                    sendBroadcast(intent);
                    setResult(LOGIN_SUCCESS, null);
                    finish();
                }
                else{
                    ToastUtil.showToast(response.message);
                }
            }
        }, new HttpNetWork.NetErrorCallback() {
            @Override
            public void onError(DHttpRequestBase request, String errorMsg) {
                dialog.cancel();
                ToastUtil.showToast("登陆失败");
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
