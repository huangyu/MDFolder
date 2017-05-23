package com.huangyu.mdfolder.bean;

import java.util.Date;

/**
 * Created by huangyu on 2017-5-23.
 */
public class FileBean {

    private String name;    // 文件名
    private String path;    // 路径
    private Date lastModified;    // 最后时间
    private String letter;  // 首字母
    private long size;      // 文件大小
    private boolean isFolder; // 是否是文件夹

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public boolean isFolder() {
        return isFolder;
    }

    public void setFolder(boolean folder) {
        isFolder = folder;
    }

}
