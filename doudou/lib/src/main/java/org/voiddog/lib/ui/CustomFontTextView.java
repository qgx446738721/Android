package org.voiddog.lib.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import org.voiddog.lib.R;
import org.voiddog.lib.util.StringUtil;

/**
 * 自定义字体
 * Created by Dog on 2015/4/30.
 */
public class CustomFontTextView extends TextView{

    Context context;

    public CustomFontTextView(Context context) {
        this(context, null);
    }

    public CustomFontTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomFontTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        if(attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomFontTextView);
            String fontFace = a.getString(R.styleable.CustomFontTextView_fontFace);
            setFontPath(fontFace);
            a.recycle();
        }
    }

    /**
     * 设置字体
     * @param name 字体名字，在assest/fonts/*.ttf
     */
    public void setFontPath(String name){
        if(name == null){
            return;
        }
        try {
            setTypeface(StringUtil.getFontFace(context.getAssets(), "fonts/" + name));
        } catch (Exception ignore){}
    }
}
