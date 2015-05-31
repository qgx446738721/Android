package org.voiddog.mblog.ui;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;
import org.voiddog.lib.ui.CustomFontTextView;
import org.voiddog.lib.util.SizeUtil;
import org.voiddog.mblog.MyApplication;
import org.voiddog.mblog.R;
import org.voiddog.mblog.activity.UserBlogActivity_;
import org.voiddog.mblog.data.ArticleData;
import org.voiddog.mblog.util.DDImageUtil;

/**
 * 主页文章item view
 * Created by Dog on 2015/4/6.
 */
@EViewGroup(R.layout.article_list_item)
public class ArticleItem extends RelativeLayout{

    @ViewById
    SimpleDraweeView sdv_card_head, sdv_user_head;
    @ViewById
    TextView tv_sub_title, tv_praise_num, tv_comment_num;
    @ViewById
    CustomFontTextView cf_tv_title;
    int headSize;

    ArticleData data;

    public ArticleItem(Context context) {
        super(context);
    }

    public ArticleItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ArticleItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @AfterViews
    void init(){
        headSize = getResources().getDimensionPixelSize(R.dimen.article_item_head_size);
    }

    @Click(R.id.sdv_user_head)
    void onHeadClick(){
        UserBlogActivity_.intent(getContext())
                .tEmail(data.email)
                .start();
    }

    public void bind(final ArticleData data){
        this.data = data;
        cf_tv_title.setText(data.title);
        tv_sub_title.setText(data.sub_title);
        tv_praise_num.setText(Integer.toString(data.praise_num));
        tv_comment_num.setText(Integer.toString(data.comment_num));
        if(data.pic != null){
            Uri uri = MyApplication.getImageHostUri(data.pic);
            sdv_card_head.setController(DDImageUtil.getControllerWithSize(
                    sdv_card_head.getController(), uri, SizeUtil.getScreenWidth(), SizeUtil.getScreenHeight()
            ));
            uri = MyApplication.getImageHostUri(data.head);
            sdv_user_head.setController(DDImageUtil.getControllerWithSize(
                    sdv_user_head.getController(), uri, headSize, headSize
            ));
        }
    }
}
