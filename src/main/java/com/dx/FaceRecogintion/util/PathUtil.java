package com.dx.FaceRecogintion.util;/**
 * Created by Administrator on 2019/5/3.
 *
 * @author Administrator
 */

import java.io.File;

/**
 *
 *@ClassName PathUtil
 *@Description 路径工具
 *@Autor Administrator
 *@Date 2019/5/3 21:49
 **/
public class PathUtil {
    /**
     * 获取当前系统分隔符
     */
    private static String seperator = System.getProperty("file.separator");


    /**
     * 创建目标路径所涉及到的目录
     * @param targetAddr
     */
    public static ExcutionResultUtil makeDirPath(String targetAddr) {
        File file = new File(targetAddr);
        if (!file.exists()){
            file.mkdirs();
            return new ExcutionResultUtil(true,"新用户注册");
        }
        return new ExcutionResultUtil(false,"用户名已存在");
    }

    /**
     * 删除指定路径
     * @param targetAddr
     * @return
     */
    public static ExcutionResultUtil deleteDirPath(String targetAddr){
        File files = new File(targetAddr);
        if (files.exists()){
            if (files.isDirectory()){
                for (File file:files.listFiles()
                        ) {
                    deleteDirPath(file.getAbsolutePath());
                }
                files.delete();
            }else {
                files.delete();
            }
        }else {
            return new ExcutionResultUtil(false,"删除指定文件不存在");
        }
        return new ExcutionResultUtil(true,"删除路径成功");
    }
}
