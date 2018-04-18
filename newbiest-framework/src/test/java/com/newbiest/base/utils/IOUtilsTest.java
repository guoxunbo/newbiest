package com.newbiest.base.utils;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.List;

/**
 * Created by guoxunbo on 2018/1/23.
 */
public class IOUtilsTest {

    public File file = new File("/Users/apple/Documents/newbiest/newbiest-framework/src/test/fileTest/aaa/aaa.txt");
    public File targetFile = new File("/Users/apple/Documents/newbiest/newbiest-framework/src/test/fileTest/aaa/bbb/bbb.txt");
    public File targetFile2 = new File("/Users/apple/Documents/newbiest/newbiest-framework/src/test/fileTest/aaa/bbb/ccc.txt");
    public File parentDir = new File("/Users/apple/Documents/newbiest/newbiest-framework/src/test/fileTest");
    public String line = "This is a test content!";

    @Test
    public void createParentDirs() throws Exception {
        IOUtils.createParentDirs(file);
        assert parentDir.exists();
    }

    @Test
    public void writeFileAndReadLines() throws Exception {
        if (!file.exists()) {
            IOUtils.createParentDirs(file);
            file.createNewFile();
        }
        IOUtils.writeFile(line, file);
        List<String> lines = IOUtils.readLines(file);
        Assert.assertEquals(line, lines.get(0));
    }

    @Test
    public void copy() throws Exception {
        if (!file.exists()) {
            IOUtils.createParentDirs(file);
            file.createNewFile();
            IOUtils.writeFile(line, file);
        }
        IOUtils.createParentDirs(targetFile);
        IOUtils.copy(file, targetFile);
        List<String> targetLines = IOUtils.readLines(targetFile);
        Assert.assertEquals(line, targetLines.get(0));
    }

    @Test
    public void move() throws Exception {
        if (!file.exists()) {
            IOUtils.createParentDirs(file);
            file.createNewFile();
            IOUtils.writeFile(line, file);
        }
        IOUtils.createParentDirs(targetFile2);
        IOUtils.move(file, targetFile2);

        assert file.exists() == false;
        List<String> targetLines = IOUtils.readLines(targetFile2);
        Assert.assertEquals(line, targetLines.get(0));
    }

    @Test
    public void getNameWithoutExtension() throws Exception {
        Assert.assertEquals("aaa", IOUtils.getNameWithoutExtension(file));
    }

    @Test
    public void getExtension() throws Exception {
        Assert.assertEquals("txt", IOUtils.getExtension(file));
    }

    @Test
    public void getDeepFile() throws Exception {
        writeFileAndReadLines();
        copy();
        move();
        // move之后就没了，故又新建一个
        writeFileAndReadLines();
        List<File> list = IOUtils.getDeepFile(parentDir);
        Assert.assertEquals(6, list.size());
        Assert.assertEquals("/Users/apple/Documents/newbiest/newbiest-framework/src/test/fileTest",
                list.get(0).getAbsolutePath());
        Assert.assertEquals("/Users/apple/Documents/newbiest/newbiest-framework/src/test/fileTest/aaa",
                list.get(1).getAbsolutePath());
        Assert.assertEquals("/Users/apple/Documents/newbiest/newbiest-framework/src/test/fileTest/aaa/bbb",
                list.get(2).getAbsolutePath());
        Assert.assertEquals("/Users/apple/Documents/newbiest/newbiest-framework/src/test/fileTest/aaa/aaa.txt",
                list.get(3).getAbsolutePath());
        Assert.assertEquals("/Users/apple/Documents/newbiest/newbiest-framework/src/test/fileTest/aaa/bbb/ccc.txt",
                list.get(4).getAbsolutePath());
        Assert.assertEquals("/Users/apple/Documents/newbiest/newbiest-framework/src/test/fileTest/aaa/bbb/bbb.txt",
                list.get(5).getAbsolutePath());
    }

}