package org.voiddog.mblog.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.beardedhen.androidbootstrap.FontAwesomeText;

import org.voiddog.mblog.R;
import org.voiddog.mblog.activity.PublishMovingActivity_;

import fr.tvbarthel.lib.blurdialogfragment.SupportBlurDialogFragment;

/**
 * 模糊背景dialog
 * Created by Dog on 2015/5/31.
 */
public class TakeOrChoseDialogFragment extends SupportBlurDialogFragment{

    View rootView;
    FontAwesomeText fat_take, fat_chose;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawableResource(R.color.transparent);
        rootView = inflater.inflate(R.layout.dialog_chose_or_take, container);
        initView();
        return rootView;
    }

    void initView(){
        fat_take = (FontAwesomeText) rootView.findViewById(R.id.fat_take);
        fat_chose = (FontAwesomeText) rootView.findViewById(R.id.fat_chose);

        fat_take.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                PublishMovingActivity_.intent(getActivity())
                        .isCamera(true)
                        .start();
            }
        });

        fat_chose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                PublishMovingActivity_.intent(getActivity())
                        .isCamera(false)
                        .start();
            }
        });
    }

    @Override
    protected int getBlurRadius() {
        // Allow to customize the blur radius factor.
        return 15;
    }

    @Override
    protected boolean isActionBarBlurred() {
        return true;
    }
    @Override
    protected boolean isRenderScriptEnable() {
        // Enable or disable the use of RenderScript for blurring effect
        // Disabled by default.
        return true;
    }
}
