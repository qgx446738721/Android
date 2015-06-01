package org.voiddog.mblog.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.voiddog.mblog.data.CommentData;
import org.voiddog.mblog.ui.CommentListItemView;
import org.voiddog.mblog.ui.CommentListItemView_;

import java.util.List;

/**
 * 评论列表
 * Created by Dog on 2015/5/25.
 */
public class CommentAdapter extends BaseAdapter{

    Context mContext;

    List<CommentData> commentDataList;

    public CommentAdapter(Context context){
        mContext = context;
    }

    public void setCommentDataList(List<CommentData> commentDataList){
        this.commentDataList = commentDataList;
        notifyDataSetChanged();
    }

    public void addCommentData(CommentData data){
        commentDataList.add(data);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return commentDataList == null ? 0 : commentDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return commentDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CommentListItemView itemView;
        if(convertView == null){
            itemView = CommentListItemView_.build(mContext);
        }
        else{
            itemView = (CommentListItemView) convertView;
        }

        itemView.bind((CommentData) getItem(position));

        return itemView;
    }
}
