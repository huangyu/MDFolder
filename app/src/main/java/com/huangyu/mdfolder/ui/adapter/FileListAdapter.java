package com.huangyu.mdfolder.ui.adapter;

import android.content.Context;
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
import com.huangyu.mdfolder.utils.DateUtils;

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
        TextView mTvCount = holder.getView(R.id.tv_count);
        TextView mTvSize = holder.getView(R.id.tv_size);
        TextView mTvTime = holder.getView(R.id.tv_time);
        View mVDivider = holder.getView(R.id.v_divider);

        mTvName.setText(fileItem.getName());

        FileListActivity activity = (FileListActivity) mContext;
        if (activity.isLightMode()) {
            holder.itemView.setBackgroundResource(R.drawable.select_item);
            mTvName.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryText));
            mTvCount.setTextColor(mContext.getResources().getColor(R.color.colorSecondaryText));
            mTvSize.setTextColor(mContext.getResources().getColor(R.color.colorSecondaryText));
            mTvTime.setTextColor(mContext.getResources().getColor(R.color.colorSecondaryText));
            mVDivider.setBackgroundResource(R.color.colorDivider);
        } else {
            holder.itemView.setBackgroundResource(R.drawable.select_item_dark);
            mTvName.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryTextWhite));
            mTvCount.setTextColor(mContext.getResources().getColor(R.color.colorSecondaryTextWhite));
            mTvSize.setTextColor(mContext.getResources().getColor(R.color.colorSecondaryTextWhite));
            mTvTime.setTextColor(mContext.getResources().getColor(R.color.colorSecondaryTextWhite));
            mVDivider.setBackgroundResource(R.color.colorDividerWhite);
        }

        int fileType = fileItem.getType();
        switch (fileType) {
            case Constants.FileType.FILE:
            case Constants.FileType.DOCUMENT:
            case Constants.FileType.SINGLE_DOCUMENT:
            case Constants.FileType.COMPRESS:
            case Constants.FileType.APK:
            case Constants.FileType.INSTALLED:
                if (fileItem.isDirectory()) {
//                    String size = mContext.getString(R.string.str_folder) + FileUtils.getFileOrDirSize(fileItem.getSize());
                    mTvCount.setText(String.valueOf(fileItem.getCount()));
                    mTvSize.setText(mContext.getString(R.string.str_folder));
                    Glide.with(mContext).load(R.mipmap.ic_folder).into(mIvIcon);
                } else {
                    mTvCount.setText("");
                    try {
                        mTvSize.setText(FileUtils.getFileOrDirSize(Long.valueOf(fileItem.getSize())));
                    } catch (Exception e) {
                        // 部分机器查询出来的文件大小为空，用文件路径来处理
                        mTvSize.setText(FileUtils.getFileSize(fileItem.getPath()));
                    }
                    if (fileItem.getIcon() == null) {
                        loadIcon(fileItem, mIvIcon);
                    } else {
                        mIvIcon.setImageDrawable(fileItem.getIcon());
                    }
                }
                break;
            case Constants.FileType.IMAGE:
            case Constants.FileType.SINGLE_IMAGE:
            case Constants.FileType.VIDEO:
            case Constants.FileType.SINGLE_VIDEO:
                mTvCount.setText("");
                try {
                    mTvSize.setText(FileUtils.getFileOrDirSize(Long.valueOf(fileItem.getSize())));
                } catch (Exception e) {
                    // 部分机器查询出来的文件大小为空，用文件路径来处理
                    mTvSize.setText(FileUtils.getFileSize(fileItem.getPath()));
                }
                if (fileItem.getPath() == null) {
                    loadIcon(fileItem, mIvIcon);
                } else {
                    Glide.with(mContext).load(fileItem.getPath()).into(mIvIcon);
                }
                break;
            case Constants.FileType.AUDIO:
            case Constants.FileType.SINGLE_AUDIO:
                mTvCount.setText("");
                try {
                    mTvSize.setText(FileUtils.getFileOrDirSize(Long.valueOf(fileItem.getSize())));
                } catch (Exception e) {
                    // 部分机器查询出来的文件大小为空，用文件路径来处理
                    mTvSize.setText(FileUtils.getFileSize(fileItem.getPath()));
                }
                if (fileItem.getBytes() == null) {
                    loadIcon(fileItem, mIvIcon);
                } else {
                    Glide.with(mContext).load(fileItem.getBytes()).into(mIvIcon);
                }
                break;
        }

        if (fileType == Constants.FileType.IMAGE || fileType == Constants.FileType.SINGLE_IMAGE || fileType == Constants.FileType.AUDIO
                || fileType == Constants.FileType.SINGLE_AUDIO || fileType == Constants.FileType.VIDEO
                || fileType == Constants.FileType.SINGLE_VIDEO || fileType == Constants.FileType.APK || fileType == Constants.FileType.INSTALLED) {
            mIvIcon.setColorFilter(null);
        } else {
            if (activity.isLightMode()) {
                mIvIcon.setColorFilter(mContext.getResources().getColor(R.color.colorDarkGrey));
            } else {
                mIvIcon.setColorFilter(mContext.getResources().getColor(R.color.colorLightGrey));
            }
        }

        mTvTime.setText(DateUtils.getFormatDate(Long.valueOf(fileItem.getDate()) * 1000));

        if (position == getItemCount() - 1) {
            mVDivider.setVisibility(View.GONE);
        } else {
            mVDivider.setVisibility(View.VISIBLE);
        }

        if (getSelectedItemCount() > 0 && isSelected(position) && isSelected(fileItem)) {
            holder.itemView.setSelected(true);
        } else {
            holder.itemView.setSelected(false);
        }
    }

    private void loadIcon(FileItem fileItem, ImageView mIvIcon) {
        if (FileUtils.getSuffix(fileItem.getName()).equals(".aac")) {
            Glide.with(mContext).load(R.mipmap.ic_aac).into(mIvIcon);
        } else if (FileUtils.getSuffix(fileItem.getName()).equals(".jpg")) {
            Glide.with(mContext).load(R.mipmap.ic_jpg).into(mIvIcon);
        } else if (FileUtils.getSuffix(fileItem.getName()).equals(".doc")) {
            Glide.with(mContext).load(R.mipmap.ic_doc).into(mIvIcon);
        } else if (FileUtils.getSuffix(fileItem.getName()).equals(".zip")) {
            Glide.with(mContext).load(R.mipmap.ic_zip).into(mIvIcon);
        } else if (FileUtils.getSuffix(fileItem.getName()).equals(".sys")) {
            Glide.with(mContext).load(R.mipmap.ic_sys).into(mIvIcon);
        } else if (FileUtils.getSuffix(fileItem.getName()).equals(".dwg")) {
            Glide.with(mContext).load(R.mipmap.ic_dwg).into(mIvIcon);
        } else if (FileUtils.getSuffix(fileItem.getName()).equals(".ace")) {
            Glide.with(mContext).load(R.mipmap.ic_dwg).into(mIvIcon);
        } else if (FileUtils.getSuffix(fileItem.getName()).equals(".iso")) {
            Glide.with(mContext).load(R.mipmap.ic_iso).into(mIvIcon);
        } else if (FileUtils.getSuffix(fileItem.getName()).equals(".ini")) {
            Glide.with(mContext).load(R.mipmap.ic_ini).into(mIvIcon);
        } else if (FileUtils.getSuffix(fileItem.getName()).equals(".css")) {
            Glide.with(mContext).load(R.mipmap.ic_css).into(mIvIcon);
        } else if (FileUtils.getSuffix(fileItem.getName()).equals(".aut")) {
            Glide.with(mContext).load(R.mipmap.ic_aut).into(mIvIcon);
        } else if (FileUtils.getSuffix(fileItem.getName()).equals(".js")) {
            Glide.with(mContext).load(R.mipmap.ic_js).into(mIvIcon);
        } else if (FileUtils.getSuffix(fileItem.getName()).equals(".avi")) {
            Glide.with(mContext).load(R.mipmap.ic_avi).into(mIvIcon);
        } else if (FileUtils.getSuffix(fileItem.getName()).equals(".txt")) {
            Glide.with(mContext).load(R.mipmap.ic_txt).into(mIvIcon);
        } else if (FileUtils.getSuffix(fileItem.getName()).equals(".db")) {
            Glide.with(mContext).load(R.mipmap.ic_db).into(mIvIcon);
        } else if (FileUtils.getSuffix(fileItem.getName()).equals(".swf")) {
            Glide.with(mContext).load(R.mipmap.ic_swf).into(mIvIcon);
        } else if (FileUtils.getSuffix(fileItem.getName()).equals(".rss")) {
            Glide.with(mContext).load(R.mipmap.ic_rss).into(mIvIcon);
        } else if (FileUtils.getSuffix(fileItem.getName()).equals(".flac")) {
            Glide.with(mContext).load(R.mipmap.ic_flac).into(mIvIcon);
        } else if (FileUtils.getSuffix(fileItem.getName()).equals(".png")) {
            Glide.with(mContext).load(R.mipmap.ic_png).into(mIvIcon);
        } else if (FileUtils.getSuffix(fileItem.getName()).equals(".cad")) {
            Glide.with(mContext).load(R.mipmap.ic_cad).into(mIvIcon);
        } else if (FileUtils.getSuffix(fileItem.getName()).equals(".pdf")) {
            Glide.with(mContext).load(R.mipmap.ic_pdf).into(mIvIcon);
        } else if (FileUtils.getSuffix(fileItem.getName()).equals(".docx")) {
            Glide.with(mContext).load(R.mipmap.ic_docx).into(mIvIcon);
        } else if (FileUtils.getSuffix(fileItem.getName()).equals(".ps")) {
            Glide.with(mContext).load(R.mipmap.ic_ps).into(mIvIcon);
        } else if (FileUtils.getSuffix(fileItem.getName()).equals(".ai")) {
            Glide.with(mContext).load(R.mipmap.ic_ai).into(mIvIcon);
        } else if (FileUtils.getSuffix(fileItem.getName()).equals(".mpg")) {
            Glide.with(mContext).load(R.mipmap.ic_mpg).into(mIvIcon);
        } else if (FileUtils.getSuffix(fileItem.getName()).equals(".bin")) {
            Glide.with(mContext).load(R.mipmap.ic_bin).into(mIvIcon);
        } else if (FileUtils.getSuffix(fileItem.getName()).equals(".exe")) {
            Glide.with(mContext).load(R.mipmap.ic_exe).into(mIvIcon);
        } else if (FileUtils.getSuffix(fileItem.getName()).equals(".mp3")) {
            Glide.with(mContext).load(R.mipmap.ic_mp3).into(mIvIcon);
        } else if (FileUtils.getSuffix(fileItem.getName()).equals(".xlsx")) {
            Glide.with(mContext).load(R.mipmap.ic_xlsx).into(mIvIcon);
        } else if (FileUtils.getSuffix(fileItem.getName()).equals(".rar")) {
            Glide.with(mContext).load(R.mipmap.ic_rar).into(mIvIcon);
        } else if (FileUtils.getSuffix(fileItem.getName()).equals(".tiff")) {
            Glide.with(mContext).load(R.mipmap.ic_tiff).into(mIvIcon);
        } else if (FileUtils.getSuffix(fileItem.getName()).equals(".dwf")) {
            Glide.with(mContext).load(R.mipmap.ic_dwf).into(mIvIcon);
        } else if (FileUtils.getSuffix(fileItem.getName()).equals(".rtf")) {
            Glide.with(mContext).load(R.mipmap.ic_rtf).into(mIvIcon);
        } else if (FileUtils.getSuffix(fileItem.getName()).equals(".php")) {
            Glide.with(mContext).load(R.mipmap.ic_php).into(mIvIcon);
        } else if (FileUtils.getSuffix(fileItem.getName()).equals(".mov")) {
            Glide.with(mContext).load(R.mipmap.ic_mov).into(mIvIcon);
        } else if (FileUtils.getSuffix(fileItem.getName()).equals(".mkv")) {
            Glide.with(mContext).load(R.mipmap.ic_mkv).into(mIvIcon);
        } else if (FileUtils.getSuffix(fileItem.getName()).equals(".bmp")) {
            Glide.with(mContext).load(R.mipmap.ic_bmp).into(mIvIcon);
        } else if (FileUtils.getSuffix(fileItem.getName()).equals(".mp4")) {
            Glide.with(mContext).load(R.mipmap.ic_mp4).into(mIvIcon);
        } else if (FileUtils.getSuffix(fileItem.getName()).equals(".svg")) {
            Glide.with(mContext).load(R.mipmap.ic_svg).into(mIvIcon);
        } else if (FileUtils.getSuffix(fileItem.getName()).equals(".psd")) {
            Glide.with(mContext).load(R.mipmap.ic_psd).into(mIvIcon);
        } else if (FileUtils.getSuffix(fileItem.getName()).equals(".xls")) {
            Glide.with(mContext).load(R.mipmap.ic_xls).into(mIvIcon);
        } else if (FileUtils.getSuffix(fileItem.getName()).equals(".dmg")) {
            Glide.with(mContext).load(R.mipmap.ic_dmg).into(mIvIcon);
        } else if (FileUtils.getSuffix(fileItem.getName()).equals(".ppt") || FileUtils.getSuffix(fileItem.getName()).equals(".pptx")) {
            Glide.with(mContext).load(R.mipmap.ic_ppt).into(mIvIcon);
        } else if (FileUtils.getSuffix(fileItem.getName()).equals(".html")) {
            Glide.with(mContext).load(R.mipmap.ic_html).into(mIvIcon);
        } else if (FileUtils.getSuffix(fileItem.getName()).equals(".gif")) {
            Glide.with(mContext).load(R.mipmap.ic_gif).into(mIvIcon);
        } else if (FileUtils.getSuffix(fileItem.getName()).equals(".htm")) {
            Glide.with(mContext).load(R.mipmap.ic_htm).into(mIvIcon);
        } else if (FileUtils.getSuffix(fileItem.getName()).equals(".cdr")) {
            Glide.with(mContext).load(R.mipmap.ic_cdr).into(mIvIcon);
        } else if (FileUtils.getSuffix(fileItem.getName()).equals(".eps")) {
            Glide.with(mContext).load(R.mipmap.ic_eps).into(mIvIcon);
        } else if (FileUtils.getSuffix(fileItem.getName()).equals(".java")) {
            Glide.with(mContext).load(R.mipmap.ic_java).into(mIvIcon);
        } else {
            Glide.with(mContext).load(R.mipmap.ic_file).into(mIvIcon);
        }
    }

    @Override
    public int getLayoutResource() {
        return R.layout.item_file_list;
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
