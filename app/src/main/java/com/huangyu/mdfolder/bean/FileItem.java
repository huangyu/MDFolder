package com.huangyu.mdfolder.bean;

/**
 * Created by huangyu on 2017-6-19.
 */
public class FileItem {

    private String path;
    private String name;
    private String size;
    private String date;
    private boolean isDirectory;
    private String parent;

    public FileItem() {

    }

    public String getPath() {
        return path;
    }

    public void setPath(String mFilePath) {
        this.path = mFilePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String mFileName) {
        this.name = mFileName;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String mSize) {
        this.size = mSize;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String mDate) {
        this.date = mDate;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public void setIsDirectory(boolean isDirectory) {
        this.isDirectory = isDirectory;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

}