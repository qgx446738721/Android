package org.voiddog.mblog.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.FontAwesomeText;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;
import org.voiddog.mblog.R;

/**
 * 头部Bar布局
 * Created by Dog on 2015/4/26.
 */
@EViewGroup(R.layout.title_bar)
public class TitleBar extends FrameLayout{

    @ViewById
    FontAwesomeText fat_left, fat_right;
    @ViewById
    TextView tv_title;

    int leftIconColor, rightIconColor, titleColor;
    Drawable titleBg;
    String leftText, leftIcon, rightText, rightIcon, title;

    public TitleBar(Context context) {
        this(context, null, 0);
    }

    public TitleBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.TitleBar, defStyleAttr, 0);

        leftIconColor = a.getColor(R.styleable.TitleBar_leftColor, 0xffffffff);
        leftIcon = a.getString(R.styleable.TitleBar_leftIcon);
        leftText = a.getString(R.styleable.TitleBar_leftText);

        rightIconColor = a.getColor(R.styleable.TitleBar_rightColor, 0xffffffff);
        rightIcon = a.getString(R.styleable.TitleBar_rightIcon);
        rightText = a.getString(R.styleable.TitleBar_rightText);

        titleBg = a.getDrawable(R.styleable.TitleBar_titleBg);
        title = a.getString(R.styleable.TitleBar_titleText);
        titleColor = a.getColor(R.styleable.TitleBar_titleColor, 0xffffffff);

        a.recycle();
    }

    @AfterViews
    void initViews(){
        //左边
        if(leftIcon == null && leftText == null){
            fat_left.setVisibility(View.GONE);
        }
        if(leftIcon != null){
            fat_left.setIcon(leftIcon);
        }
        if(leftText != null){
            fat_left.setText(leftText);
        }
        fat_left.setTextColor(leftIconColor);

        //标题
        if(titleBg != null){
            setBackground(titleBg);
        }
        tv_title.setTextColor(titleColor);
        if(title != null){
            tv_title.setText(title);
        }

        //右边
        if(rightIcon == null && rightText == null){
            fat_right.setVisibility(View.GONE);
        }
        if(rightIcon != null){
            fat_right.setIcon(rightIcon);
        }
        if(rightText != null){
            fat_right.setText(rightText);
        }
        fat_right.setTextColor(rightIconColor);
    }

    /**
     * 设置点击左边按钮事件
     * @param clickListener 点击接口
     */
    public void setOnLeftClickListener(OnClickListener clickListener){
        fat_left.setOnClickListener(clickListener);
    }

    /**
     * 设置点击右边按钮事件
     * @param clickListener 点击接口
     */
    public void setOnRightClickListener(OnClickListener clickListener){
        fat_right.setOnClickListener(clickListener);
    }

    /**
     * 设置左边按钮的text
     * @param text 内容
     */
    public void setLeftText(String text){
        fat_left.setText(text);
    }

    /**
     * 设置左边按钮的图标
     * @param icon 图标编号 参见http://fortawesome.github.io/Font-Awesome/icons/
     *             例: fa-arrow-left
     */
    public void setLeftIcon(String icon){
        fat_left.setIcon(icon);
    }

    /**
     * 设置右边按钮的text
     * @param text 内容
     */
    public void setRightText(String text){
        fat_right.setText(text);
    }

    /**
     * 设置右边按钮的图片
     * @param icon 图标编号 参见http://fortawesome.github.io/Font-Awesome/icons/
     *             例: fa-arrow-right
     */
    public void setRightIcon(String icon){
        fat_right.setIcon(icon);
    }

    /**
     * 这只标题内容
     * @param text 内容
     */
    public void setTitle(String text){
        tv_title.setText(text);
    }
}
