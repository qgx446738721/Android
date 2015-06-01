package org.voiddog.mblog.adapter;

import android.content.Context;
import android.content.IntentFilter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.voiddog.mblog.data.ArticleData;
import org.voiddog.mblog.data.CommentData;
import org.voiddog.mblog.receiver.UpdateArticleItemReceiver;
import org.voiddog.mblog.ui.ArticleItem;
import org.voiddog.mblog.ui.ArticleItem_;

import java.util.Iterator;
import java.util.List;

/**
 * 主页文章列表适配器
 * Created by Dog on 2015/4/6.
 */
public class ArticleListAdapter extends BaseAdapter{

    List<ArticleData> dataList = null;
    MyReceiver receiver;

    public ArticleListAdapter(){
        receiver = new MyReceiver();
    }

    public ArticleListAdapter(List<ArticleData> dataList){
        this.dataList = dataList;
        receiver = new MyReceiver();
    }

    public void setDataList(List<ArticleData> dataList){
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    public void addAll(List<ArticleData> dataList){
        if(this.dataList == null){
            this.dataList = dataList;
        }
        else {
            this.dataList.addAll(dataList);
        }
        notifyDataSetChanged();
    }

    public void clearAll(){
        if(dataList != null){
            dataList.clear();
        }
    }

    public void registerReceiver(Context context){
        IntentFilter filter = new IntentFilter(UpdateArticleItemReceiver.UPDATE_ARTICLE_ACTION);
        context.registerReceiver(receiver, filter);
    }

    public void unRegisterReceiver(Context context){
        try {
            context.unregisterReceiver(receiver);
        }
        catch (Exception ignore){}
    }

    @Override
    public int getCount() {
        if(dataList == null) return 0;
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ArticleItem articleItem;
        if(convertView == null){
            articleItem = ArticleItem_.build(parent.getContext());
        }
        else{
            articleItem = (ArticleItem) convertView;
        }

        articleItem.bind((ArticleData) getItem(position));

        return articleItem;
    }

    ArticleData findArticleById(int mid){
        Iterator iterator = dataList.iterator();
        while(iterator.hasNext()){
            ArticleData articleData = (ArticleData) iterator.next();
            if(articleData.mid == mid){
                return articleData;
            }
        }
        return null;
    }

    class MyReceiver extends UpdateArticleItemReceiver {

        @Override
        protected void onCommentAdd(int mid, CommentData data) {
            ArticleData articleData = findArticleById(mid);
            if(articleData != null){
                articleData.reply_num++;
                notifyDataSetChanged();
            }
        }

        @Override
        protected void onPraiseArticle(int mid, boolean add) {
            ArticleData articleData = findArticleById(mid);
            if(articleData != null){
                if(add) {
                    articleData.praise_num++;
                    articleData.is_praise = 1;
                }
                else{
                    articleData.praise_num--;
                    articleData.is_praise = 0;
                }
                notifyDataSetChanged();
            }
        }

        @Override
        protected void onReceiveArticleData(ArticleData articleData) {
            dataList.add(0, articleData);
            notifyDataSetChanged();
        }
    }
}
