package com.huangyu.library.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;

import com.huangyu.library.app.BaseConstants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 日志保存文件工具类
 * Created by huangyu on 2017-4-10.
 */
public class LogToFileUtils {

    // 用来存储设备信息和异常信息
    private static Map<String, String> mDeviceInfos = new HashMap<>();

    // 用于格式化日期,作为日志文件名的一部分
    private static DateFormat mDateformat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault());

    private LogToFileUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 保存错误信息到文件中
     *
     * @return 返回文件名称, 便于将文件传送到服务器
     */
    public static String insertSysLogToFile(Exception e) {
        StringWriter sw = null;
        PrintWriter pw = null;
        FileOutputStream fos = null;

        String fileName = null;
        try {
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault());
            long timestamp = System.currentTimeMillis();
            String time = formatter.format(new Date());
            fileName = "CaughtException-" + time + "-" + timestamp + ".txt";
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                String path = BaseConstants.LOG_PATH;
                File dir = new File(path);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                sw = new StringWriter();
                pw = new PrintWriter(sw);

                // 将出错的栈信息输出到printWriter中
                e.printStackTrace(pw);
                pw.flush();
                sw.flush();

                fos = new FileOutputStream(path + fileName);
                fos.write(sw.toString().getBytes());
            }
        } catch (Exception ex) {
            insertSysLogToFile(e);
        } finally {
            try {
                if (sw != null) {
                    sw.close();
                }
                if (pw != null) {
                    pw.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception e1) {
                insertSysLogToFile(e1);
            }
            return fileName;
        }
    }

    /**
     * 保存错误信息到文件中
     *
     * @param ex
     * @return 返回文件名称, 便于将文件传送到服务器
     */
    public static String saveCrashInfoToFile(Throwable ex) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : mDeviceInfos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "\n");
        }

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append(result);
        try {
            long timestamp = System.currentTimeMillis();
            String time = mDateformat.format(new Date());
            String fileName = "crash-" + time + "-" + timestamp + ".txt";
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                String path = BaseConstants.LOG_PATH;
                File dir = new File(path);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                FileOutputStream fos = new FileOutputStream(path + fileName);
                fos.write(sb.toString().getBytes());
                fos.close();
            }
            return fileName;
        } catch (Exception e) {
            saveCrashInfoFile(getExceptionInfo(e));
        }
        return null;
    }

    /**
     * 收集设备参数信息
     *
     * @param ctx
     */
    public static void collectDeviceInfo(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                mDeviceInfos.put("versionName", versionName);
                mDeviceInfos.put("versionCode", versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                mDeviceInfos.put(field.getName(), field.get(null).toString());
            } catch (Exception e) {
                saveCrashInfoFile(getExceptionInfo(e));
            }
        }
    }

    /**
     * 保存错误信息到文件中
     *
     * @return 返回文件名称, 便于将文件传送到服务器
     */
    public static String saveCrashInfoFile(String content) {
        try {
            String fileName = null;
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                File configFile = new File(BaseConstants.LOG_PATH);
                if (!configFile.exists()) {
                    configFile.mkdirs();
                }

                if (configFile.exists()) {
                    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
                    long timestamp = System.currentTimeMillis();
                    String time = formatter.format(new Date());
                    fileName = "CaughtException-" + time + "-" + timestamp + ".txt";
                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        String path = BaseConstants.LOG_PATH;
                        File dir = new File(path);
                        if (!dir.exists()) {
                            dir.mkdirs();
                        }
                        FileOutputStream fos = new FileOutputStream(path + fileName);
                        fos.write(content.getBytes());
                        fos.close();
                    }
                }
            }
            return fileName;
        } catch (Exception e) {
            saveCrashInfoFile(getExceptionInfo(e));
        }
        return null;
    }

    /**
     * 获取Exception中的信息
     *
     * @param e
     * @return
     */
    public static String getExceptionInfo(Exception e) {
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        e.printStackTrace(printWriter);
        Throwable cause = e.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        return writer.toString();
    }

}
