package org.voiddog.mblog.activity;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.reflect.TypeToken;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.voiddog.lib.http.DHttpRequestBase;
import org.voiddog.lib.http.DJsonObjectResponse;
import org.voiddog.lib.http.HttpNetWork;
import org.voiddog.lib.http.HttpResponsePacket;
import org.voiddog.lib.util.ImageUtil;
import org.voiddog.lib.util.StringUtil;
import org.voiddog.lib.util.ToastUtil;
import org.voiddog.mblog.R;
import org.voiddog.mblog.data.PostImgResponse;
import org.voiddog.mblog.fragment.RegisterStep2Fragment;
import org.voiddog.mblog.http.ImagePost;
import org.voiddog.mblog.http.PublishMovingRequest;
import org.voiddog.mblog.preference.Config_;
import org.voiddog.mblog.ui.TitleBar;
import org.voiddog.mblog.util.DialogUtil;

/**
 * 发布动态
 * boolean isCamera
 * Created by Dog on 2015/5/30.
 */
@EActivity(R.layout.activity_publish_moving)
public class PublishMovingActivity extends AppCompatActivity{
    public static final int REQUEST_CHOSE_PHOTO = 0;
    public static final int REQUEST_TAKE_PHOTO = 1;
    public static final int REQUEST_CROP_PHOTO = 2;

    @ViewById
    TitleBar title_bar;
    @ViewById
    EditText et_title, et_sub_title, et_content;
    @ViewById
    SimpleDraweeView sdv_upload;
    @Extra
    boolean isCamera = false;
    @Pref
    Config_ config;

    Dialog progressDialog;
    String path;

    @AfterViews
    void init(){
        progressDialog = DialogUtil.createProgressDialog(this);

        setUpTitle();

        if(isCamera){
            // TODO 跳转到相机
        }
        else{
            ChoseImgFromLibActivity_.intent(this)
                    .maxChoseNumber(1)
                    .startForResult(REQUEST_CHOSE_PHOTO);
        }
    }

    @Click({R.id.btn_h1, R.id.btn_h3, R.id.btn_a, R.id.btn_strong})
    void onButtonClick(View view){
       if(et_content.isFocusable()) {
           switch (view.getId()) {
               case R.id.btn_h1: {
                   et_content.append("<h1></h1>");
                   break;
               }
               case R.id.btn_h3: {
                   et_content.append("<h3></h3>");
                   break;
               }
               case R.id.btn_a: {
                   et_content.append("<a href=\"\"></a>");
                   break;
               }
               case R.id.btn_strong: {
                   et_content.append("<strong></strong>");
                   break;
               }
           }
       }
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
                startPublishMoving();
            }
        });
    }

    void startPublishMoving(){
        if(!checkInput()){
            return;
        }

        pushPhoto();
    }

    void pushPhoto(){
        if(path == null){
            finish();
            return;
        }

        progressDialog.show();
        ImagePost.getInstance().postImage(path, new DJsonObjectResponse() {
            @Override
            public void onSuccess(int statusCode, DResponse response) {
                if (response.code == 0) {
                    PostImgResponse postImgResponse = response.getData(
                            new TypeToken<PostImgResponse>() {
                            }.getType()
                    );
                    postText(postImgResponse.postImg);
                } else {
                    progressDialog.cancel();
                    ToastUtil.showToast(response.message);
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable throwable) {
                progressDialog.cancel();
            }
        });
    }

    void postText(String photoUrl){
        PublishMovingRequest publishMovingRequest = new PublishMovingRequest();
        publishMovingRequest.email = config.email().get();
        publishMovingRequest.title = et_title.getText().toString();
        publishMovingRequest.sub_title = et_sub_title.getText().toString();
        publishMovingRequest.content = et_content.getText().toString();
        publishMovingRequest.pic = photoUrl;

        HttpNetWork.getInstance().request(publishMovingRequest, new HttpNetWork.NetResponseCallback() {
            @Override
            public void onResponse(DHttpRequestBase request, HttpResponsePacket response) {
                progressDialog.cancel();
                if(response.code == 0){
                    ToastUtil.showToast("上传成功");
                    finish();
                }
                else{
                    ToastUtil.showToast(response.message);
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

    boolean checkInput(){
        if(et_title.getText().length() == 0){
            ToastUtil.showToast("标题不能为空");
            return false;
        }
        if(et_sub_title.getText().length() == 0){
            ToastUtil.showToast("子标题不能为空");
            return false;
        }
        if(et_content.getText().length() == 0){
            ToastUtil.showToast("内容不能为空");
            return false;
        }
        return true;
    }

    @OnActivityResult(REQUEST_CHOSE_PHOTO)
    void onTakeReturn(int resultCode, Intent data){
        if(resultCode == RESULT_OK && data != null){
            String[] paths = data.getStringArrayExtra("paths");
            if(paths == null || paths.length == 0){
                finish();
                return;
            }
            CropActivity_.intent(this)
                    .extra("filePath", paths[0])
                    .startForResult(REQUEST_CROP_PHOTO);
            overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.alpha_out);
        }
        else{
            if(path == null) {
                finish();
            }
        }
    }

    @OnActivityResult(REQUEST_CROP_PHOTO)
    void onCropPhoto(int resultCode, Intent data){
        if(resultCode == CropActivity.CROP_SUCCESS && data != null){
            path = data.getStringExtra("path");
            if(path != null) {
                int itemSize = getResources().getDimensionPixelOffset(R.dimen.publish_article_bottom_photo_width);
                Uri uri = StringUtil.getUriFromFilePath(path);
                sdv_upload.setController(ImageUtil.getControllerWithSize(
                        sdv_upload.getController(), uri, itemSize, itemSize
                ));
                return;
            }
        }
        if(path == null) {
            finish();
        }
    }
}
