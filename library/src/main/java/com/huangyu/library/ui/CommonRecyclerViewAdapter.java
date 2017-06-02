package com.huangyu.library.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
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

    protected SparseBooleanArray mSelectArray;

    public CommonRecyclerViewAdapter(Context context) {
        mContext = context;
        mDataList = new ArrayList<>();
        mSelectArray = new SparseBooleanArray();
    }

    public void setData(List<T> list) {
        if (list == null) {
            return;
        }
        mDataList.addAll(list);
        notifyDataSetChanged();
    }

    public void addItem(T data) {
        mDataList.add(data);
        notifyDataSetChanged();
    }

    public void addItem(T data, int position) {
        mDataList.add(position, data);
        notifyItemInserted(position);
    }

    public void removeItem(int positon) {
        mDataList.remove(positon);
        notifyItemRemoved(positon);
    }

    public void clearData(boolean ifClearSelected) {
        mDataList.clear();
        if (ifClearSelected) {
            clearSelectedState();
        }
        notifyDataSetChanged();
    }

    public boolean isSelected(int position) {
        return getSelectedItemList().contains(position);
    }

    public void switchSelectedState(int position) {
        if (mSelectArray.get(position, false)) {
            mSelectArray.delete(position);
        } else {
            mSelectArray.put(position, true);
        }
        notifyItemChanged(position);
    }

    public void clearSelectedState() {
        List<Integer> selection = getSelectedItemList();
        mSelectArray.clear();
        for (Integer i : selection) {
            notifyItemChanged(i);
        }
    }

    public int getSelectedItemCount() {
        return mSelectArray.size();
    }

    public List<Integer> getSelectedItemList() {
        List<Integer> itemList = new ArrayList<>(mSelectArray.size());
        for (int i = 0; i < mSelectArray.size(); ++i) {
            itemList.add(mSelectArray.keyAt(i));
        }
        return itemList;
    }

    public List<T> getSelectedDataList() {
        List<T> dataList = new ArrayList<>(mSelectArray.size());
        for (int i = 0; i < mSelectArray.size(); ++i) {
            dataList.add(mDataList.get(mSelectArray.keyAt(i)));
        }
        return dataList;
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
                    CommonRecyclerViewHolder holder = (CommonRecyclerViewHolder) v.getTag();
                    mOnItemClick.onItemClick(v, holder.getLayoutPosition());
                }
            });
        }
        if (mOnItemLongClick != null) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
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
