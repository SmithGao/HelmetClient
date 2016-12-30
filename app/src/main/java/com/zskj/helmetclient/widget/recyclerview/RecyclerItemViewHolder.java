package com.zskj.helmetclient.widget.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;

public class RecyclerItemViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
    private MyItemClickListener clickListener;

    /**
     * 不需要需要监听点击事件
     */
    public RecyclerItemViewHolder(View itemView) {
        super(itemView);
    }

    /**
     * 需要监听点击事件
     * @param view
     * @param clickListener
     */
    public RecyclerItemViewHolder(final View view, MyItemClickListener clickListener) {
        super(view);
        this.clickListener = clickListener;
        view.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (clickListener != null) {
            clickListener.onitemClick(v, getLayoutPosition());
        }
    }

}
