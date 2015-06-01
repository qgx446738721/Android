package org.voiddog.mblog.ui;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;
import org.voiddog.lib.util.SizeUtil;
import org.voiddog.mblog.MyApplication;
import org.voiddog.mblog.R;
import org.voiddog.mblog.data.ArticleData;
import org.voiddog.mblog.util.DDImageUtil;

/**
 * 用户的全部照片
 * Created by Dog on 2015/5/30.
 */
@EViewGroup(R.layout.ui_user_article_item)
public class UserArticleItem extends LinearLayout{

    @ViewById
    SimpleDraweeView sdv_card_head;
    @ViewById
    TextView tv_card_content, tv_comment_num, tv_praise_num;
    @ViewById
    TextView tv_title, tv_sub_title;

    public UserArticleItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UserArticleItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public UserArticleItem(Context context) {
        super(context);
    }

    @AfterViews
    void init(){
        sdv_card_head.setAspectRatio(1.0f);
    }

    public void bind(ArticleData articleData){
        Uri uri = MyApplication.getImageHostUri(articleData.pic);
        sdv_card_head.setController(DDImageUtil.getControllerWithSize(
                sdv_card_head.getController(), uri, SizeUtil.getScreenWidth(), SizeUtil.getScreenHeight()
        ));
        tv_praise_num.setText(Integer.toString(articleData.praise_num));
        tv_comment_num.setText(Integer.toString(articleData.reply_num));
        tv_card_content.setText(Html.fromHtml(articleData.content));
        tv_title.setText(articleData.title);
        tv_sub_title.setText(articleData.sub_title);
    }
}
