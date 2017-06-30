package com.huangyu.mdfolder.app;

import com.huangyu.library.app.BaseApplication;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumConfig;
import com.yanzhenjie.album.task.LocalImageLoader;

import java.util.Locale;

/**
 * Created by huangyu on 2017-5-23.
 */
public class AppApplication extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        Album.initialize(new AlbumConfig.Build()
                .setImageLoader(new LocalImageLoader())
                .setLocale(Locale.getDefault())
                .build()
        );
    }

}
