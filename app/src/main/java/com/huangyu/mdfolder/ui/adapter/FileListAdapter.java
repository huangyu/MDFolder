package com.huangyu.mdfolder.ui.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.huangyu.library.ui.CommonRecyclerViewAdapter;
import com.huangyu.library.ui.CommonRecyclerViewHolder;
import com.huangyu.library.util.FileUtils;
import com.huangyu.mdfolder.R;
import com.huangyu.mdfolder.utils.DateUtils;

import java.io.File;
import java.util.List;

/**
 * Created by huangyu on 2017-5-23.
 */
public class FileListAdapter extends CommonRecyclerViewAdapter<File> {

    public List<File> mSelectedFileList;

    public FileListAdapter(Context context) {
        super(context);
    }

    @Override
    public void convert(CommonRecyclerViewHolder holder, File file, int position) {
        ImageView mIvIcon = holder.getView(R.id.iv_icon);
        TextView mTvName = holder.getView(R.id.tv_name);
        TextView mTvSize = holder.getView(R.id.tv_size);
        TextView mTvTime = holder.getView(R.id.tv_time);
        View mVDivider = holder.getView(R.id.v_divider);

        mTvName.setText(file.getName());

        if (file.isDirectory()) {
            mTvSize.setText(mContext.getString(R.string.str_folder));
            mIvIcon.setImageResource(R.mipmap.ic_folder);
        } else {
            mTvSize.setText(FileUtils.getFileSize(file));
            mIvIcon.setImageResource(R.mipmap.ic_file);
        }

        mTvTime.setText(DateUtils.getFormatDate(file.lastModified()));

        if (position == getItemCount() - 1) {
            mVDivider.setVisibility(View.GONE);
        } else {
            mVDivider.setVisibility(View.VISIBLE);
        }

        if (isSelected(position) && isInSelectPath(position)) {
            holder.itemView.setSelected(true);
        } else {
            holder.itemView.setSelected(false);
        }
    }

    @Override
    public int getLayoutResource() {
        return R.layout.item_file_list;
    }

    /**
     * 是否选中项在当前目录
     *
     * @param position
     * @return
     */
    private boolean isInSelectPath(int position) {
        // 考虑正在选择的情况
        if (mSelectedFileList == null) {
            if (mSelectArray == null || mSelectArray.size() == 0) {
                return false;
            } else {
                return true;
            }
        }
        // 判断路径是否一致
        else {
            String selectPath = mSelectedFileList.get(0).getParent();
            String currentPath = getItem(position).getParent();
            if (selectPath.equals(currentPath)) {
                return true;
            }
            return false;
        }
    }

}
