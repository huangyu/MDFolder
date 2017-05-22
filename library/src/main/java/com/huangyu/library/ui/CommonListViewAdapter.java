package com.huangyu.library.ui;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * ListView通用适配器
 * Created by huangyu on 2017-4-12.
 */
public abstract class CommonListViewAdapter<T> extends android.widget.BaseAdapter {

    protected Context mContext;
    protected List<T> mData;

    public CommonListViewAdapter(Context context) {
        super();
        mContext = context;
        this.mData = new ArrayList<>();
    }

    public CommonListViewAdapter(Context context, List<T> mData) {
        super();
        mContext = context;
        if (mData != null) {
            this.mData = mData;
        } else {
            this.mData = new ArrayList<>();
        }
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

    @SuppressWarnings("unchecked")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(getLayoutId(), null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        bindView(convertView, viewHolder, position);
        return convertView;
    }

    @Override
    public T getItem(int position) {
        return mData == null ? null : mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 获取布局id
     *
     * @return layout id
     */
    protected abstract int getLayoutId();

    /**
     * 界面绑定，处理数据
     *
     * @param position
     */
    protected abstract void bindView(View convertView, ViewHolder viewHolder, int position);

    /**
     * 设置数据源
     *
     * @param data
     */
    public void setData(List<T> data) {
        mData = data;
    }

    /**
     * 获取数据源
     *
     * @return
     */
    public List<T> getData() {
        return mData;
    }

    /**
     * 替换数据源
     *
     * @param data
     */
    public void replaceAllData(List<T> data) {
        if (data == null) return;
        mData.clear();
        mData.addAll(data);
        notifyDataSetChanged();
    }

    /**
     * 增加单个数据
     *
     * @param data
     */
    public void add(T data) {
        if (data == null) {
            return;
        }
        if (mData == null) {
            mData = new ArrayList<>();
        }
        mData.add(data);
        notifyDataSetChanged();
    }

    /**
     * 增加单个数据
     *
     * @param data
     */
    public void add(int position, T data) {
        if (data == null) {
            return;
        }
        if (mData == null) {
            mData = new ArrayList<>();
        }
        mData.add(position, data);
        notifyDataSetChanged();
    }

    /**
     * 增加数据源
     *
     * @param d
     */
    public void addAll(List<T> d) {
        if (d == null) return;
        if (mData == null) mData = new ArrayList<>();
        mData.addAll(d);
        notifyDataSetChanged();
    }

    /**
     * 移除某个数据源
     *
     * @param position
     */
    public void remove(int position) {
        if (mData == null) {
            return;
        }
        mData.remove(position);
        notifyDataSetChanged();
    }

    /**
     * 自定义通用的ViewHolder
     */
    protected class ViewHolder {

        private View mConvertView;
        private SparseArray<View> mViewsArray; // 暂存View

        protected ViewHolder(View convertView) {
            this.mConvertView = convertView;
            mViewsArray = new SparseArray<View>();
        }

        /**
         * 获取布局View
         *
         * @param ViewId view的ID
         * @param <T>
         * @return T extends View
         */
        @SuppressWarnings({"unchecked", "hiding"})
        protected <T extends View> T getView(int ViewId) {
            View view = mViewsArray.get(ViewId);
            if (null == view) {
                view = mConvertView.findViewById(ViewId);
                mViewsArray.put(ViewId, view);
            }
            return (T) view;
        }

    }

}
