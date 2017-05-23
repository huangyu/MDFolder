package com.huangyu.mdfolder.ui.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.huangyu.library.ui.CommonRecyclerViewAdapter;
import com.huangyu.library.ui.CommonRecyclerViewHolder;
import com.huangyu.mdfolder.R;

import java.io.File;

/**
 * Created by huangyu on 2017-5-23.
 */
public class FileListAdapter extends CommonRecyclerViewAdapter<File> {

    private ImageView mIvIcon;
    private TextView mTvName;
    private TextView mTvSize;
    private TextView mTvTime;
    private View mVDivider;

    public FileListAdapter(Context context) {
        super(context);
    }

    @Override
    public void convert(CommonRecyclerViewHolder holder, File data, int position) {
        mIvIcon = holder.getView(R.id.iv_icon);
        mTvName = holder.getView(R.id.tv_name);
        mTvSize = holder.getView(R.id.tv_size);
        mTvTime = holder.getView(R.id.tv_time);
        mVDivider = holder.getView(R.id.v_divider);

        if (position == getItemCount() - 1) {
            mVDivider.setVisibility(View.GONE);
        } else {
            mVDivider.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getLayoutResource() {
        return R.layout.item_file_list;
    }

}
