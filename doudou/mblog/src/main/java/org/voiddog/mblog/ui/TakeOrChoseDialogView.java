package org.voiddog.mblog.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.voiddog.lib.util.SizeUtil;
import org.voiddog.mblog.R;
import org.voiddog.mblog.activity.ChoseImgFromLibActivity_;

/**
 * 拍照或者从相册选取 dialog view
 * Created by Dog on 2015/5/15.
 */
@EViewGroup(R.layout.dialog_take_or_chose)
public class TakeOrChoseDialogView extends LinearLayout{

    Context mContext;
    Dialog dialog;
    int maxChoseNumber = 1;
    int requestCode = 0;

    public TakeOrChoseDialogView(Context context) {
        super(context);
        mContext = context;
        initRoot();
    }

    public TakeOrChoseDialogView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initRoot();

    }

    public TakeOrChoseDialogView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initRoot();
    }

    /**
     * 设置最多选择几张
     * @param maxChoseNumber 最多几张
     */
    public void setMaxChoseNumber(int maxChoseNumber){
        this.maxChoseNumber = maxChoseNumber;
    }

    /**
     * 设置请求页代码
     * @param requestCode
     */
    public void setRequestCode(int requestCode){
        this.requestCode = requestCode;
    }

    /**
     * 设置当前view所在的dialog
     * @param dialog 需要传递的dialog
     */
    public void setDialog(Dialog dialog){
        this.dialog = dialog;
    }

    @Click({R.id.btn_take_photo, R.id.btn_chose_from_library})
    void onButtonClick(View view){
        switch (view.getId()){
            case R.id.btn_take_photo:{
                cancelDialog();
                break;
            }
            case R.id.btn_chose_from_library:{
                cancelDialog();
                ChoseImgFromLibActivity_.intent(mContext)
                        .extra("maxChoseNumber", maxChoseNumber)
                        .startForResult(requestCode);
                ((Activity)mContext).overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.alpha_out);
                break;
            }
        }
    }

    void initRoot(){
        int padding = SizeUtil.dp2px(mContext, 20);
        setOrientation(VERTICAL);
        setPadding(padding, padding >> 1, padding, padding >> 1);
    }

    void cancelDialog(){
        if(dialog != null && dialog.isShowing()){
            dialog.cancel();
        }
    }
}
