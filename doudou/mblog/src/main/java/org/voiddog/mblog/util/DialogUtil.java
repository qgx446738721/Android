package org.voiddog.mblog.util;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;

import org.voiddog.mblog.R;
import org.voiddog.mblog.ui.TakeOrChoseDialogView;
import org.voiddog.mblog.ui.TakeOrChoseDialogView_;

/**
 * Dialog生成类
 * Created by Dog on 2015/5/2.
 */
public class DialogUtil {
    public static Dialog createProgressDialog(Context context){
        Dialog dialog = new Dialog(context, R.style.ProgressDialog);
        View view = View.inflate(context, R.layout.dialog_progress, null);
        dialog.setContentView(view, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        dialog.setCancelable(false);
        return dialog;
    }

    /**
     * 创建选择拍照或者从图片库选取
     * @param context 上下文
     * @param maxChoseNumber 最多选择几张
     * @param requestCode 请求的code
     * @return dialog
     */
    public static Dialog createTakeOrChose(Context context, int maxChoseNumber, int requestCode){
        Dialog dialog = new Dialog(context, R.style.TakeOrChoseDialog);

        TakeOrChoseDialogView view = TakeOrChoseDialogView_.build(context);
        view.setMaxChoseNumber(maxChoseNumber);
        view.setRequestCode(requestCode);

        dialog.setContentView(view, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        view.setDialog(dialog);

        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCanceledOnTouchOutside(true);

        return dialog;
    }
}
