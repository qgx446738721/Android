package org.voiddog.lib.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.EditText;

import org.voiddog.lib.R;
import org.voiddog.lib.util.FontUtil;

/**
 * 自定义字体编辑栏
 * Created by Dog on 2015/5/6.
 */
public class CustomFontEditTextView extends EditText {

    Context context;

    public CustomFontEditTextView(Context context) {
        super(context);
        this.context = context;
        init(null);
    }

    public CustomFontEditTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(attrs);
    }

    public CustomFontEditTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init(attrs);
    }

    void init(AttributeSet attrs){
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
            setTypeface(FontUtil.getFontFace(context.getAssets(), "fonts/" + name));
        } catch (Exception ignore){}
    }
}
