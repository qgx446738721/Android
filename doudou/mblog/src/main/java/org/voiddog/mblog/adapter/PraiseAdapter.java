package org.voiddog.mblog.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * 赞的适配器
 * Created by Dog on 2015/5/12.
 */
public class PraiseAdapter extends RecyclerView.Adapter<PraiseAdapter.PraiseViewHolder>{

    @Override
    public PraiseViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(PraiseViewHolder praiseViewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class PraiseViewHolder extends RecyclerView.ViewHolder{

        public PraiseViewHolder(View itemView) {
            super(itemView);
        }
    }
}
