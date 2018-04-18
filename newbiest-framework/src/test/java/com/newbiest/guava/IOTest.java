package com.newbiest.guava;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.List;

/**
 * I/O基本操作类
 * Created by guoxunbo on 2018/1/22.
 */
public class IOTest {

    /**
     * 测试文件相关操作
     * 所有流的操作都不需要人工去处理
     */
    @Test
    public void fileTest() throws Exception{
        File file = new File("/Users/apple/Documents/newbiest/newbiest-framework/src/test/guavaTest/aaa/aaa.txt");
        assert file.exists() == false;
        // 以前当父级文件不存在的时候会报错，虽然File提供了一个parentDir方法。但是可能父级的父级的文件夹就开始不存在了。需要自己递归处理。故
        // Guava提供了创建当前文件上面的所有父级文件夹(如果不存在的话)。
        Files.createParentDirs(file);
        file.createNewFile();
        assert file.exists() == true;

        // 写文件
        String line = "This is a test content!";
        Files.write(line.getBytes(), file);
        // 读取文件 按行读取
        List<String> lines = Files.readLines(file, Charsets.UTF_8);
        Assert.assertEquals(line, lines.get(0));

        // 文件拷贝
        File targetFile = new File("/Users/apple/Documents/newbiest/newbiest-framework/src/test/guavaTest/aaa/bbb/bbb.txt");
        Files.createParentDirs(targetFile);
        Files.copy(file, targetFile);
        List<String> targetLines = Files.readLines(targetFile, Charsets.UTF_8);
        Assert.assertEquals(line, targetLines.get(0));

        // 取得后缀和取得全名但是不包括后缀
        String extension = Files.getFileExtension(file.getName());
        Assert.assertEquals("txt", extension);
        String fileName = Files.getNameWithoutExtension(file.getName());
        Assert.assertEquals("aaa", fileName);

        // 递归获取某个文件夹下面的文件，提供最外层的目录，返回这个目录下所有子级文件.
        // /a/b/c/d/e/f.txt 根据/a目录去获取，会返回/a,/a/b, /a/c, /a/d, /a/e, /a/e/f.txt
        File parentDirectory = new File("/Users/apple/Documents/newbiest/newbiest-framework/src/test/guavaTest");
        FluentIterable<File> iterable = Files.fileTreeTraverser().breadthFirstTraversal(parentDirectory);
        List<File> list = Lists.newArrayList(iterable);
        Assert.assertEquals(5, list.size());
        Assert.assertEquals("/Users/apple/Documents/newbiest/newbiest-framework/src/test/guavaTest",
                list.get(0).getAbsolutePath());
        Assert.assertEquals("/Users/apple/Documents/newbiest/newbiest-framework/src/test/guavaTest/aaa",
                list.get(1).getAbsolutePath());
        Assert.assertEquals("/Users/apple/Documents/newbiest/newbiest-framework/src/test/guavaTest/aaa/bbb",
                list.get(2).getAbsolutePath());
        Assert.assertEquals("/Users/apple/Documents/newbiest/newbiest-framework/src/test/guavaTest/aaa/aaa.txt",
                list.get(3).getAbsolutePath());
        Assert.assertEquals("/Users/apple/Documents/newbiest/newbiest-framework/src/test/guavaTest/aaa/bbb/bbb.txt",
                list.get(4).getAbsolutePath());
        // 移动文件
        File targetFile2 = new File("/Users/apple/Documents/newbiest/newbiest-framework/src/test/guavaTest/aaa/bbb/ccc.txt");
        Files.createParentDirs(targetFile2);
        Files.move(file, targetFile2);
        targetLines = Files.readLines(targetFile2, Charsets.UTF_8);
        Assert.assertEquals(line, targetLines.get(0));
    }
}
