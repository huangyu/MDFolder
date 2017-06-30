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
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.huangyu.library.ui.CommonRecyclerViewAdapter;
import com.huangyu.library.ui.CommonRecyclerViewHolder;
import com.huangyu.library.util.FileUtils;
import com.huangyu.mdfolder.R;
import com.huangyu.mdfolder.bean.FileItem;
import com.huangyu.mdfolder.ui.activity.FileListActivity;

/**
 * Created by huangyu on 2017/6/30.
 */
public class AlbumImageAdapter extends CommonRecyclerViewAdapter<FileItem> {

    public AlbumImageAdapter(Context context) {
        super(context);
    }

    @Override
    public void convert(CommonRecyclerViewHolder holder, FileItem data, int position) {
        ImageView ivImage = holder.getView(R.id.iv_image);
        TextView tvName = holder.getView(R.id.tv_name);
        TextView tvSize = holder.getView(R.id.tv_size);

        FileListActivity activity = (FileListActivity) mContext;
        if (activity.isLightMode()) {
            holder.itemView.setBackgroundResource(R.drawable.select_item);
            tvName.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryText));
            tvSize.setTextColor(mContext.getResources().getColor(R.color.colorSecondaryText));
        } else {
            holder.itemView.setBackgroundResource(R.drawable.select_item_dark);
            tvName.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryTextWhite));
            tvSize.setTextColor(mContext.getResources().getColor(R.color.colorSecondaryTextWhite));
        }

        Glide.with(mContext).load(data.getPath()).into(ivImage);
        tvName.setText(data.getName());
        try {
            tvSize.setText(FileUtils.getFileOrDirSize(Long.valueOf(data.getSize())));
        } catch (Exception e) {
            // 部分机器查询出来的文件大小为空，用文件路径来处理
            tvSize.setText(FileUtils.getFileSize(data.getPath()));
        }
    }

    @Override
    public int getLayoutResource() {
        return R.layout.item_album_image;
    }

}
