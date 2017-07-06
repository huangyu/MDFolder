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
        String path = mAudioList.get(position).getPath();
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_audio_pager, viewGroup, false);
        EasyVideoPlayer easyVideoPlayer = findById(view, R.id.audio_player);
        easyVideoPlayer.setSource(Uri.fromFile(new File(path)));
        easyVideoPlayer.setCallback(mEasyVideoCallback);

        ImageView ivMusic = ButterKnife.findById(view, R.id.iv_view_music);
        Glide.with(mContext).load(R.mipmap.ic_file).into(ivMusic);
        if (((AudioBrowserActivity) mContext).isLightMode()) {
            ivMusic.setColorFilter(mContext.getResources().getColor(R.color.colorDarkGrey));
            easyVideoPlayer.setBackgroundColor(mContext.getResources().getColor(R.color.colorWhite));
        } else {
            ivMusic.setColorFilter(mContext.getResources().getColor(R.color.colorLightGrey));
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

}
