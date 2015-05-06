package org.voiddog.mblog.util;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.voiddog.mblog.R;

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
}
