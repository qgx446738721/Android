package org.voiddog.mblog.fragment;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.EditText;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.voiddog.lib.util.KeyBoardUtils;
import org.voiddog.lib.util.StringUtil;
import org.voiddog.lib.util.ToastUtil;
import org.voiddog.mblog.R;
import org.voiddog.mblog.activity.RegisterActivity;
import org.voiddog.mblog.ui.TitleBar;

/**
 * 注册第一步
 * Created by Dog on 2015/5/26.
 */
@EFragment(R.layout.fragment_register_step_1)
public class RegisterStep1Fragment extends Fragment{
    @ViewById
    TitleBar title_bar;
    @ViewById
    EditText et_email, et_password, et_password_retry;

    RegisterActivity activity;

    @AfterViews
    void init(){
        activity = (RegisterActivity) getActivity();

        setUpTitle();
    }

    /**
     * 设置头部bar
     */
    void setUpTitle(){
        title_bar.setOnLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.prePage();
            }
        });
        title_bar.setOnRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO 检查是否已经注册
                if(!checkInput()){
                    return;
                }
                KeyBoardUtils.closeKeybord(et_email, activity);
                activity.nextPage();
            }
        });
    }

    boolean checkInput(){
        activity.email = et_email.getText().toString();
        if(!StringUtil.isEmail(activity.email)){
            ToastUtil.showToast("邮箱不合法");
            return false;
        }
        activity.password = et_password.getText().toString();
        if(StringUtil.isEmpty(activity.password)){
            ToastUtil.showToast("密码不能为空");
            return false;
        }
        if(!activity.password.equals(et_password_retry.getText().toString())){
            ToastUtil.showToast("两次密码输入不一致");
            return false;
        }
        return true;
    }
}
