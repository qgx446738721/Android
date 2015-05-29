package org.voiddog.mblog.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;

import org.voiddog.mblog.data.ArticleData;
import org.voiddog.mblog.ui.UserArticleItem;
import org.voiddog.mblog.ui.UserArticleItem_;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户的动态列表
 * Created by Dog on 2015/5/30.
 */
public class UserArticleListAdapter extends BaseAdapter{

    List<ArticleData> articleDataList = new ArrayList<>();
    Context mContext;

    public UserArticleListAdapter(Context context){
        mContext = context;
    }

    public void addArticleDataList(List<ArticleData> articleDatas){
        articleDataList.addAll(articleDatas);
        notifyDataSetChanged();
    }

    public void setArticleDataList(List<ArticleData> articleDataList){
        this.articleDataList = articleDataList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return articleDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return articleDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        UserArticleItem articleItem;
        if(convertView == null){
            articleItem = UserArticleItem_.build(mContext);
            articleItem.setLayoutParams(new AbsListView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            ));
        }
        else{
            articleItem = (UserArticleItem) convertView;
        }

        articleItem.bind((ArticleData) getItem(position));

        return articleItem;
    }
}
