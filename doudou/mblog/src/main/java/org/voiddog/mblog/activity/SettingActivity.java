package org.voiddog.mblog.activity;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.reflect.TypeToken;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.voiddog.lib.http.DHttpRequestBase;
import org.voiddog.lib.http.DJsonObjectResponse;
import org.voiddog.lib.http.HttpNetWork;
import org.voiddog.lib.http.HttpResponsePacket;
import org.voiddog.lib.util.ImageUtil;
import org.voiddog.lib.util.KeyBoardUtils;
import org.voiddog.lib.util.StringUtil;
import org.voiddog.lib.util.ToastUtil;
import org.voiddog.mblog.Const;
import org.voiddog.mblog.MyApplication;
import org.voiddog.mblog.R;
import org.voiddog.mblog.data.PostImgResponse;
import org.voiddog.mblog.db.model.UserModel;
import org.voiddog.mblog.http.ImagePost;
import org.voiddog.mblog.http.UpdateUserInfoRequest;
import org.voiddog.mblog.preference.Config_;
import org.voiddog.mblog.receiver.UpdateUserInfoReceiver;
import org.voiddog.mblog.ui.TitleBar;
import org.voiddog.mblog.util.DialogUtil;

import java.sql.SQLException;

/**
 * 设置页面
 * Created by Dog on 2015/6/1.
 */
@EActivity(R.layout.setting_activity)
public class SettingActivity extends AppCompatActivity{
    static final int REQUEST_TAKE_OR_SELECT = 1;
    static final int REQUEST_CROP_PHOTO = 2;

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

    UserModel userModel;
    Dialog dialog, progressDialog;

    @AfterViews
    void init(){
        dialog = DialogUtil.createTakeOrChose(this, 1, REQUEST_TAKE_OR_SELECT);
        progressDialog = DialogUtil.createProgressDialog(this);
        userModel = new UserModel();
        userModel.moving_num = config.moving_num().get();
        userModel.head = config.head().get();
        userModel.sex = config.sex().get();
        userModel.age = config.age().get();
        userModel.nickname = config.nickname().get();
        userModel.email = config.email().get();

        et_user_name.setText(userModel.nickname);
        et_age.setText(Integer.toString(userModel.age));

        clearUserHead();
        setUpTitle();
        setUpSexSelecter();
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
                if (!checkInput()) {
                    return;
                }

                UpdateUserInfoRequest updateUserInfoRequest = new UpdateUserInfoRequest();
                updateUserInfoRequest.email = config.email().get();
                updateUserInfoRequest.info = userModel;
                progressDialog.show();
                HttpNetWork.getInstance().request(updateUserInfoRequest, new HttpNetWork.NetResponseCallback() {
                    @Override
                    public void onResponse(DHttpRequestBase request, HttpResponsePacket response) {
                        progressDialog.cancel();
                        ToastUtil.showToast(response.message);
                        if(response.code == 0){
                            config.edit()
                                    .age().put(userModel.age)
                                    .head().put(userModel.head)
                                    .nickname().put(userModel.nickname)
                                    .sex().put(userModel.sex)
                                    .apply();
                            try {
                                UserModel.Dao().createOrUpdate(userModel);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            Intent intent = new Intent();
                            intent.setAction(UpdateUserInfoReceiver.UPDATE_USER_INFO);
                            intent.putExtra("COMMAND", UpdateUserInfoReceiver.NEED_UPDATE);
                            sendBroadcast(intent);
                            finish();
                        }
                    }
                }, new HttpNetWork.NetErrorCallback() {
                    @Override
                    public void onError(DHttpRequestBase request, String errorMsg) {
                        progressDialog.cancel();
                        ToastUtil.showToast("网络或服务器错误");
                    }
                });
            }
        });
    }

    void setUpSexSelecter(){
        if(userModel.sex == Const.MALE){
            gp_sex_select.check(R.id.rb_male);
        }
        else{
            gp_sex_select.check(R.id.rb_female);
        }
        gp_sex_select.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_male: {
                        userModel.sex = Const.MALE;
                        break;
                    }
                    case R.id.rb_female: {
                        userModel.sex = Const.FEMALE;
                        break;
                    }
                }
            }
        });
    }

    void onGetPhoto(String filePath){
        sdv_user_head.setImageURI(StringUtil.getUriFromFilePath(filePath));
        progressDialog.show();
        ImagePost.getInstance().postImage(filePath, new DJsonObjectResponse() {
            @Override
            public void onSuccess(int statusCode, DResponse response) {
                progressDialog.cancel();
                if (response.code == 0) {
                    PostImgResponse postImgResponse = response.getData(
                            new TypeToken<PostImgResponse>() {
                            }.getType()
                    );
                    userModel.head = postImgResponse.postImg;
                } else {
                    clearUserHead();
                }
                ToastUtil.showToast(response.message);
            }

            @Override
            public void onFailure(int statusCode, Throwable throwable) {
                progressDialog.cancel();
                clearUserHead();
                ToastUtil.showToast("上传头像失败");
            }
        });
    }

    boolean checkInput(){
        if(userModel.head == null){
            ToastUtil.showToast("请上传头像");
            return false;
        }
        if(userModel.sex == -1){
            ToastUtil.showToast("请告诉我你的性别");
            return false;
        }
        userModel.nickname = et_user_name.getText().toString();
        if(StringUtil.isEmpty(userModel.nickname)){
            ToastUtil.showToast("请输入名称");
            return false;
        }
        userModel.age = StringUtil.isAge(et_age.getText().toString());
        if(userModel.age == -1){
            ToastUtil.showToast("年龄不合法");
            return false;
        }
        return true;
    }

    void clearUserHead() {
        Uri uri = MyApplication.getImageHostUri(userModel.head);
        sdv_user_head.setController(ImageUtil.getControllerWithSize(
                        sdv_user_head.getController(),
                        uri,
                        sdv_user_head.getLayoutParams().width,
                        sdv_user_head.getLayoutParams().height
        ));
    }

    @Click(R.id.rl_root)
    void hideSoftKeyBoard(){
        KeyBoardUtils.closeKeybord(et_user_name, this);
        et_user_name.clearFocus();
    }

    @Click(R.id.sdv_user_head)
    void onHeadClick(){
        if(!dialog.isShowing()){
            dialog.show();
        }
    }

    @OnActivityResult(REQUEST_TAKE_OR_SELECT)
    void onGetPhoto(int result, Intent data){
        if(result == RESULT_OK){
            String[] paths = data.getStringArrayExtra("paths");
            CropActivity_.intent(this)
                    .extra("filePath", paths[0])
                    .startForResult(REQUEST_CROP_PHOTO);
            overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.alpha_out);
        }
    }

    @OnActivityResult(REQUEST_CROP_PHOTO)
    void onCropPhoto(int result, Intent data){
        if(result == CropActivity.CROP_SUCCESS){
            String path = data.getStringExtra("path");
            onGetPhoto(path);
        }
    }
}
