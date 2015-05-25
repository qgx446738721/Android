package org.voiddog.mblog.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;

import org.voiddog.lib.util.SizeUtil;
import org.voiddog.mblog.R;
import org.voiddog.mblog.http.HttpStruct;

import java.util.List;

/**
 * 赞的人的列表
 * Created by Dog on 2015/5/25.
 */
public class PraiseUserRecycleAdapter extends RecyclerView.Adapter<PraiseUserRecycleAdapter.ViewHolder>{
    List<HttpStruct.User> userList;

    public PraiseUserRecycleAdapter(){}

    public void setUserMobile(List<HttpStruct.User> userList){
        this.userList = userList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        RoundingParams roundingParams = RoundingParams.asCircle();
        GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(viewGroup.getResources());
        GenericDraweeHierarchy hierarchy = builder.setFadeDuration(300)
                .setRoundingParams(roundingParams)
                .setPlaceholderImage(viewGroup.getResources().getDrawable(R.mipmap.no_head), ScalingUtils.ScaleType.CENTER_CROP)
                .build();
        hierarchy.setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP);

        SimpleDraweeView view = new SimpleDraweeView(viewGroup.getContext());
        int padding = SizeUtil.dp2px(viewGroup.getContext(), 5);
        int itemSize = viewGroup.getResources().getDimensionPixelOffset(R.dimen.article_detail_praise_height) - (padding << 1);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                // TODO 跳转到个人信息页面
            }
        });

        view.setHierarchy(hierarchy);
        view.setLayoutParams(new RecyclerView.LayoutParams(itemSize, itemSize));
        view.setPadding(padding, padding, padding, padding);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        // TODO 设置头像
    }

    @Override
    public int getItemCount() {
        return userList == null ? 0 : userList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        SimpleDraweeView userHead;

        public ViewHolder(View itemView) {
            super(itemView);
            userHead = (SimpleDraweeView) itemView;
        }
    }
}
