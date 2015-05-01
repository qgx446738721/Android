package org.voiddog.mblog.ui;

import android.content.Context;
import android.util.AttributeSet;

import org.voiddog.lib.ui.PullToZoomScrollViewEx;

/**
 * 文章详情的scroll view
 * Created by Dog on 2015/4/22.
 */
public class ArticlePullToZoomScrollView extends PullToZoomScrollViewEx{
    public ArticlePullToZoomScrollView(Context context) {
        this(context, null);
    }

    public ArticlePullToZoomScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScalingRunnable = new ScalingRunnable();
        ((InternalScrollView) mRootView).setOnScrollViewChangedListener(new OnScrollViewChangedListener() {
            @Override
            public void onInternalScrollChanged(int left, int top, int oldLeft, int oldTop) {
                if (isPullToZoomEnabled() && isParallax()) {
                    float f = mHeaderHeight - mHeaderContainer.getBottom() + mRootView.getScrollY();
                    if ((f > 0.0F) && (f < mHeaderHeight)) {
                        int i = (int) (0.65D * f);
                        mHeaderContainer.scrollTo(0, -i);
                    }
                }
            }
        });
    }
}
