package org.voiddog.mblog.ui;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.facebook.drawee.view.SimpleDraweeView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;
import org.voiddog.lib.util.ImageUtil;
import org.voiddog.lib.util.StringUtil;
import org.voiddog.mblog.R;

/**
 * 可以点击选择的正方形图片，用于图片选择的grid view
 * Created by Dog on 2015/5/15.
 */
@EViewGroup(R.layout.ui_image_select_grid_view_item)
public class ImageSelectGridViewItem extends FrameLayout{

    @ViewById
    SimpleDraweeView sdv_img;
    @ViewById
    ImageView iv_check;

    Context mContext;
    String filePath;

    public ImageSelectGridViewItem(Context context) {
        super(context);
        mContext = context;
        initRootView();
    }

    public ImageSelectGridViewItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initRootView();
    }

    public ImageSelectGridViewItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initRootView();
    }

    /**
     * 点击图片
     */
    public void click(){
        if(iv_check.getVisibility() == VISIBLE){
            iv_check.setVisibility(GONE);
        }
        else{
            iv_check.setVisibility(VISIBLE);
        }
    }

    /**
     * 是否被选中
     * @return true or false
     */
    public boolean isSelect(){
        return iv_check.getVisibility() == VISIBLE;
    }

    /**
     * 获取当前图片的路径
     * @return file path
     */
    public String getFilePath(){
        return filePath;
    }

    /**
     * 绑定数据
     * @param filePath 图片路径
     * @param isCheck 是否check
     * @param imgSize 图片大小
     */
    public void bindData(String filePath, boolean isCheck, int imgSize){
        this.filePath = filePath;
        Uri uri = StringUtil.getUriFromFilePath(filePath);
        sdv_img.setController(ImageUtil.getControllerWithSize(sdv_img.getController(), uri,
                imgSize, imgSize));
        iv_check.getLayoutParams().width = iv_check.getLayoutParams().height = imgSize;
        if(isCheck){
            iv_check.setVisibility(VISIBLE);
        }
        else{
            iv_check.setVisibility(GONE);
        }
    }

    /**
     * 初始化root view
     */
    void initRootView(){
        setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    @AfterViews
    void init(){
        sdv_img.setAspectRatio(1.0f);
    }
}
