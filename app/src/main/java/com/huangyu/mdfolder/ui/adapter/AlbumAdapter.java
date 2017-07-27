/*
 * Copyright © Yan Zhenjie. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.huangyu.mdfolder.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.huangyu.library.ui.CommonRecyclerViewAdapter;
import com.huangyu.library.ui.CommonRecyclerViewHolder;
import com.huangyu.mdfolder.R;
import com.huangyu.mdfolder.bean.FileItem;
import com.huangyu.mdfolder.ui.activity.FileListActivity;

import java.util.ArrayList;

/**
 * Created by huangyu on 2017/6/30.
 */
public class AlbumAdapter extends CommonRecyclerViewAdapter<FileItem> {

    public ArrayList<FileItem> mSelectedFileList;

    public AlbumAdapter(Context context) {
        super(context);
    }

    @Override
    public void convert(CommonRecyclerViewHolder holder, FileItem fileItem, int position) {
        ImageView ivImage = holder.getView(R.id.iv_image);
        TextView tvName = holder.getView(R.id.tv_name);
        TextView tvSize = holder.getView(R.id.tv_size);

        FileListActivity activity = (FileListActivity) mContext;
        if (activity.isLightMode()) {
            holder.itemView.setBackgroundResource(R.drawable.select_item);
            tvName.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryText));
            tvSize.setTextColor(mContext.getResources().getColor(R.color.colorSecondaryText));
            if (fileItem.getImages() != null && fileItem.getImages().size() > 0) {
                FileItem f = fileItem.getImages().get(0);
                if (f != null) {
                    Glide.with(mContext).load(f.getPath()).into(ivImage);
                }
            }
        } else {
            holder.itemView.setBackgroundResource(R.drawable.select_item_dark);
            tvName.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryTextWhite));
            tvSize.setTextColor(mContext.getResources().getColor(R.color.colorSecondaryTextWhite));
            if (fileItem.getImages() != null && fileItem.getImages().size() > 0) {
                FileItem f = fileItem.getImages().get(0);
                if (f != null) {
                    Glide.with(mContext).load(f.getPath()).into(ivImage);
                }
            }
        }

        tvName.setText(fileItem.getName());
        String fileRemark = fileItem.getRemark();
        if (!TextUtils.isEmpty(fileRemark)) {
            tvName.append(" (" + fileRemark + ")");
        }
        tvSize.setText(String.valueOf(fileItem.getImages().size()));

        if (getSelectedItemCount() > 0 && isSelected(position) && isSelected(fileItem)) {
            ivImage.setImageAlpha(50);
            holder.itemView.setSelected(true);
        } else {
            ivImage.setImageAlpha(255);
            holder.itemView.setSelected(false);
        }
    }

    @Override
    public int getLayoutResource() {
        return R.layout.item_album_folder;
    }

    private boolean isSelected(FileItem fileItem) {
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
            for (FileItem selectFile : mSelectedFileList) {
                if (fileItem.getPath().equals(selectFile.getPath())) {
                    return true;
                }
            }
            return false;
        }
    }

}
