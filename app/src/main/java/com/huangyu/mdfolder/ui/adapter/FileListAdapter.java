package com.huangyu.mdfolder.ui.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.huangyu.library.ui.CommonRecyclerViewAdapter;
import com.huangyu.library.ui.CommonRecyclerViewHolder;
import com.huangyu.library.util.FileUtils;
import com.huangyu.mdfolder.R;
import com.huangyu.mdfolder.app.Constants;
import com.huangyu.mdfolder.bean.FileItem;
import com.huangyu.mdfolder.ui.activity.FileListActivity;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by huangyu on 2017-5-23.
 */
public class FileListAdapter extends CommonRecyclerViewAdapter<FileItem> {

    public ArrayList<FileItem> mSelectedFileList;

    public FileListAdapter(Context context) {
        super(context);
    }

    @Override
    public void convert(CommonRecyclerViewHolder holder, FileItem fileItem, int position) {
        ImageView mIvIcon = holder.getView(R.id.iv_icon);
        TextView mTvName = holder.getView(R.id.tv_name);
        TextView mTvSize = holder.getView(R.id.tv_size);
        TextView mTvTime = holder.getView(R.id.tv_time);
        View mVDivider = holder.getView(R.id.v_divider);

        mTvName.setText(fileItem.getName());

        FileListActivity activity = (FileListActivity) mContext;
        if (activity.isLightMode()) {
            holder.itemView.setBackgroundResource(R.drawable.select_item);
            mTvName.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryText));
            mTvSize.setTextColor(mContext.getResources().getColor(R.color.colorSecondaryText));
            mTvTime.setTextColor(mContext.getResources().getColor(R.color.colorSecondaryText));
            mVDivider.setBackgroundResource(R.color.colorDivider);
        } else {
            holder.itemView.setBackgroundResource(R.drawable.select_item_dark);
            mTvName.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryTextWhite));
            mTvSize.setTextColor(mContext.getResources().getColor(R.color.colorSecondaryTextWhite));
            mTvTime.setTextColor(mContext.getResources().getColor(R.color.colorSecondaryTextWhite));
            mVDivider.setBackgroundResource(R.color.colorDividerWhite);
        }

        int fileType = fileItem.getType();
        switch (fileType) {
            case Constants.FileType.FILE:
            case Constants.FileType.DOCUMENT:
                if (fileItem.isDirectory()) {
//                    String size = mContext.getString(R.string.str_folder) + FileUtils.getFileOrDirSize(fileItem.getSize());
                    mTvSize.setText(mContext.getString(R.string.str_folder));
                    if (activity.isLightMode()) {
                        Glide.with(mContext).load(R.mipmap.ic_folder).error(R.mipmap.ic_folder).into(mIvIcon);
                    } else {
                        Glide.with(mContext).load(R.mipmap.ic_folder_white).error(R.mipmap.ic_folder_white).into(mIvIcon);
                    }
                } else {
                    mTvSize.setText(FileUtils.getFileOrDirSize(fileItem.getSize()));
                    if (activity.isLightMode()) {
                        Glide.with(mContext).load(R.mipmap.ic_file).error(R.mipmap.ic_file).into(mIvIcon);
                    } else {
                        Glide.with(mContext).load(R.mipmap.ic_file_white).error(R.mipmap.ic_file_white).into(mIvIcon);
                    }
                }
                break;
            case Constants.FileType.AUDIO:
                mTvSize.setText(FileUtils.getFileOrDirSize(fileItem.getSize()));
                if (activity.isLightMode()) {
                    Glide.with(mContext).load(R.mipmap.ic_view_music).error(R.mipmap.ic_music).into(mIvIcon);
                } else {
                    Glide.with(mContext).load(R.mipmap.ic_view_music_white).error(R.mipmap.ic_music_white).into(mIvIcon);
                }
                break;
            case Constants.FileType.IMAGE:
                mTvSize.setText(FileUtils.getFileOrDirSize(fileItem.getSize()));
                if (activity.isLightMode()) {
                    Glide.with(mContext).load(fileItem.getPath()).error(R.mipmap.ic_view_photo).into(mIvIcon);
                } else {
                    Glide.with(mContext).load(fileItem.getPath()).error(R.mipmap.ic_view_photo_white).into(mIvIcon);
                }
                break;
            case Constants.FileType.VIDEO:
                mTvSize.setText(FileUtils.getFileOrDirSize(fileItem.getSize()));
                if (activity.isLightMode()) {
                    Glide.with(mContext).load(Uri.fromFile(new File(fileItem.getPath()))).error(R.mipmap.ic_view_video).into(mIvIcon);
                } else {
                    Glide.with(mContext).load(Uri.fromFile(new File(fileItem.getPath()))).error(R.mipmap.ic_view_video_white).into(mIvIcon);
                }
                break;
        }

        if (fileType == Constants.FileType.IMAGE || fileType == Constants.FileType.VIDEO) {
            mIvIcon.setColorFilter(null);
        } else {
            if (activity.isLightMode()) {
                mIvIcon.setColorFilter(mContext.getResources().getColor(R.color.colorDarkGray));
            } else {
                mIvIcon.setColorFilter(mContext.getResources().getColor(R.color.colorLightGray));
            }
        }

        mTvTime.setText(fileItem.getDate());

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
