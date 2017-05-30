package com.huangyu.library.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView通用Adapter
 * Created by huangyu on 2017-4-12.
 */
public abstract class CommonRecyclerViewAdapter<T> extends RecyclerView.Adapter<CommonRecyclerViewHolder> {

    protected Context mContext;

    protected List<T> mDataList;

    protected OnItemClickListener mOnItemClick;
    protected OnItemLongClickListener mOnItemLongClick;

    protected int mSelectPosition;

    public CommonRecyclerViewAdapter(Context context) {
        mContext = context;
        mDataList = new ArrayList<>();
        mSelectPosition = -1;
    }

    public void setData(List<T> list) {
        if (list == null) {
            return;
        }
        mDataList.addAll(list);
        beforeNotify();
        notifyDataSetChanged();
    }

    public void addItem(T data) {
        mDataList.add(data);
        beforeNotify();
        notifyDataSetChanged();
    }

    public void addItem(T data, int position) {
        mDataList.add(position, data);
        beforeNotify();
        notifyItemInserted(position);
    }

    public void removeItem(int positon) {
        mDataList.remove(positon);
        beforeNotify();
        notifyItemRemoved(positon);
    }

    public void clearData() {
        mDataList.clear();
        beforeNotify();
        notifyDataSetChanged();
    }

    public void beforeNotify() {
        mSelectPosition = -1;
    }

    public void setOnItemClick(OnItemClickListener onItemClick) {
        this.mOnItemClick = onItemClick;
    }

    public void setOnItemLongClick(OnItemLongClickListener onItemLongClick) {
        this.mOnItemLongClick = onItemLongClick;
    }

    @Override
    public CommonRecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        final CommonRecyclerViewHolder holder;

        holder = CommonRecyclerViewHolder.getViewHolder(viewGroup, getLayoutResource());

        if (mOnItemClick != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSelectPosition = -1;
                    CommonRecyclerViewHolder holder = (CommonRecyclerViewHolder) v.getTag();
                    mOnItemClick.onItemClick(v, holder.getLayoutPosition());
                }
            });
        }
        if (mOnItemLongClick != null) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mSelectPosition = holder.getAdapterPosition();
                    holder.itemView.setSelected(true);

                    CommonRecyclerViewHolder holder = (CommonRecyclerViewHolder) v.getTag();
                    mOnItemLongClick.onItemLongClick(v, holder.getLayoutPosition());
                    return true;
                }
            });
        }

        holder.itemView.setTag(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(CommonRecyclerViewHolder holder, int position) {
        if (holder != null && position < mDataList.size()) {
            convert(holder, mDataList.get(position), position);
        }
    }

    public abstract void convert(CommonRecyclerViewHolder holder, T data, int position);

    public abstract int getLayoutResource();

    public T getItem(int position) {
        if (position < 0 || position >= mDataList.size()) {
            return null;
        }
        return (mDataList == null || mDataList.isEmpty()) ? null : mDataList.get(position);
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(View view, int position);
    }

}
