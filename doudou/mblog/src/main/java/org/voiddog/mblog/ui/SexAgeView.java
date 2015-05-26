package org.voiddog.mblog.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.FontAwesomeText;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;
import org.voiddog.mblog.Const;
import org.voiddog.mblog.R;

/**
 * 性别，年龄视图
 * Created by Dog on 2015/5/18.
 */
@EViewGroup(R.layout.ui_sex_age_view)
public class SexAgeView extends LinearLayout{
    @ViewById
    TextView tv_age;
    @ViewById
    FontAwesomeText fat_sex;

    Context mContext;
    int initSex, initAge;

    public SexAgeView(Context context) {
        super(context);
        mContext = context;
        initRootView(null, 0);
    }

    public SexAgeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initRootView(attrs, 0);
    }

    public SexAgeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initRootView(attrs, defStyleAttr);
    }

    /**
     * 设置性别
     * @param sex 1:男 2:女
     */
    public void setSex(int sex){
        //1男 2女
        if(sex == Const.MALE){
            setBackgroundResource(R.drawable.male_bg);
            fat_sex.setIcon("fa-mars");
        }
        else{
            setBackgroundResource(R.drawable.female_bg);
            fat_sex.setIcon("fa-venus");
        }
    }

    /**
     * 设置年龄
     * @param age 年龄
     */
    public void setAge(int age){
        tv_age.setText(Integer.toString(age));
    }

    void initRootView(AttributeSet attrs, int defStyleAttr){
        int contentPadding = getResources().getDimensionPixelSize(R.dimen.sex_age_content_padding);
        setPadding(contentPadding, 0, contentPadding, 0);
        setGravity(Gravity.CENTER_VERTICAL);
        setOrientation(HORIZONTAL);
        if(attrs != null){
            final TypedArray a = mContext.obtainStyledAttributes(
                    attrs, R.styleable.TitleBar, defStyleAttr, 0);
            initSex = a.getInt(R.styleable.SexAgeView_sex, 1);
            initAge = a.getInt(R.styleable.SexAgeView_age, 0);
            a.recycle();
        }
    }

    @AfterViews
    void init(){
        setAge(initAge);
        setSex(initSex);
    }
}
