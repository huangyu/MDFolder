package com.huangyu.mdfolder.app;

import com.huangyu.library.app.BaseConstants;

/**
 * Created by huangyu on 2017-5-26.
 */
public class Constants extends BaseConstants {

    public static final int PERMISSION_ACCESS_FILES = 0x01;

    public static class EditType {

        public static final int NONE = 0x00;

        public static final int SELECT = 0x01;

        public static final int COPY = 0x02;

        public static final int CUT = 0x03;
    }

    public static class SelectType {
        public static final int MENU_FILE = 0x10;

        public static final int MENU_PHOTO = 0x11;

        public static final int MENU_MUSIC = 0x12;

        public static final int MENU_VIDEO = 0x13;

        public static final int MENU_DOCUMENT = 0x14;

        public static final int MENU_DOWNLOAD = 0x15;
    }

    public static class FileType {
        public static final int FILE = 0x20;

        public static final int IMAGE = 0x21;

        public static final int AUDIO = 0x22;

        public static final int VIDEO = 0x23;

        public static final int DOCUMENT = 0x24;
    }

    public static class SortType {
        public static final int TYPE = 0x30;

        public static final int TIME = 0x31;

        public static final int ALPHABET = 0x32;

        public static final int SIZE = 0x33;
    }

    public static class OrderType {
        public static final int DESC = 0x40;

        public static final int ASC = 0x41;
    }

}
