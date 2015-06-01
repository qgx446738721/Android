package org.voiddog.mblog.ui;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;
import org.voiddog.lib.util.ImageUtil;
import org.voiddog.lib.util.TimeUtil;
import org.voiddog.mblog.MyApplication;
import org.voiddog.mblog.R;
import org.voiddog.mblog.activity.UserBlogActivity_;
import org.voiddog.mblog.data.CommentData;

/**
 * 回复列表item view
 * Created by Dog on 2015/5/25.
 */
@EViewGroup(R.layout.ui_comment_list_item)
public class CommentListItemView extends LinearLayout{
    @ViewById
    SimpleDraweeView sdv_user_head;
    @ViewById
    TextView tv_reply_title, tv_reply_content, tv_time;

    CommentData mData;
    int headSize;

    public CommentListItemView(Context context) {
        super(context);
        initRootView();
    }

    public CommentListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initRootView();
    }

    public CommentListItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initRootView();
    }

    public void bind(CommentData data){
        mData = data;
        if(data != null) {
            Uri uri = MyApplication.getImageHostUri(data.head);
            sdv_user_head.setController(ImageUtil.getControllerWithSize(
                            sdv_user_head.getController(),
                            uri, headSize, headSize
            ));
            tv_reply_title.setText(data.nickname);
            tv_reply_content.setText(data.content);
            tv_time.setText(TimeUtil.getTimeStringBySeconds(data.create_time));
        }
    }

    void initRootView(){
        headSize = getResources().getDimensionPixelSize(R.dimen.comment_head_size);
        setLayoutParams(new AbsListView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        setOrientation(HORIZONTAL);

        int padding = getResources().getDimensionPixelOffset(R.dimen.comment_padding);
        setPadding(padding, padding, padding, padding);
    }

    @Click(R.id.sdv_user_head)
    void onHeadClick(){
        if(mData != null){
            UserBlogActivity_.intent(getContext())
                    .tEmail(mData.email)
                    .start();
        }
    }
}
