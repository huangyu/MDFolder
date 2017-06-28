package com.huangyu.mdfolder.utils;

import com.huangyu.mdfolder.bean.FileItem;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import net.lingala.zip4j.util.Zip4jUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by huangyu on 2017/6/28.
 */

public class ZipUtils {

    private ZipUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 压缩文件
     */
    public static boolean zipFile(ArrayList<File> fileList, String toPath) {
        try {
            ZipFile zipFile = new ZipFile(toPath);
            ZipParameters parameters = new ZipParameters();
            parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
            zipFile.addFiles(fileList, parameters);
        } catch (ZipException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 解压文件
     *
     * @param zipFilePath 压缩文件路径
     * @param toPath      解压路径
     */
    public static boolean unzipFile(String zipFilePath, String toPath) {
        try {
            ZipFile zipFile = new ZipFile(zipFilePath);
            zipFile.extractAll(toPath);
        } catch (ZipException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 解压文件
     *
     * @param zipFilePath 压缩文件路径
     * @param toPath      解压路径
     */
    public static boolean unzipFile(String zipFilePath, String toPath, String password) {
        try {
            ZipFile zipFile = new ZipFile(zipFilePath);
            if (zipFile.isEncrypted()) {
                zipFile.setPassword(password);
            }
            zipFile.extractAll(toPath);
        } catch (ZipException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    /**
     * zip文件是否加密
     *
     * @param zipFilePath 压缩文件路径
     * @return
     */
    public static boolean isEncrypted(String zipFilePath) {
        try {
            ZipFile zipFile = new ZipFile(zipFilePath);
            return zipFile.isEncrypted();
        } catch (ZipException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 显示zip文件内容
     *
     * @param zipFilePath 压缩文件路径
     * @return
     */
    @SuppressWarnings("unchecked")
    public static ArrayList<FileItem> listFiles(String zipFilePath) {
        ArrayList<FileItem> fileItemList = new ArrayList<>();
        try {
            ZipFile zipFile = new ZipFile(zipFilePath);
            List<FileHeader> fileHeaderList = zipFile.getFileHeaders();
            FileItem fileItem;
            for (FileHeader fileHeader : fileHeaderList) {
                fileItem = new FileItem();
                fileItem.setName(fileHeader.getFileName());
                fileItem.setDate(String.valueOf(Zip4jUtil.dosToJavaTme(fileHeader.getLastModFileTime())));
                fileItem.setSize(String.valueOf(fileHeader.getUncompressedSize()));
                fileItemList.add(fileItem);
            }
        } catch (ZipException e) {
            e.printStackTrace();
        }
        return fileItemList;
    }

}
