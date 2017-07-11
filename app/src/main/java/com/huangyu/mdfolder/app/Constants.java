package com.huangyu.mdfolder.app;

import com.huangyu.library.app.BaseConstants;

/**
 * Created by huangyu on 2017-5-26.
 */
public class Constants extends BaseConstants {

    public static final int PERMISSION_ACCESS_FILES = 0x01;

    public static final int STORAGE_REQUEST_CODE = 0x02;

    public static class EditType {

        public static final int NONE = 0x00000000;

        public static final int SELECT = 0x00000001;

        public static final int COPY = 0x00000002;

        public static final int CUT = 0x00000003;

        public static final int ZIP = 0x00000004;

        public static final int UNZIP = 0x00000005;
    }

    public static class SelectType {
        public static final int MENU_FILE = 0x00000010;

        public static final int MENU_PHOTO = 0x00000011;

        public static final int MENU_MUSIC = 0x00000012;

        public static final int MENU_VIDEO = 0x00000013;

        public static final int MENU_DOCUMENT = 0x00000014;

        public static final int MENU_DOWNLOAD = 0x00000015;

        public static final int MENU_APK = 0x00000016;

        public static final int MENU_ZIP = 0x00000017;

        public static final int MENU_APPS = 0x00000018;
    }

    public static class FileType {
        public static final int FILE = 0x00000020;

        public static final int IMAGE = 0x00000021;

        public static final int AUDIO = 0x00000022;

        public static final int VIDEO = 0x00000023;

        public static final int DOCUMENT = 0x00000024;

        public static final int SINGLE_IMAGE = 0x00000025;

        public static final int SINGLE_AUDIO = 0x00000026;

        public static final int SINGLE_VIDEO = 0x00000027;

        public static final int SINGLE_DOCUMENT = 0x00000028;

        public static final int APK = 0x00000029;

        public static final int COMPRESS = 0x00000030;

        public static final int INSTALLED = 0x00000031;
    }

    public static class SortType {
        public static final int TYPE = 0x00000040;

        public static final int TIME = 0x00000041;

        public static final int ALPHABET = 0x00000042;

        public static final int SIZE = 0x00000043;
    }

    public static class OrderType {
        public static final int DESC = 0x00000050;

        public static final int ASC = 0x00000051;
    }

}
