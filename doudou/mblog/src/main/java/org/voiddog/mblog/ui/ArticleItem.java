package org.voiddog.mblog.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.facebook.drawee.view.SimpleDraweeView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;
import org.voiddog.lib.ui.CustomFontTextView;
import org.voiddog.lib.util.ImageCacheManager;
import org.voiddog.lib.util.SizeUtil;
import org.voiddog.mblog.MyApplication;
import org.voiddog.mblog.R;
import org.voiddog.mblog.adapter.ArticleListAdapter;

/**
 * 主页文章item view
 * Created by Dog on 2015/4/6.
 */
@EViewGroup(R.layout.article_list_item)
public class ArticleItem extends RelativeLayout{

    @ViewById
    SimpleDraweeView sdv_card_head;
    @ViewById
    TextView tv_sub_title;
    @ViewById
    CustomFontTextView cf_tv_title;

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
        sdv_card_head.setAspectRatio(2f);
    }

    public void bind(final ArticleListAdapter.Data data){
        cf_tv_title.setText(data.title);
        tv_sub_title.setText(data.subtitle);
        if(data.image != null){
            sdv_card_head.setImageURI(MyApplication.getImageHostUri(data.image));
        }
    }
}
