package org.voiddog.mblog.ui;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;
import org.voiddog.lib.ui.CustomFontTextView;
import org.voiddog.lib.util.SizeUtil;
import org.voiddog.mblog.MyApplication;
import org.voiddog.mblog.R;
import org.voiddog.mblog.http.HttpStruct;

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

    public void bind(final HttpStruct.Article data){
        cf_tv_title.setText(data.title);
        tv_sub_title.setText(data.subtitle);
        if(data.image != null){
            Uri uri = MyApplication.getImageHostUri(data.image);
            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                    .setResizeOptions(new ResizeOptions(SizeUtil.getScreenWidth(), SizeUtil.getScreenHeight()))
                    .build();
            PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                    .setOldController(sdv_card_head.getController())
                    .setImageRequest(request)
                    .build();
            sdv_card_head.setController(controller);
        }
    }
}
