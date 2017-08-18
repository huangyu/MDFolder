package com.huangyu.mdfolder.utils;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * Created by huangyu on 2017/8/18.
 */

public class RootUtils {

    private RootUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }


    /**
     * 判断当前手机是否有ROOT权限
     *
     * @return 有/无
     */
    public static boolean isRoot() {
        boolean bool = false;

        try {
            if ((!new File("/system/bin/su").exists()) && (!new File("/system/xbin/su").exists())) {
                bool = false;
            } else {
                bool = true;
            }
        } catch (Exception e) {

        }
        return bool;
    }

    /**
     * 请求root权限
     *
     * @param pkgCodePath 包目录
     * @return 成功/失败
     */
    public static boolean requestRootPermission(String pkgCodePath) {
        Process process = null;
        DataOutputStream os = null;
        try {
            String cmd = "chmod 777 " + pkgCodePath;
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(cmd + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
            }
        }
        return true;
    }

    /**
     * 执行Root命令
     *
     * @param cmd 命令
     * @return 执行结果
     */
    public static String executeRootCommand(String cmd) {
        String result = "";
        DataOutputStream dos = null;
        DataInputStream dis = null;

        try {
            Process p = Runtime.getRuntime().exec("su");
            dos = new DataOutputStream(p.getOutputStream());
            dis = new DataInputStream(p.getInputStream());

            dos.writeBytes(cmd + "\n");
            dos.flush();
            dos.writeBytes("exit\n");
            dos.flush();
            String line;
            while ((line = dis.readLine()) != null) {
                result += line + "\r\n";
            }
            p.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (dis != null) {
                try {
                    dis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    /**
     * 挂载找到分区位置
     *
     * @return 分区位置
     */
    private static String mountToFindPartition() {
        String re = "";
        String block = "";
        re += executeRootCommand("mount");
        if (re.length() > 10) {
            block = re.substring(0, re.indexOf(" /system"));
            block = block.substring(block.lastIndexOf("\n") + 1);
        }
        Log.i("mount", re);
        return block;
    }

    /**
     * 挂载
     *
     * @return 是否挂载成功
     */
    public static boolean mount() {
        String block = mountToFindPartition();
        if (block != null && block.length() > 0) {
            String re = executeRootCommand("mount -o remount,rw " + block + " /system");
            Log.i("mount", re);
            return true;
        }
        return false;
    }

}