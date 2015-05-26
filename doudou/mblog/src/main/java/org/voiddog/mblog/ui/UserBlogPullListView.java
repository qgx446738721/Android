package org.voiddog.mblog.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;

import org.voiddog.lib.ui.PullToZoomListViewEx;

/**
 * 用户个人列表
 * Created by Dog on 2015/5/26.
 */
public class UserBlogPullListView extends PullToZoomListViewEx{

    TitleBar title_bar;

    public UserBlogPullListView(Context context) {
        super(context);
        getPullRootView().setDividerHeight(0);
    }

    public UserBlogPullListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getPullRootView().setDividerHeight(0);
    }

    public void setTitle_bar(TitleBar title_bar){
        this.title_bar = title_bar;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        super.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        if(title_bar != null && mHeaderHeight > 0
                && mHeaderContainer != null ) {
            int bottom = mHeaderContainer.getBottom();
            if(bottom > 0 && bottom <= mHeaderHeight) {
                int alpha = (mHeaderHeight - mHeaderContainer.getBottom()) * 0x55 / mHeaderHeight;
                title_bar.setBackgroundColor((alpha << 24));
            }
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        super.onScrollStateChanged(view, scrollState);
    }
}
