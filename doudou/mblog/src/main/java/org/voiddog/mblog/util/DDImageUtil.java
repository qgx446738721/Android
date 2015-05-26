package org.voiddog.mblog.util;

import android.net.Uri;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.facebook.imagepipeline.request.Postprocessor;

import org.voiddog.lib.util.ImageUtil;

/**
 * 图片工具类
 * Created by Dog on 2015/5/26.
 */
public class DDImageUtil extends ImageUtil{

    /**
     * 根据图片大小 获取制定的控制器
     * @param draweeView view视图
     * @param uri 图片链接
     * @param width 图片宽度
     * @param height 图片高度
     * @param postprocessor 进监听器
     * @return 图片加载控制器
     */
    public static PipelineDraweeController getControllerBySize(SimpleDraweeView draweeView, Uri uri,
                                                               int width, int height, Postprocessor postprocessor){
        ImageRequestBuilder builder = ImageRequestBuilder.newBuilderWithSource(uri);
        builder.setResizeOptions(new ResizeOptions(width, height));
        if(postprocessor != null){
            builder.setPostprocessor(postprocessor);
        }
        builder.setAutoRotateEnabled(true);
        ImageRequest request = builder.build();
        PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(draweeView.getController())
                .build();
        return controller;
    }
}
