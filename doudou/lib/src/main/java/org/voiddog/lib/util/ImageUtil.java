package org.voiddog.lib.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.view.View;

/**
 * 图像处理库
 * Created by Dog on 2015/5/1.
 */
public class ImageUtil {
    /**
     * 模糊图片处理
     * @param in 输入的图片
     * @param radius 高斯模糊的半径
     * @return 模糊完后的图片
     */
    public static void blur(final Bitmap in, final View view, final Context context,
                            final float scaleFactor, final float radius){
        Bitmap overlay = Bitmap.createBitmap((int) (view.getMeasuredWidth()/scaleFactor),
                (int) (view.getMeasuredHeight()/scaleFactor), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
        canvas.translate(-view.getLeft()/scaleFactor, -view.getTop()/scaleFactor);
        canvas.scale(1 / scaleFactor, 1 / scaleFactor);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(in, 0, 0, paint);

        RenderScript rs = RenderScript.create(context);

        Allocation overlayAlloc = Allocation.createFromBitmap(rs, overlay);
        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rs, overlayAlloc.getElement());

        blur.setInput(overlayAlloc);

        blur.setRadius(radius);

        blur.forEach(overlayAlloc);

        overlayAlloc.copyTo(overlay);

        view.setBackground(new BitmapDrawable(context.getResources(), overlay));
    }

    public static Bitmap getBlurImage(Bitmap in, Context context){
        float scaleFactor = 8;
        float radius = 2;
        Bitmap overlay = Bitmap.createBitmap((int) (in.getWidth()/scaleFactor),
                (int) (in.getHeight()/scaleFactor), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
        canvas.scale(1 / scaleFactor, 1 / scaleFactor);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(in, 0, 0, paint);

        RenderScript rs = RenderScript.create(context);

        Allocation overlayAlloc = Allocation.createFromBitmap(rs, overlay);
        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rs, overlayAlloc.getElement());

        blur.setInput(overlayAlloc);

        blur.setRadius(radius);

        blur.forEach(overlayAlloc);

        overlayAlloc.copyTo(overlay);
        return overlay;
    }
}
