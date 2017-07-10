package com.huangyu.mdfolder.ui.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.afollestad.easyvideoplayer.EasyVideoCallback;
import com.afollestad.easyvideoplayer.EasyVideoPlayer;
import com.bumptech.glide.Glide;
import com.huangyu.library.util.FileUtils;
import com.huangyu.mdfolder.R;
import com.huangyu.mdfolder.bean.FileItem;
import com.huangyu.mdfolder.ui.activity.AudioBrowserActivity;

import java.io.File;
import java.util.List;

import butterknife.ButterKnife;

import static butterknife.ButterKnife.findById;

/**
 * Created by huangyu on 2017-6-20.
 */
public class AudioPagerAdapter extends PagerAdapter {

    private Context mContext;
    private List<FileItem> mAudioList;
    private EasyVideoCallback mEasyVideoCallback;

    public AudioPagerAdapter(Context context, List<FileItem> audioList, EasyVideoCallback easyVideoCallback) {
        this.mContext = context;
        this.mAudioList = audioList;
        this.mEasyVideoCallback = easyVideoCallback;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return mAudioList == null ? 0 : mAudioList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup viewGroup, final int position) {
        FileItem fileItem = mAudioList.get(position);
        String path = fileItem.getPath();
        byte[] bytes = fileItem.getBytes();
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_audio_pager, viewGroup, false);
        EasyVideoPlayer easyVideoPlayer = findById(view, R.id.audio_player);
        easyVideoPlayer.setSource(Uri.fromFile(new File(path)));
        easyVideoPlayer.setCallback(mEasyVideoCallback);

        ImageView ivMusic = ButterKnife.findById(view, R.id.iv_view_music);
        if (bytes == null) {
            loadIcon(fileItem, ivMusic);
        } else {
            Glide.with(mContext).load(bytes).into(ivMusic);
        }
        if (((AudioBrowserActivity) mContext).isLightMode()) {
//            ivMusic.setColorFilter(mContext.getResources().getColor(R.color.colorDarkGrey));
            easyVideoPlayer.setBackgroundColor(mContext.getResources().getColor(R.color.colorWhite));
        } else {
//            ivMusic.setColorFilter(mContext.getResources().getColor(R.color.colorLightGrey));
            easyVideoPlayer.setBackgroundColor(mContext.getResources().getColor(R.color.colorDeepDark));
        }
        view.setTag("audio" + position);
        viewGroup.addView(view);
        return view;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
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

}
