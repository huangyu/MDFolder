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
import com.huangyu.mdfolder.R;
import com.huangyu.mdfolder.bean.FileItem;
import com.huangyu.mdfolder.ui.activity.FileListActivity;

/**
 * Created by huangyu on 2017/6/30.
 */
public class AlbumFolderAdapter extends CommonRecyclerViewAdapter<FileItem> {

    public AlbumFolderAdapter(Context context) {
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
            Glide.with(mContext).load(fileItem.getPath()).into(ivImage);
        } else {
            holder.itemView.setBackgroundResource(R.drawable.select_item_dark);
            tvName.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryTextWhite));
            tvSize.setTextColor(mContext.getResources().getColor(R.color.colorSecondaryTextWhite));
            Glide.with(mContext).load(fileItem.getPath()).into(ivImage);
        }

        tvName.setText(fileItem.getName());
        tvSize.setText(String.valueOf(fileItem.getImages().size()));
    }

    @Override
    public int getLayoutResource() {
        return R.layout.item_album_folder;
    }

}
