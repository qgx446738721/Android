package org.voiddog.mblog.ui;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;
import org.voiddog.lib.util.ImageUtil;
import org.voiddog.lib.util.StringUtil;
import org.voiddog.mblog.R;
import org.voiddog.mblog.data.ImageFolder;

/**
 * 图片文件夹view
 * Created by Dog on 2015/5/15.
 */
@EViewGroup(R.layout.ui_image_folder_list_item)
public class ImageFolderListViewItem extends LinearLayout{

    @ViewById
    SimpleDraweeView sdv_image_folder;
    @ViewById
    TextView tv_folder_name;

    Context mContext;
    //图像大小
    int imageSize;

    public ImageFolderListViewItem(Context context) {
        super(context);
        mContext = context;
        initRootView();
    }

    public ImageFolderListViewItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initRootView();
    }

    public ImageFolderListViewItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initRootView();
    }

    /**
     * 绑定数据到view
     * @param imageFolder 图片文件夹信息
     */
    public void bindData(ImageFolder imageFolder){
        if(imageFolder == null){
            return;
        }
        Uri uri = StringUtil.getUriFromFilePath(imageFolder.getFirstImagePath());
        sdv_image_folder.setController(ImageUtil.getControllerWithSize(
                sdv_image_folder.getController(), uri, imageSize, imageSize
        ));
        tv_folder_name.setText(String.format("%s (%d)", imageFolder.getName(),
                imageFolder.getCount()));
    }

    /**
     * 初始化root view
     */
    void initRootView(){
        imageSize = mContext.getResources().getDimensionPixelSize(R.dimen.image_folder_item_height);
        setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                imageSize));
        setBackgroundResource(R.color.select_lib_abs_view_bg);
        setOrientation(HORIZONTAL);
    }
}
