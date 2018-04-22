package com.newbiest.base.utils;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.exception.NewbiestException;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;

/**
 * I/O操作相关工具类
 * Created by guoxunbo on 2018/1/23.
 */
public class IOUtils {

    /**
     * 创建此文件的所有父级文件夹 如/a/b/c.txt 若/a不存在则创建/a, /a/b.若/a/b不存在就创建/a/b.
     * @param file
     */
    public static void createParentDirs(File file) throws ClientException {
        try {
            Files.createParentDirs(file);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e);
        }
    }

    /**
     * 给文件写值
     * @param content 内容
     * @param targetFile 目标文件
     */
    public static void writeFile(String content, File targetFile) throws ClientException {
        try {
            Files.write(content.getBytes(), targetFile);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e);
        }
    }

    /**
     * 文件按行读取
     * @param file 文件
     * @param charset 字符集
     * @return
     */
    public static List<String> readLines(File file, Charset charset) throws ClientException {
        try {
            exists(file);
            return Files.readLines(file, charset);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e);
        }
    }

    /**
     * 文件按行读取
     * @param file 文件
     * @return
     */
    public static List<String> readLines(File file) throws ClientException {
        try {
            return readLines(file, StringUtils.getCharset(StringUtils.CHARSET_UTF_8));
        } catch (Exception e) {
            throw ExceptionManager.handleException(e);
        }
    }

    /**
     * 文件内容复制
     * @param source 源文件
     * @param target 目标文件
     */
    public static void copy(File source, File target) throws ClientException {
        try {
            exists(source);
            Files.copy(source, target);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e);
        }
    }

    /**
     * 文件移动
     * @param source 源文件
     * @param target 目标文件
     */
    public static void move(File source, File target) throws ClientException {
        try {
            Files.move(source, target);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e);
        }
    }

    /**
     * 取得没有后缀名的文件
     * @param source 源文件
     */
    public static String getNameWithoutExtension(File source) throws ClientException {
        try {
            return Files.getNameWithoutExtension(source.getName());
        } catch (Exception e) {
            throw ExceptionManager.handleException(e);
        }
    }

    /**
     * 文件移动
     * @param source 源文件
     */
    public static String getExtension(File source) throws ClientException {
        try {
            return Files.getFileExtension(source.getName());
        } catch (Exception e) {
            throw ExceptionManager.handleException(e);
        }
    }

    /**
     * 递归获取某个文件夹下面的文件，提供最外层的目录，返回这个目录下所有子级文件.
     * /a/b/c/d/e/f.txt 根据/a目录去获取，会返回/a, /a/b, /a/c, /a/d, /a/e, /a/e/f.txt
     * @param source 源文件
     */
    public static List<File> getDeepFile(File source) throws ClientException {
        try {
            exists(source);
            FluentIterable<File> iterable = Files.fileTreeTraverser().breadthFirstTraversal(source);
            return Lists.newArrayList(iterable);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e);
        }
    }

    public static void exists(File file) throws ClientException{
        if (!file.exists()) {
            throw new ClientException(NewbiestException.COMMON_FILE_IS_NOT_EXIST);
        }
    }
}
