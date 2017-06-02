package com.huangyu.mdfolder.mvp.view;

import com.huangyu.library.mvp.IBaseView;

/**
 * Created by huangyu on 2017/5/22.
 */

public interface IFileListView extends IBaseView {
    void addTab(String path);
    boolean removeTab();
    void refreshData(boolean ifClearSelected);
}
