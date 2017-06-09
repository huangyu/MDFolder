package com.huangyu.mdfolder.app;

import com.huangyu.library.app.BaseConstants;

/**
 * Created by huangyu on 2017-5-26.
 */
public class Constants extends BaseConstants {

    public static final int PERMISSION_ACCESS_FILES = 0x01;

    public static class EditType {

        public static final int NONE = 0;

        public static final int SELECT = 1;

        public static final int COPY = 2;

        public static final int CUT = 3;
    }

    public static class FileType {
        public static final int FILE = 10;

        public static final int PHOTO = 11;

        public static final int MUSIC = 12;

        public static final int VIDEO = 13;

        public static final int APPS = 14;
    }

}
