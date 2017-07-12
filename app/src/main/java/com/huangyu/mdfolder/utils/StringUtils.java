package com.huangyu.mdfolder.utils;

/**
 * Created by huangyu on 2017/7/12.
 */

public class StringUtils {

    private StringUtils() {
    }

    /**
     * 忽略大小写匹配
     *
     * @param str
     * @param searchStr
     * @return
     */
    public static boolean containsIgnoreCase(String str, String searchStr) {
        if (str == null || searchStr == null) {
            return false;
        }

        final int length = searchStr.length();
        if (length == 0) {
            return true;
        }

        for (int i = str.length() - length; i >= 0; i--) {
            if (str.regionMatches(true, i, searchStr, 0, length)) {
                return true;
            }
        }
        return false;
    }

}
