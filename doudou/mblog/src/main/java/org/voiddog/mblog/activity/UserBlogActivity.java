package org.voiddog.mblog.activity;

import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.facebook.drawee.view.SimpleDraweeView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.voiddog.lib.ui.BounceListView;
import org.voiddog.lib.ui.CustomFontTextView;
import org.voiddog.mblog.R;
import org.voiddog.mblog.adapter.ArticleListAdapter;
import org.voiddog.mblog.ui.TitleBar;

/**
 * 用户的个人页面
 * String email 查看对象的email
 * Created by Dog on 2015/5/26.
 */
@EActivity(R.layout.activity_user_blog)
public class UserBlogActivity extends AppCompatActivity{
    @ViewById
    BounceListView lv_user_blog;
    @ViewById
    TitleBar title_bar;

    //头部view 部分
    View headView;
    SimpleDraweeView sdv_user_head;
    CustomFontTextView cf_tv_user_name;

    ArticleListAdapter adapter;

    @AfterViews
    void init(){
        initHeadView();
        lv_user_blog.addHeaderView(headView);
        adapter = new ArticleListAdapter();
        lv_user_blog.setAdapter(adapter);

        setUpTitle();
    }

    void initHeadView(){
        headView = View.inflate(this, R.layout.ui_user_blog_head, null);
        sdv_user_head = (SimpleDraweeView) headView.findViewById(R.id.sdv_user_head);
        cf_tv_user_name = (CustomFontTextView) headView.findViewById(R.id.cf_tv_user_name);
    }

    void setUpTitle(){
        title_bar.setOnLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
