package org.voiddog.mblog.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.reflect.TypeToken;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.voiddog.lib.http.DHttpRequestBase;
import org.voiddog.lib.http.DJsonObjectResponse;
import org.voiddog.lib.http.HttpNetWork;
import org.voiddog.lib.http.HttpResponsePacket;
import org.voiddog.lib.util.KeyBoardUtils;
import org.voiddog.lib.util.StringUtil;
import org.voiddog.lib.util.ToastUtil;
import org.voiddog.mblog.Const;
import org.voiddog.mblog.R;
import org.voiddog.mblog.activity.RegisterActivity;
import org.voiddog.mblog.data.PostImgResponse;
import org.voiddog.mblog.data.UserData;
import org.voiddog.mblog.http.ImagePost;
import org.voiddog.mblog.http.RegisterRequest;
import org.voiddog.mblog.preference.Config_;
import org.voiddog.mblog.ui.TitleBar;
import org.voiddog.mblog.util.DDStringUtil;
import org.voiddog.mblog.util.DialogUtil;

/**
 * 注册第二步
 * Created by Dog on 2015/5/26.
 */
@EFragment(R.layout.fragment_register_step_2)
public class RegisterStep2Fragment extends Fragment{
    @ViewById
    TitleBar title_bar;
    @ViewById
    EditText et_user_name, et_age;
    @ViewById
    SimpleDraweeView sdv_user_head;
    @ViewById
    RadioGroup gp_sex_select;
    @Pref
    Config_ config;

    RegisterActivity activity;
    String fileUrl, nickName;
    Dialog dialog;
    int sex = -1, age = -1;

    /**
     * 上传头像，并显示当前头像
     * @param filePath 本地头像路径
     */
    public void setImageHeadPath(String filePath){
        sdv_user_head.setImageURI(StringUtil.getUriFromFilePath(filePath));
        activity.showDialog();
        ImagePost.getInstance().postImage(filePath, new DJsonObjectResponse() {
            @Override
            public void onSuccess(int statusCode, DResponse response) {
                activity.hideDialog();
                if (response.code == 0) {
                    PostImgResponse postImgResponse = response.getData(
                            new TypeToken<PostImgResponse>() {
                            }.getType()
                    );
                    fileUrl = postImgResponse.postImg;
                } else {
                    clearUserHead();
                }
                ToastUtil.showToast(response.message);
            }

            @Override
            public void onFailure(int statusCode, Throwable throwable) {
                activity.hideDialog();
                clearUserHead();
                ToastUtil.showToast("网络或服务器错误");
            }
        });
    }

    @AfterViews
    void init(){
        activity = (RegisterActivity) getActivity();
        dialog = DialogUtil.createTakeOrChose(activity, 1, RegisterActivity.REQUEST_TAKE_OR_SELECT);

        setUpTitle();
        setUpSexSelecter();
    }

    @Click(R.id.rl_root)
    void hideSoftKeyBoard(){
        KeyBoardUtils.closeKeybord(et_user_name, activity);
        et_user_name.clearFocus();
    }

    @Click(R.id.sdv_user_head)
    void onHeadClick(){
        if(!dialog.isShowing()){
            dialog.show();
        }
    }

    void setUpTitle(){
        title_bar.setOnLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.prePage();
            }
        });

        title_bar.setOnRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkInput()) {
                    return;
                }
                RegisterRequest registerRequest = new RegisterRequest();
                registerRequest.email = activity.email;
                registerRequest.password = activity.password;
                UserData userData = new UserData();
                userData.email = activity.email;
                userData.head = fileUrl;
                userData.nickname = nickName;
                userData.sex = sex;
                registerRequest.info = userData;
                //显示进度条
                activity.showDialog();
                HttpNetWork.getInstance().request(registerRequest, new HttpNetWork.NetResponseCallback() {
                    @Override
                    public void onResponse(DHttpRequestBase request, HttpResponsePacket response) {
                        activity.hideDialog();
                        if (response.code == 0) {
                            config.edit()
                                    .email().put(activity.email)
                                    .head().put(fileUrl)
                                    .moving_num().put(0)
                                    .age().put(age)
                                    .nickname().put(nickName)
                                    .sex().put(sex)
                                    .auto_login().put(true)
                                    .apply();
                            Intent intent = new Intent();
                            intent.putExtra("head", fileUrl);
                            intent.putExtra("nickname", nickName);
                            activity.setResult(RegisterActivity.REGISTER_SUCCESS, intent);
                            activity.finish();
                        }
                        ToastUtil.showToast(response.message);
                    }
                }, new HttpNetWork.NetErrorCallback() {
                    @Override
                    public void onError(DHttpRequestBase request, String errorMsg) {
                        activity.hideDialog();
                        ToastUtil.showToast(errorMsg);
                    }
                });
            }
        });
    }

    void setUpSexSelecter(){
        gp_sex_select.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_male:{
                        sex = Const.MALE;
                        break;
                    }
                    case R.id.rb_female:{
                        sex = Const.FEMALE;
                        break;
                    }
                }
            }
        });
    }

    boolean checkInput(){
        if(fileUrl == null){
            ToastUtil.showToast("请上传头像");
            return false;
        }
        if(sex == -1){
            ToastUtil.showToast("请告诉我你的性别");
            return false;
        }
        nickName = et_user_name.getText().toString();
        if(StringUtil.isEmpty(nickName)){
            ToastUtil.showToast("请输入名称");
            return false;
        }
        age = StringUtil.isAge(et_age.getText().toString());
        if(age == -1){
            ToastUtil.showToast("年龄不合法");
            return false;
        }
        return true;
    }

    void clearUserHead(){
        sdv_user_head.setImageURI(DDStringUtil.getUriFromResource(R.color.clouds));
    }
}
