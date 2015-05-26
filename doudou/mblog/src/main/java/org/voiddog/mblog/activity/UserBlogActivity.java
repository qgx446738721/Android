package org.voiddog.mblog.activity;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.BasePostprocessor;
import com.facebook.imagepipeline.request.Postprocessor;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.voiddog.lib.ui.CustomFontTextView;
import org.voiddog.lib.util.ImageUtil;
import org.voiddog.mblog.Const;
import org.voiddog.mblog.R;
import org.voiddog.mblog.adapter.ArticleListAdapter;
import org.voiddog.mblog.data.ArticleData;
import org.voiddog.mblog.preference.Config_;
import org.voiddog.mblog.ui.SexAgeView;
import org.voiddog.mblog.ui.TitleBar;
import org.voiddog.mblog.ui.UserBlogPullListView;
import org.voiddog.mblog.util.DDImageUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户的个人页面
 * String email 查看对象的email
 * Created by Dog on 2015/5/26.
 */
@EActivity(R.layout.activity_user_blog)
public class UserBlogActivity extends AppCompatActivity{
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

    ArticleListAdapter adapter;
    //我的邮箱
    String mEmail;

    @AfterViews
    void init(){
        if(tEmail == null){
            finish();
            return;
        }
        mEmail = config.email().get();

        initHeadView();
        adapter = new ArticleListAdapter();
        ptz_lv_user_blog.setAdapter(adapter);

        setUpTitle();

        loadLocalData();
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
    }

    void setUpTitle(){
        title_bar.setOnLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 加载本地数据
     **/
    void loadLocalData(){
        // TODO 加载本地数据
        //测试
        Uri uri = Uri.parse("http://voiddog.qiniudn.com/madoka.jpg");
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
        int size = getResources().getDimensionPixelSize(R.dimen.user_head_size);
        sdv_user_head.setController(
                DDImageUtil.getControllerBySize(sdv_user_head, uri, size, size, postprocessor)
        );
        cf_tv_user_name.setText("戚耿鑫");
        //判断是否是本人来显示编辑按钮
        if(mEmail != null && mEmail.equals(tEmail)){
            showEdit();
        }
        else{
            hideEdit();
        }
        //性别
        sex_age.setAge(20);
        sex_age.setSex(Const.MALE);
        //文章数据
        List<ArticleData> articleList = new ArrayList<>();
        for(int i = 0; i < 10; i++){
            ArticleData article = new ArticleData();
            article.pic = "bible.jpg";
            article.title = "力哥是个基佬";
            article.sub_title = "就是";
            articleList.add(article);
        }
        adapter.setDataList(articleList);
    }

    void requestNetData(){
        // TODO 加载网络数据
    }

    void showEdit(){
        title_bar.setRightIcon("fa-cog");
        title_bar.setOnRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO 去编辑个人信息页面
            }
        });
    }

    void hideEdit(){
        title_bar.setRightIcon(null);
        title_bar.setOnRightClickListener(null);
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
}
