package org.voiddog.mblog.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.constant.WBConstants;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.voiddog.lib.util.ImageUtil;
import org.voiddog.lib.util.SizeUtil;
import org.voiddog.lib.util.StringUtil;
import org.voiddog.lib.util.ToastUtil;
import org.voiddog.mblog.Const;
import org.voiddog.mblog.MyApplication;
import org.voiddog.mblog.R;
import org.voiddog.mblog.ui.TitleBar;
import org.voiddog.mblog.util.DialogUtil;

/**
 * 文件详情页面
 * Created by Dog on 2015/4/7.
 */
@EActivity(R.layout.activity_article_detail)
public class ArticleDetailActivity extends AppCompatActivity implements IWeiboHandler.Response{
    @ViewById
    TitleBar title_bar;
    @ViewById
    ImageView iv_blur_bg;
    @ViewById
    SimpleDraweeView sdv_card_head, sdv_user_head;
    @ViewById
    TextView tv_content;
    @ViewById
    TextView tv_user_name, tv_time;
    @ViewById
    RecyclerView rv_praise;
    @Extra
    int article_id;
    @Extra
    String article_content, article_title, article_subtitle;

    IWeiboShareAPI mWeiboShareAPI;
    Dialog progressDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 创建微博 SDK 接口实例
        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, Const.WB_APP_KEY);

        // 注册第三方应用到微博客户端中，注册成功后该应用将显示在微博的应用列表中。
        // 但该附件栏集成分享权限需要合作申请，详情请查看 Demo 提示
        // NOTE：请务必提前注册，即界面初始化的时候或是应用程序初始化时，进行注册
        mWeiboShareAPI.registerApp();

        // 当 Activity 被重新初始化时（该 Activity 处于后台时，可能会由于内存不足被杀掉了），
        // 需要调用 {@link IWeiboShareAPI#handleWeiboResponse} 来接收微博客户端返回的数据。
        // 执行成功，返回 true，并调用 {@link IWeiboHandler.Response#onResponse}；
        // 失败返回 false，不调用上述回调
        if (savedInstanceState != null) {
            mWeiboShareAPI.handleWeiboResponse(getIntent(), this);
        }
    }

    /**
     * @see {@link AppCompatActivity#onNewIntent}
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        // 从当前应用唤起微博并进行分享后，返回到当前应用时，需要在此处调用该函数
        // 来接收微博客户端返回的数据；执行成功，返回 true，并调用
        // {@link IWeiboHandler.Response#onResponse}；失败返回 false，不调用上述回调
        mWeiboShareAPI.handleWeiboResponse(intent, this);
    }

    @AfterViews
    void init(){
        //一体化色彩
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        //给progress 赋值
        progressDialog = DialogUtil.createProgressDialog(this);
        setUpTitle();
        //设置图片大小为1:1
        sdv_card_head.setAspectRatio(1.0f);
        //填充原始数据
        tv_content.setText(article_content);
        loadData();
    }

    @Click(R.id.fat_share)
    void startShare(){
        shareToWeiBo();
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
//        if(!progressDialog.isShowing()){
//            progressDialog.show();
//        }
//        MyHttpRequest.GetArticle getArticle = new MyHttpRequest.GetArticle(article_id);
//        MyHttpNetWork.getInstance().request(getArticle, new DJsonObjectResponse() {
//            @Override
//            public void onSuccess(int statusCode, DResponse response) {
//                progressDialog.cancel();
//                if (response.code == 0) {
//                    try {
//                        HttpStruct.Article article = response.getData(HttpStruct.Article.class);
//                        tv_content.setText(article.body);
//                        tv_user_name.setText(article.nickname);
//                        tv_time.setText(article.updated_at);
//                        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(MyApplication.getImageHostUri(article.head))
//                                .setResizeOptions(new ResizeOptions(sdv_user_head.getWidth() << 1, sdv_user_head.getHeight() << 1))
//                                .build();
//                        PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
//                                .setOldController(sdv_user_head.getController())
//                                .setImageRequest(request)
//                                .build();
//                        sdv_user_head.setController(controller);
//                        loadImage(article.image);
//                    } catch (Exception ignore) {
//                        ToastUtil.showToast("数据错误");
//                        ArticleDetailActivity.this.finish();
//                    }
//                } else {
//                    ToastUtil.showToast(response.message);
//                    ArticleDetailActivity.this.finish();
//                }
//            }
//
//            @Override
//            public void onFailure(int statusCode, Throwable throwable) {
//                progressDialog.cancel();
//                ToastUtil.showToast("网络或服务器错误, 错误代码: " + statusCode);
//                ArticleDetailActivity.this.finish();
//            }
//        });
    }

    /**
     * 分享到微博
     */
    void shareToWeiBo(){
        if(mWeiboShareAPI == null){
            ToastUtil.showToast("初始化微博失败");
            return;
        }
        // 获取微博客户端相关信息，如是否安装、支持 SDK 的版本
        if(!mWeiboShareAPI.isWeiboAppInstalled()){
            ToastUtil.showToast("未安装微博客户端");
            return;
        }
        int supportApiLevel = mWeiboShareAPI.getWeiboAppSupportAPI();
        if(!mWeiboShareAPI.isWeiboAppSupportAPI() || supportApiLevel < Const.WB_API_VERSION){
            ToastUtil.showToast("此微博客户端不支持，请更新");
            return;
        }

        // 2. 初始化从第三方到微博的消息请求
        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        // 用transaction唯一标识一个请求
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = getShareMessage();

        mWeiboShareAPI.sendRequest(ArticleDetailActivity.this, request);
    }

    WeiboMultiMessage getShareMessage(){
        WeiboMultiMessage multiMessage = new WeiboMultiMessage();
        String message = tv_content.getText().toString();
        if(!StringUtil.isEmpty(message)){
            TextObject textObject = new TextObject();
            textObject.text = message;
            multiMessage.textObject = textObject;
        }
        sdv_card_head.setDrawingCacheEnabled(true);
        ImageObject imageObject = new ImageObject();
        imageObject.setImageObject(sdv_card_head.getDrawingCache());
        multiMessage.imageObject = imageObject;
        return multiMessage;
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
        Bitmap newBitmap = ImageUtil.getBlurImage(bitmap, 8, 10);
        setBlur(newBitmap);
    }

    @UiThread
    void setBlur(Bitmap blur){
        iv_blur_bg.setImageBitmap(blur);
    }

    @Override
    public void onResponse(BaseResponse baseResponse) {
        Log.e("fuck", "error code: " + baseResponse.errCode + " error message" + baseResponse.errMsg);
        switch (baseResponse.errCode) {
            case WBConstants.ErrorCode.ERR_OK:
                ToastUtil.showToast("分享成功");
                break;
            case WBConstants.ErrorCode.ERR_CANCEL:
                ToastUtil.showToast("分享取消");
                break;
            case WBConstants.ErrorCode.ERR_FAIL:
                ToastUtil.showToast("分享失败");
                break;
            default:
                ToastUtil.showToast("未知错误");
                break;
        }
    }
}