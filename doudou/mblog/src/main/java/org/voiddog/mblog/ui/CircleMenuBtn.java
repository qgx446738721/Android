package org.voiddog.mblog.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.widget.RelativeLayout;

import com.beardedhen.androidbootstrap.FontAwesomeText;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.AnimationRes;
import org.voiddog.lib.util.SizeUtil;
import org.voiddog.mblog.R;

/**
 * 圆形按钮
 * Created by Dog on 2015/5/31.
 */
@EViewGroup(R.layout.ui_main_menu)
public class CircleMenuBtn extends RelativeLayout{

    @ViewById
    FontAwesomeText fat_plus;
    @AnimationRes
    Animation reduce, turn_round;

    OnClickListener clickListener;

    public CircleMenuBtn(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initRootView();
    }

    public CircleMenuBtn(Context context) {
        super(context);
        initRootView();
    }

    public CircleMenuBtn(Context context, AttributeSet attrs) {
        super(context, attrs);
        initRootView();
    }

    void initRootView(){
        int padding = SizeUtil.dp2px(getContext(), 5);
        setPadding(padding, padding, padding, padding);
        setBackgroundResource(R.drawable.radio_shadow);
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        this.clickListener = l;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:{
                startAnimation(reduce);
                return true;
            }
            case MotionEvent.ACTION_UP:{
                startAnimation(turn_round);
                turn_round.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        if(clickListener != null){
                            clickListener.onClick(CircleMenuBtn.this);
                        }
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
                break;
            }
        }
        return super.onTouchEvent(event);
    }
}
