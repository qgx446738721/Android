package org.voiddog.mblog.adapter;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;

import org.voiddog.lib.util.ImageUtil;
import org.voiddog.lib.util.SizeUtil;
import org.voiddog.mblog.MyApplication;
import org.voiddog.mblog.R;
import org.voiddog.mblog.activity.UserBlogActivity_;
import org.voiddog.mblog.data.CommentData;

import java.util.ArrayList;
import java.util.List;

/**
 * 赞的适配器
 * Created by Dog on 2015/5/12.
 */
public class PraiseAdapter extends RecyclerView.Adapter<PraiseAdapter.PraiseViewHolder>{
    List<CommentData> commentDataList = new ArrayList<>();
    int itemSize = -1;

    public void setPraiseAdapter(List<CommentData> commentDataList){
        this.commentDataList = commentDataList;
        notifyDataSetChanged();
    }

    @Override
    public PraiseViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        RoundingParams roundingParams = RoundingParams.fromCornersRadius(15.0f);
        GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(viewGroup.getResources());
        GenericDraweeHierarchy hierarchy = builder.setFadeDuration(300)
                .setRoundingParams(roundingParams)
                .setPlaceholderImage(viewGroup.getResources().getDrawable(R.mipmap.no_head), ScalingUtils.ScaleType.CENTER_CROP)
                .build();
        hierarchy.setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP);

        SimpleDraweeView view = new SimpleDraweeView(viewGroup.getContext());
        int padding = SizeUtil.dp2px(viewGroup.getContext(), 5);
        if(itemSize == -1) {
            itemSize = viewGroup.getResources().getDimensionPixelSize(R.dimen.comment_head_size);
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getTag() instanceof String){
                    String email = (String) v.getTag();
                    UserBlogActivity_.intent(v.getContext())
                            .tEmail(email)
                            .start();
                }
            }
        });

        view.setHierarchy(hierarchy);
        view.setLayoutParams(new RecyclerView.LayoutParams(itemSize, itemSize));
        view.setPadding(padding, padding, padding, padding);
        return new PraiseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PraiseViewHolder praiseViewHolder, int i) {
        Uri uri = MyApplication.getImageHostUri(commentDataList.get(i).head);
        praiseViewHolder.userHead.setController(ImageUtil.getControllerWithSize(
                praiseViewHolder.userHead.getController(),
                uri,
                itemSize,
                itemSize
        ));
        praiseViewHolder.userHead.setTag(commentDataList.get(i).email);
    }

    @Override
    public int getItemCount() {
        return commentDataList.size();
    }

    class PraiseViewHolder extends RecyclerView.ViewHolder{

        SimpleDraweeView userHead;

        public PraiseViewHolder(View itemView) {
            super(itemView);
            userHead = (SimpleDraweeView) itemView;
        }
    }
}
