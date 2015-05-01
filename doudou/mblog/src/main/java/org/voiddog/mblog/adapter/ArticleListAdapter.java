package org.voiddog.mblog.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.voiddog.mblog.R;
import org.voiddog.mblog.ui.ArticleItem;
import org.voiddog.mblog.ui.ArticleItem_;

import java.util.List;

/**
 * 主页文章列表适配器
 * Created by Dog on 2015/4/6.
 */
public class ArticleListAdapter extends BaseAdapter{

    List<Data> dataList = null;

    public ArticleListAdapter(){}

    public ArticleListAdapter(List<Data> dataList){
        this.dataList = dataList;
    }

    public void addAll(List<Data> dataList){
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

        articleItem.bind((Data) getItem(position));

        return articleItem;
    }

    public static class Data{
        public int id = 0;
        public String image;
        public String title;
        public String subtitle;
        public String content;
    }
}
