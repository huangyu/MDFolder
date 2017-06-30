package com.huangyu.mdfolder.utils;

import com.huangyu.mdfolder.bean.FileItem;
import com.hzy.lib7z.Un7Zip;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.UnzipParameters;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import net.lingala.zip4j.util.Zip4jUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.innosystec.unrar.Archive;
import de.innosystec.unrar.exception.RarException;

/**
 * Created by huangyu on 2017/6/28.
 */

public class ZipUtils {

    private ZipUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 压缩zip文件
     */
    public static boolean zipFile(ArrayList<File> fileList, String toPath) {
        try {
            ZipFile zipFile = new ZipFile(toPath);
            ZipParameters parameters = new ZipParameters();
            parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
            for (File file : fileList) {
                if (file.isDirectory()) {
                    zipFile.addFolder(file, parameters);
                } else {
                    zipFile.addFile(file, parameters);
                }
            }
        } catch (ZipException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 解压zip文件
     *
     * @param zipFilePath 压缩文件路径
     * @param toPath      解压路径
     */
    @SuppressWarnings("unchecked")
    public static boolean unZipFile(String zipFilePath, String toPath) {
        try {
            ZipFile zipFile = new ZipFile(zipFilePath);
            UnzipParameters param = new UnzipParameters();
            zipFile.setFileNameCharset("ISO8859-1");
            List<FileHeader> list = zipFile.getFileHeaders();
            for (FileHeader fileHeader : list) {
                byte[] b = fileHeader.getFileName().getBytes("ISO8859-1");
                String fileName;
                fileName = new String(b, "UTF-8");
                if (fileName.getBytes("UTF-8").length != b.length) {
                    fileName = new String(b, "GBK");
                }
                zipFile.extractFile(fileHeader, toPath, param, fileName);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 解压zip文件
     *
     * @param zipFilePath 压缩文件路径
     * @param toPath      解压路径
     */
    @SuppressWarnings("unchecked")
    public static boolean unZipFile(String zipFilePath, String toPath, String password) {
        try {
            ZipFile zipFile = new ZipFile(zipFilePath);
            UnzipParameters param = new UnzipParameters();
            zipFile.setFileNameCharset("ISO8859-1");
            List<FileHeader> list = zipFile.getFileHeaders();
            for (FileHeader fileHeader : list) {
                byte[] b = fileHeader.getFileName().getBytes("ISO8859-1");
                String fileName;
                fileName = new String(b, "UTF-8");
                if (fileName.getBytes("UTF-8").length != b.length) {
                    fileName = new String(b, "GBK");
                }
                if (zipFile.isEncrypted()) {
                    zipFile.setPassword(password);
                }
                zipFile.extractFile(fileHeader, toPath, param, fileName);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 解压rar文件
     *
     * @param zipFilePath 压缩文件路径
     * @param toPath      解压路径
     */
    public static boolean unRarFile(String zipFilePath, String toPath) {
        unRar(new File(zipFilePath), toPath);
        return true;
    }

    private static void unRar(File rarFilePathFile, String toPath) {
        FileOutputStream fileOut;
        File file;
        Archive rarFile = null;
        try {
            rarFile = new Archive(rarFilePathFile);
        } catch (RarException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        de.innosystec.unrar.rarfile.FileHeader fh = rarFile.nextFileHeader();
        while (fh != null) {
            String filePath;
            if (fh.isUnicode()) {
                filePath = fh.getFileNameW().trim();
            } else {
                filePath = fh.getFileNameString().trim();
            }
            filePath = filePath.replaceAll("\\\\", File.separator);
            file = new File(toPath, filePath);
            if (fh.isDirectory()) {
                file.mkdirs();
            } else {
                File parent = file.getParentFile();
                if (parent != null && !parent.exists()) {
                    parent.mkdirs();
                }
                try {
                    fileOut = new FileOutputStream(file);
                    rarFile.extractFile(fh, fileOut);
                    fileOut.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (RarException e) {
                    e.printStackTrace();
                }
            }
            fh = rarFile.nextFileHeader();
        }
        try {
            rarFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 解压7z文件
     *
     * @param zipFilePath 压缩文件路径
     * @param toPath      解压路径
     */
    public static boolean un7zipFile(String zipFilePath, String toPath) {
        return Un7Zip.extract7z(zipFilePath, toPath);
    }

    /**
     * 判断zip文件是否加密
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
    public static ArrayList<FileItem> listZipFiles(String zipFilePath) {
        ArrayList<FileItem> fileItemList = new ArrayList<>();
        try {
            ZipFile zipFile = new ZipFile(zipFilePath);
            zipFile.setFileNameCharset("ISO8859-1");
            List<FileHeader> fileHeaderList = zipFile.getFileHeaders();
            FileItem fileItem;
            for (FileHeader fileHeader : fileHeaderList) {
                byte[] b = fileHeader.getFileName().getBytes("ISO8859-1");
                String fileName;
                fileName = new String(b, "UTF-8");
                if (fileName.getBytes("UTF-8").length != b.length) {
                    fileName = new String(b, "GBK");
                }
                fileItem = new FileItem();
                fileItem.setName(fileName);
                fileItem.setDate(String.valueOf(Zip4jUtil.dosToJavaTme(fileHeader.getLastModFileTime())));
                fileItem.setSize(String.valueOf(fileHeader.getUncompressedSize()));
                fileItem.setPath(zipFilePath);
                fileItemList.add(fileItem);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileItemList;
    }

}
