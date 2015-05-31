package org.voiddog.mblog.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import org.voiddog.mblog.data.ArticleData;
import org.voiddog.mblog.data.CommentData;

/**
 * 有新的动态发布
 * Created by Dog on 2015/5/31.
 */
public abstract class UpdateArticleItemReceiver extends BroadcastReceiver{
    public static String UPDATE_ARTICLE_ACTION = "org.voiddog.update_article";
    public static final int ACTION_ADD = 1;
    public static final int ACTION_PRAISE_ADD = 2;
    public static final int ACTION_PRAISE_CANCEL = 3;
    public static final int ACTION_COMMENT_ADD = 4;

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(UPDATE_ARTICLE_ACTION)) {
            int command = intent.getIntExtra("COMMEND", 0);
            switch (command){
                case ACTION_ADD:{
                    Bundle bundle = intent.getExtras();
                    if (bundle != null) {
                        ArticleData articleData = (ArticleData) bundle.get("data");
                        if (articleData != null) {
                            onReceiveArticleData(articleData);
                        }
                    }
                    break;
                }
                case ACTION_PRAISE_ADD:{
                    int mid = intent.getIntExtra("mid", -1);
                    if(mid != -1){
                        onPraiseArticle(mid, true);
                    }
                    break;
                }
                case ACTION_PRAISE_CANCEL:{
                    int mid = intent.getIntExtra("mid", -1);
                    if(mid != -1){
                        onPraiseArticle(mid, false);
                    }
                    break;
                }
                case ACTION_COMMENT_ADD:{
                    Bundle bundle = intent.getExtras();
                    if (bundle != null) {
                        CommentData commentData = (CommentData) bundle.get("data");
                        int mid = intent.getIntExtra("mid", -1);
                        if (commentData != null && mid != -1) {
                            onCommentAdd(mid, commentData);
                        }
                    }
                    break;
                }
            }
        }
    }

    protected abstract void onCommentAdd(int mid, CommentData data);

    /**
     * 更新某一条动态的赞
     * @param mid 动态id
     * @param add 是赞还是取消赞
     */
    protected abstract void onPraiseArticle(int mid, boolean add);

    /**
     * 获取到新的动态
     * @param articleData 新的动态
     */
    protected abstract void onReceiveArticleData(ArticleData articleData);
}
