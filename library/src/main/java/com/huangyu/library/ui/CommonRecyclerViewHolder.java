package com.huangyu.library.ui;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * RecyclerView通用ViewHolder
 * Created by huangyu on 2017-4-12.
 */
public class CommonRecyclerViewHolder extends RecyclerView.ViewHolder {

    private SparseArray<View> mView;

    private CommonRecyclerViewHolder(View itemView) {
        super(itemView);
        this.mView = new SparseArray<>();
    }

    public static CommonRecyclerViewHolder getViewHolder(ViewGroup parent, int layoutId) {
        return new CommonRecyclerViewHolder(LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false));
    }

    public <T extends View> T getView(int viewId) {
        View view = mView.get(viewId);
        if (view == null) {
            view = itemView.findViewById(viewId);
            mView.put(viewId, view);
        }
        return (T) view;
    }

}
