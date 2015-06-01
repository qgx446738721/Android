package org.voiddog.mblog.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.FontAwesomeText;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.BasePostprocessor;
import com.facebook.imagepipeline.request.Postprocessor;
import com.google.gson.reflect.TypeToken;
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
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.voiddog.lib.http.DHttpRequestBase;
import org.voiddog.lib.http.HttpNetWork;
import org.voiddog.lib.http.HttpResponsePacket;
import org.voiddog.lib.util.ImageUtil;
import org.voiddog.lib.util.SizeUtil;
import org.voiddog.lib.util.StringUtil;
import org.voiddog.lib.util.TimeUtil;
import org.voiddog.lib.util.ToastUtil;
import org.voiddog.mblog.BuildConfig;
import org.voiddog.mblog.Const;
import org.voiddog.mblog.MyApplication;
import org.voiddog.mblog.R;
import org.voiddog.mblog.adapter.CommentAdapter;
import org.voiddog.mblog.adapter.PraiseAdapter;
import org.voiddog.mblog.data.ArticleData;
import org.voiddog.mblog.data.CommentData;
import org.voiddog.mblog.http.CommentRequest;
import org.voiddog.mblog.http.GetArticleRequest;
import org.voiddog.mblog.http.GetMovingReplyRequest;
import org.voiddog.mblog.http.GetPraiseRequest;
import org.voiddog.mblog.preference.Config_;
import org.voiddog.mblog.receiver.UpdateArticleItemReceiver;
import org.voiddog.mblog.ui.TitleBar;
import org.voiddog.mblog.util.DDImageUtil;
import org.voiddog.mblog.util.DialogUtil;

import java.util.List;

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
    @ViewById
    FontAwesomeText fat_praise;
    @ViewById
    EditText et_comment;
    @ViewById
    ListView lv_comment;
    @Extra
    int article_id;
    @Extra
    String article_content, article_title, article_subtitle;
    @Pref
    Config_ config;

    IWeiboShareAPI mWeiboShareAPI;
    Dialog progressDialog = null;
    PraiseAdapter adapter = new PraiseAdapter();
    CommentAdapter commentAdapter;
    int isPraise = 0;
    GetPraiseRequest getPraiseRequest = new GetPraiseRequest();

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
        commentAdapter = new CommentAdapter(this);

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
        tv_content.setText(Html.fromHtml(article_content));
        setUpPraiseRecycleView();
        setCommentList();
        loadData();
    }

    @Click(R.id.fat_share)
    void startShare(){
        shareToWeiBo();
    }

    @Click(R.id.tv_comment)
    void startComment(){
        if(config.email().exists()){
            String content = et_comment.getText().toString();
            if(StringUtil.isEmpty(content)){
                ToastUtil.showToast("评论内容");
                return;
            }
            String email = config.email().get();
            final CommentRequest commentRequest = new CommentRequest();
            commentRequest.email = email;
            commentRequest.mid = article_id;
            commentRequest.content = content;
            progressDialog.show();
            HttpNetWork.getInstance().request(commentRequest, new HttpNetWork.NetResponseCallback() {
                @Override
                public void onResponse(DHttpRequestBase request, HttpResponsePacket response) {
                    progressDialog.cancel();
                    ToastUtil.showToast(response.message);
                    if(response.code == 0){
                        et_comment.setText("");
                        Intent intent = new Intent();
                        intent.setAction(UpdateArticleItemReceiver.UPDATE_ARTICLE_ACTION);
                        intent.putExtra("mid", article_id);
                        intent.putExtra("COMMAND", UpdateArticleItemReceiver.ACTION_COMMENT_ADD);
                        ArticleDetailActivity.this.sendBroadcast(intent);
                        //添加数据到当前的评论列表
                        CommentData data = new CommentData();
                        data.content = commentRequest.content;
                        data.create_time = System.currentTimeMillis() / 1000;
                        data.email = config.email().get();
                        data.head = config.head().get();
                        data.nickname = config.nickname().get();
                        commentAdapter.addCommentData(data);
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
        else{
            LoginOrRegisterActivity_.intent(this).start();
        }
    }

    @Click(R.id.fat_praise)
    void onPraiseClick(){
        if(config.email().exists()) {
            CommentRequest commentRequest = new CommentRequest();
            commentRequest.type = Const.PRAISE;
            commentRequest.mid = article_id;
            commentRequest.email = config.email().get();
            progressDialog.show();
            HttpNetWork.getInstance().request(commentRequest, new HttpNetWork.NetResponseCallback() {
                @Override
                public void onResponse(DHttpRequestBase request, HttpResponsePacket response) {
                    progressDialog.cancel();
                    if(response.code == 0){
                        Intent intent = new Intent();
                        intent.setAction(UpdateArticleItemReceiver.UPDATE_ARTICLE_ACTION);
                        intent.putExtra("mid", article_id);
                        if(isPraise == 1){
                            isPraise = 0;
                            intent.putExtra("COMMAND", UpdateArticleItemReceiver.ACTION_PRAISE_CANCEL);
                            fat_praise.setIcon("fa-heart-o");
                            fat_praise.setTextColor(getResources().getColor(R.color.white));
                        }
                        else{
                            isPraise = 1;
                            intent.putExtra("COMMAND", UpdateArticleItemReceiver.ACTION_PRAISE_ADD);
                            fat_praise.setIcon("fa-heart");
                            fat_praise.setTextColor(getResources().getColor(R.color.red));
                        }
                        ArticleDetailActivity.this.sendBroadcast(intent);
                        requestPraiseFromNet();
                    }
                    ToastUtil.showToast(response.message);
                }
            }, new HttpNetWork.NetErrorCallback() {
                @Override
                public void onError(DHttpRequestBase request, String errorMsg) {
                    progressDialog.cancel();
                    ToastUtil.showToast("网络或服务器错误");
                }
            });
        }
        else{
            LoginOrRegisterActivity_.intent(this).start();
        }
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
        if(!progressDialog.isShowing()){
            progressDialog.show();
        }
        GetArticleRequest articleRequest = new GetArticleRequest();
        articleRequest.mid = article_id;
        articleRequest.email = config.email().getOr("");
        HttpNetWork.getInstance().request(articleRequest, new HttpNetWork.NetResponseCallback() {
            @Override
            public void onResponse(DHttpRequestBase request, HttpResponsePacket response) {
                progressDialog.cancel();
                if (response.code == 0) {
                    ArticleData articleData = response.getData(
                            new TypeToken<ArticleData>() {
                            }.getType()
                    );
                    isPraise = articleData.is_praise;
                    if (articleData.is_praise == 1) {
                        fat_praise.setIcon("fa-heart");
                        fat_praise.setTextColor(getResources().getColor(R.color.red));
                    } else {
                        fat_praise.setIcon("fa-heart-o");
                        fat_praise.setTextColor(getResources().getColor(R.color.white));
                    }
                    tv_content.setText(Html.fromHtml(articleData.content));
                    tv_user_name.setText(articleData.nickname);
                    tv_time.setText(TimeUtil.getTimeStringBySeconds(articleData.create_time));
                    Uri uri = MyApplication.getImageHostUri(articleData.head);
                    int head_size = getResources().getDimensionPixelSize(R.dimen.article_detail_head_size);
                    sdv_user_head.setController(ImageUtil.getControllerWithSize(
                            sdv_user_head.getController(), uri, head_size, head_size
                    ));
                    loadImage(articleData.pic);
                } else {
                    onLoadDataFailure(response.message);
                }
            }
        }, new HttpNetWork.NetErrorCallback() {
            @Override
            public void onError(DHttpRequestBase request, String errorMsg) {
                progressDialog.cancel();
                onLoadDataFailure("网络错误");
            }
        });
    }

    /**
     * 设置赞的列表
     */
    void setUpPraiseRecycleView(){
        getPraiseRequest.mid = article_id;
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        //绑定适配器
        rv_praise.setAdapter(adapter);
        rv_praise.setLayoutManager(layoutManager);
        requestPraiseFromNet();
    }

    void requestPraiseFromNet(){
        HttpNetWork.getInstance().request(getPraiseRequest, new HttpNetWork.NetResponseCallback() {
            @Override
            public void onResponse(DHttpRequestBase request, HttpResponsePacket response) {
                if (response.code == 0) {
                    List<CommentData> dataList = response.getData(
                            new TypeToken<List<CommentData>>() {
                            }.getType()
                    );
                    adapter.setPraiseAdapter(dataList);
                }
            }
        }, new HttpNetWork.NetErrorCallback() {
            @Override
            public void onError(DHttpRequestBase request, String errorMsg) {
            }
        });
    }

    /**
     * 设置评论列表
     */
    void setCommentList(){
        lv_comment.setAdapter(commentAdapter);
        GetMovingReplyRequest replyRequest = new GetMovingReplyRequest();
        replyRequest.mid = article_id;
        HttpNetWork.getInstance().request(replyRequest, new HttpNetWork.NetResponseCallback() {
            @Override
            public void onResponse(DHttpRequestBase request, HttpResponsePacket response) {
                if(response.code == 0){
                    List<CommentData> commentDataList = response.getData(
                            new TypeToken<List<CommentData>>(){}.getType()
                    );
                    commentAdapter.setCommentDataList(commentDataList);
                }
            }
        }, new HttpNetWork.NetErrorCallback() {
            @Override
            public void onError(DHttpRequestBase request, String errorMsg) {                }
        });
    }

    void onLoadDataFailure(String message){
        ToastUtil.showToast(message);
        finish();
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
        sdv_card_head.setController(DDImageUtil.getControllerBySize(
                sdv_card_head, uri, SizeUtil.getScreenWidth(), SizeUtil.getScreenHeight(), postprocessor
        ));
    }

    @Background
    void startBlur(Bitmap bitmap){
        Bitmap newBitmap = ImageUtil.getBlurImage(bitmap, 2, 8);
        setBlur(newBitmap);
    }

    @UiThread
    void setBlur(Bitmap blur){
        iv_blur_bg.setImageBitmap(blur);
    }

    @Override
    public void onResponse(BaseResponse baseResponse) {
        if(BuildConfig.DEBUG) {
            Log.e("fuck", "error code: " + baseResponse.errCode + " error message" + baseResponse.errMsg);
        }
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