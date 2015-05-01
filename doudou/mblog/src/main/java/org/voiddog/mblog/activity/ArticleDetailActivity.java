package org.voiddog.mblog.activity;

import android.graphics.Bitmap;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.facebook.drawee.view.SimpleDraweeView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.voiddog.lib.http.DJsonObjectResponse;
import org.voiddog.lib.util.ImageCacheManager;
import org.voiddog.lib.util.ImageUtil;
import org.voiddog.lib.util.SizeUtil;
import org.voiddog.lib.util.ToastUtil;
import org.voiddog.mblog.MyApplication;
import org.voiddog.mblog.R;
import org.voiddog.mblog.http.HttpStruct;
import org.voiddog.mblog.http.MyHttpNetWork;
import org.voiddog.mblog.http.MyHttpRequest;
import org.voiddog.mblog.ui.TitleBar;

/**
 * 文件详情页面
 * Created by Dog on 2015/4/7.
 */
@EActivity(R.layout.activity_article_detail)
public class ArticleDetailActivity extends ActionBarActivity{
    @ViewById
    TitleBar title_bar;
    @ViewById
    ImageView iv_card_head, iv_blur_bg;
    @ViewById
    TextView tv_content;
    @Extra
    int article_id;
    @Extra
    String article_content, article_title, article_subtitle;

    @AfterViews
    void init(){
        //一体化色彩
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        setUpTitle();
        //设置图片大小为1:1
        iv_card_head.getLayoutParams().height = SizeUtil.getScreenWidth();
        //填充原始数据
        tv_content.setText(article_content);
        loadData();
    }

    void setUpTitle(){
        title_bar.setTitle(article_title);
        title_bar.setOnLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArticleDetailActivity.this.finish();
            }
        });
    }

    void loadData(){
        MyHttpRequest.GetArticle getArticle = new MyHttpRequest.GetArticle(article_id);
        MyHttpNetWork.getInstance().request(getArticle, new DJsonObjectResponse() {
            @Override
            public void onSuccess(int statusCode, DResponse response) {
                if(response.code == 0){
                    try {
                        HttpStruct.Article article = response.getData(HttpStruct.Article.class);
                        tv_content.setText(article.body);
                        loadImage(article.image);
                    } catch (Exception ignore){
                        ToastUtil.showToast("数据错误");
                        ArticleDetailActivity.this.finish();
                    }
                }
                else{
                    ToastUtil.showToast(response.message);
                    ArticleDetailActivity.this.finish();
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable throwable) {
                ToastUtil.showToast("网络或服务器错误, 错误代码: " + statusCode);
                ArticleDetailActivity.this.finish();
            }
        });
    }

    /**
     * 加载图片
     */
    void loadImage(String image){
        if(image == null) return;
        String url = MyApplication.getImageHost(image);
        ImageLoader.ImageListener imageListener = new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                Bitmap bitmap = response.getBitmap();
                if(bitmap != null){
                    iv_card_head.setImageBitmap(bitmap);
                    iv_blur_bg.setImageBitmap(ImageUtil.getBlurImage(bitmap, ArticleDetailActivity.this));
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        };
        ImageCacheManager.getInstacne().getImageLoader().get(url, imageListener, SizeUtil.getScreenWidth(),
                SizeUtil.getScreenHeight());
    }
}