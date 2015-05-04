package org.voiddog.mblog.activity;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.BasePostprocessor;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.facebook.imagepipeline.request.Postprocessor;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.voiddog.lib.http.DJsonObjectResponse;
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
    ImageView iv_blur_bg;
    @ViewById
    SimpleDraweeView sdv_card_head;
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
        sdv_card_head.setAspectRatio(1.0f);
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
                if (response.code == 0) {
                    try {
                        HttpStruct.Article article = response.getData(HttpStruct.Article.class);
                        tv_content.setText(article.body);
                        loadImage(article.image);
                    } catch (Exception ignore) {
                        ToastUtil.showToast("数据错误");
                        ArticleDetailActivity.this.finish();
                    }
                } else {
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
        Uri uri = MyApplication.getImageHostUri(image);
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
        ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(uri)
                .setResizeOptions(new ResizeOptions(SizeUtil.getScreenWidth(), SizeUtil.getScreenHeight()))
                .setPostprocessor(postprocessor)
                .build();
        PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                .setImageRequest(imageRequest)
                .setOldController(sdv_card_head.getController())
                .build();
        sdv_card_head.setController(controller);
    }

    @Background
    void startBlur(Bitmap bitmap){
        Bitmap newBitmap = ImageUtil.getBlurImage(bitmap, ArticleDetailActivity.this);
        setBlur(newBitmap);
    }

    @UiThread
    void setBlur(Bitmap blur){
        iv_blur_bg.setImageBitmap(blur);
    }
}