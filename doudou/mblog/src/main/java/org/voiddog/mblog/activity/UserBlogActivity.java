package org.voiddog.mblog.activity;

import android.app.Dialog;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.BasePostprocessor;
import com.facebook.imagepipeline.request.Postprocessor;
import com.google.gson.reflect.TypeToken;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.voiddog.lib.http.DHttpRequestBase;
import org.voiddog.lib.http.HttpNetWork;
import org.voiddog.lib.http.HttpResponsePacket;
import org.voiddog.lib.ui.CustomFontTextView;
import org.voiddog.lib.util.ImageUtil;
import org.voiddog.lib.util.ToastUtil;
import org.voiddog.mblog.Const;
import org.voiddog.mblog.MyApplication;
import org.voiddog.mblog.R;
import org.voiddog.mblog.adapter.ArticleListAdapter;
import org.voiddog.mblog.adapter.UserArticleListAdapter;
import org.voiddog.mblog.data.ArticleData;
import org.voiddog.mblog.data.GetMovingByIdRequest;
import org.voiddog.mblog.data.GetUserInfoResponseData;
import org.voiddog.mblog.db.model.UserModel;
import org.voiddog.mblog.http.AddAttentionRequest;
import org.voiddog.mblog.http.GetUserInfoRequest;
import org.voiddog.mblog.preference.Config_;
import org.voiddog.mblog.receiver.UpdateUserInfoReceiver;
import org.voiddog.mblog.ui.SexAgeView;
import org.voiddog.mblog.ui.TitleBar;
import org.voiddog.mblog.ui.UserBlogPullListView;
import org.voiddog.mblog.util.DDImageUtil;
import org.voiddog.mblog.util.DialogUtil;

import java.sql.SQLException;
import java.util.List;

/**
 * 用户的个人页面
 * String email 查看对象的email
 * Created by Dog on 2015/5/26.
 */
@EActivity(R.layout.activity_user_blog)
public class UserBlogActivity extends AppCompatActivity implements AbsListView.OnScrollListener{
    @ViewById
    UserBlogPullListView ptz_lv_user_blog;
    @ViewById
    TitleBar title_bar;
    @Pref
    Config_ config;
    @Extra
    String tEmail = null;

    //头部view 部分
    View headView;
    SimpleDraweeView sdv_user_head;
    CustomFontTextView cf_tv_user_name;
    ImageView iv_blur_bg;
    SexAgeView sex_age;
    TextView footView;

    UserArticleListAdapter adapter;
    //我的邮箱
    String mEmail;
    int userHeadSize = 0;
    Postprocessor postprocessor;
    int firstVisibleItem, visibleItemCount, totalItemCount;
    boolean isLoading = false, hasMore = true;
    GetMovingByIdRequest movingRequest = new GetMovingByIdRequest();
    Dialog progressDialog;
    UserModel userModel;
    MyUpdateUserInfoReceiver receiver;

    @AfterViews
    void init(){
        if(tEmail == null){
            finish();
            return;
        }
        if(receiver == null){
            receiver = new MyUpdateUserInfoReceiver();
            IntentFilter filter = new IntentFilter(UpdateUserInfoReceiver.UPDATE_USER_INFO);
            registerReceiver(receiver, filter);
        }
        isLoading = false;
        hasMore = true;
        mEmail = config.email().get();
        userHeadSize = getResources().getDimensionPixelSize(R.dimen.user_head_size);
        postprocessor = new BasePostprocessor() {
            @Override
            public String getName() {
                return "postprocessor";
            }

            @Override
            public void process(Bitmap bitmap) {
                startBlur(bitmap);
            }
        };

        initHeadView();
        setUpFootView();
        adapter = new UserArticleListAdapter(this);
        ptz_lv_user_blog.setAdapter(adapter);
        ptz_lv_user_blog.setScrollListener(this);

        setUpTitle();
        loadLocalData();
        loadUserInfoFromNet();
        movingRequest.page = 0;
        movingRequest.id = tEmail;
        movingRequest.email = mEmail == null ? "" : mEmail;
        loadMoreListData();
    }

    @ItemClick(R.id.ptz_lv_user_blog)
    void onArticleClick(ArticleData articleData){
        if(articleData != null){
            ArticleDetailActivity_.intent(this)
                    .article_content(articleData.content)
                    .article_id(articleData.mid)
                    .article_subtitle(articleData.sub_title)
                    .article_title(articleData.title)
                    .start();
        }
    }

    void initHeadView(){
        headView = View.inflate(this, R.layout.ui_user_blog_head, null);
        sdv_user_head = (SimpleDraweeView) headView.findViewById(R.id.sdv_user_head);
        cf_tv_user_name = (CustomFontTextView) headView.findViewById(R.id.cf_tv_user_name);
        sex_age = (SexAgeView) headView.findViewById(R.id.sex_age);

        ptz_lv_user_blog.setHeaderView(headView);
        ptz_lv_user_blog.setHeaderLayoutParams(
                new AbsListView.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        getResources().getDimensionPixelOffset(R.dimen.user_info_head_height)
                )
        );

        iv_blur_bg = new ImageView(this);
        iv_blur_bg.setScaleType(ImageView.ScaleType.CENTER_CROP);
        iv_blur_bg.setLayoutParams(new AbsListView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        ));
        ptz_lv_user_blog.setZoomView(iv_blur_bg);
        ptz_lv_user_blog.setTitle_bar(title_bar);

        sdv_user_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userModel != null) {
                    String paths[] = new String[1];
                    paths[0] = Const.IMG_HOST + userModel.head;
                    PhotoPreviewActivity_.intent(UserBlogActivity.this)
                            .isLocal(false)
                            .photoPaths(paths)
                            .start();
                }
            }
        });
    }

    void setUpFootView(){
        View view = View.inflate(this, R.layout.load_more_foot_view, null);
        footView = (TextView) view.findViewById(R.id.tv_load_more);
        ptz_lv_user_blog.getPullRootView().addFooterView(footView);
    }

    void setUpTitle(){
        title_bar.setOnLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if(mEmail != null && mEmail.equals(tEmail)){
            showEdit();
        }
        else{
            showAttention();
        }
    }

    void loadMoreListData(){
        if(isLoading || !hasMore){
            return;
        }
        footView.setText("正在加载");
        isLoading = true;
        HttpNetWork.getInstance().request(movingRequest, new HttpNetWork.NetResponseCallback() {
            @Override
            public void onResponse(DHttpRequestBase request, HttpResponsePacket response) {
                if(response.code == 0){
                    List<ArticleData> articleDataList = response.getData(
                            new TypeToken<List<ArticleData>>(){}.getType()
                    );
                    adapter.addArticleDataList(articleDataList);
                    movingRequest.page++;
                    if(articleDataList.size() < movingRequest.num){
                        hasMore = false;
                        footView.setText("加载完毕");
                    }
                }
                else{
                    footView.setText(response.message);
                }
                isLoading = false;
            }
        }, new HttpNetWork.NetErrorCallback() {
            @Override
            public void onError(DHttpRequestBase request, String errorMsg) {
                isLoading = false;
                ToastUtil.showToast("网络或服务器错误");
                footView.setText("加载失败");
            }
        });
    }

    /**
     * 加载本地数据
     **/
    void loadLocalData(){
        try {
            userModel = UserModel.Dao().queryForId(tEmail);
            fillUserInfoData(userModel);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void loadUserInfoFromNet(){
        GetUserInfoRequest userInfoRequest = new GetUserInfoRequest();
        userInfoRequest.email = tEmail;
        HttpNetWork.getInstance().request(userInfoRequest, new HttpNetWork.NetResponseCallback() {
            @Override
            public void onResponse(DHttpRequestBase request, HttpResponsePacket response) {
                if(response.code == 0){
                    GetUserInfoResponseData responseData = response.getData(
                            new TypeToken<GetUserInfoResponseData>(){}.getType()
                    );
                    userModel = responseData.info;
                    fillUserInfoData(userModel);
                    try {
                        UserModel.Dao().createOrUpdate(userModel);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    ToastUtil.showToast(response.message);
                    finish();
                }
            }
        }, new HttpNetWork.NetErrorCallback() {
            @Override
            public void onError(DHttpRequestBase request, String errorMsg) {
                ToastUtil.showToast("网络错误");
                finish();
            }
        });
    }

    void fillUserInfoData(UserModel mUserModel){
        if(mUserModel != null){
            Uri uri = MyApplication.getImageHostUri(mUserModel.head);
            sdv_user_head.setController(DDImageUtil.getControllerBySize(
                    sdv_user_head, uri, userHeadSize, userHeadSize, postprocessor
            ));
            cf_tv_user_name.setText(mUserModel.nickname);
            sex_age.setAge(mUserModel.age);
            sex_age.setSex(mUserModel.sex);
        }
    }

    void showEdit(){
        title_bar.setRightIcon("fa-cog");
        title_bar.setRightText("");
        title_bar.setOnRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingActivity_.intent(UserBlogActivity.this).start();
            }
        });
    }

    void showAttention(){
        title_bar.setRightIcon(null);
        title_bar.setRightText("关注");
        title_bar.setOnRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mEmail == null){
                    LoginOrRegisterActivity_.intent(UserBlogActivity.this)
                            .start();
                }
                else {
                    if(progressDialog == null){
                        progressDialog = DialogUtil.createProgressDialog(UserBlogActivity.this);
                    }
                    progressDialog.show();
                    AddAttentionRequest attentionRequest = new AddAttentionRequest();
                    attentionRequest.s_email = mEmail;
                    attentionRequest.t_email = tEmail;
                    HttpNetWork.getInstance().request(attentionRequest, new HttpNetWork.NetResponseCallback() {
                        @Override
                        public void onResponse(DHttpRequestBase request, HttpResponsePacket response) {
                            progressDialog.cancel();
                            ToastUtil.showToast(response.message);
                        }
                    }, new HttpNetWork.NetErrorCallback() {
                        @Override
                        public void onError(DHttpRequestBase request, String errorMsg) {
                            progressDialog.cancel();
                            ToastUtil.showToast("网络错误");
                        }
                    });
                }
            }
        });
    }

    @Background
    void startBlur(Bitmap bitmap){
        Bitmap newBitmap = ImageUtil.getBlurImage(bitmap, 2, 8);
        applyBlur(newBitmap);
    }

    @UiThread
    void applyBlur(Bitmap bitmap){
        iv_blur_bg.setImageBitmap(bitmap);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if(scrollState == SCROLL_STATE_IDLE && firstVisibleItem + visibleItemCount == totalItemCount){
            loadMoreListData();
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.firstVisibleItem = firstVisibleItem;
        this.visibleItemCount = visibleItemCount;
        this.totalItemCount = totalItemCount;
    }

    @Override
    protected void onDestroy() {
        try{
            unregisterReceiver(receiver);
        }
        catch (Exception ignore){}
        super.onDestroy();
    }

    class MyUpdateUserInfoReceiver extends UpdateUserInfoReceiver {

        @Override
        protected void onUpdate() {
            loadLocalData();
        }
    }
}
