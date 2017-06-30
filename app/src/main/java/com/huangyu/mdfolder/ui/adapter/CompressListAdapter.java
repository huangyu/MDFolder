package com.huangyu.mdfolder.ui.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.huangyu.library.ui.CommonRecyclerViewAdapter;
import com.huangyu.library.ui.CommonRecyclerViewHolder;
import com.huangyu.library.util.FileUtils;
import com.huangyu.mdfolder.R;
import com.huangyu.mdfolder.bean.FileItem;
import com.huangyu.mdfolder.ui.activity.FileListActivity;
import com.huangyu.mdfolder.utils.DateUtils;

/**
 * Created by huangyu on 2017-6-20.
 */
public class CompressListAdapter extends CommonRecyclerViewAdapter<FileItem> {

    public CompressListAdapter(Context context) {
        super(context);
    }

    @Override
    public void convert(CommonRecyclerViewHolder holder, FileItem data, int position) {
        TextView tvName = holder.getView(R.id.tv_name);
        TextView tvSize = holder.getView(R.id.tv_size);
        TextView tvTime = holder.getView(R.id.tv_time);
        View vDivider = holder.getView(R.id.v_divider);

        FileListActivity activity = (FileListActivity) mContext;
        if (activity.isLightMode()) {
            holder.itemView.setBackgroundResource(R.drawable.select_item);
            tvName.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryText));
            tvSize.setTextColor(mContext.getResources().getColor(R.color.colorSecondaryText));
            tvTime.setTextColor(mContext.getResources().getColor(R.color.colorSecondaryText));
            vDivider.setBackgroundResource(R.color.colorDivider);
        } else {
            holder.itemView.setBackgroundResource(R.drawable.select_item_grey);
            tvName.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryTextWhite));
            tvSize.setTextColor(mContext.getResources().getColor(R.color.colorSecondaryTextWhite));
            tvTime.setTextColor(mContext.getResources().getColor(R.color.colorSecondaryTextWhite));
            vDivider.setBackgroundResource(R.color.colorDividerWhite);
        }

        tvName.setText(data.getName());
        tvSize.setText(FileUtils.getFileOrDirSize(Long.valueOf(data.getSize())));
        try {
            tvTime.setText(DateUtils.getFormatDate(Long.valueOf(data.getDate())));
        } catch (Exception e) {
            // rar
            tvTime.setText(data.getDate());
        }

        if (position == getItemCount() - 1) {
            vDivider.setVisibility(View.GONE);
        } else {
            vDivider.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getLayoutResource() {
        return R.layout.item_zip_list;
    }

}
