package com.newbiest.guava;

import com.google.common.base.*;
import org.apache.commons.collections.IteratorUtils;
import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.Charset;
import java.util.List;
import java.util.regex.Pattern;

/**
 * String工具类测试
 * Created by guoxunbo on 2018/1/19.
 */
public class StringTest {

    /**
     * 连接器和分割符测试
     */
    @Test
    public void joinerAndSplitterTest() {
        List<String> list = Lists.newArrayList("a", "b", null, "c", "d");
        // 去除空字符串后按照-拼接
        String joinStrWithOutNull = Joiner.on("-").skipNulls().join(list);
        Assert.assertEquals("a-b-c-d", joinStrWithOutNull);

        // 空字符串用empty代替按照-拼接
        String joinerUseForNull = Joiner.on("-").useForNull("empty").join(list);
        Assert.assertEquals("a-b-empty-c-d", joinerUseForNull);

        String splitterStr = "a,b,c,d,e";
        Iterable<String> iterable = Splitter.on(",").trimResults().omitEmptyStrings().split(splitterStr);
        List splitList = Lists.newArrayList(iterable);

        Assert.assertEquals(5, splitList.size());
        Assert.assertEquals("a", splitList.get(0));
        Assert.assertEquals("d", splitList.get(3));

        // 限制只取前3个 第3个包含了后续的所有字符
        iterable = Splitter.on(",").trimResults().omitEmptyStrings().limit(3).split(splitterStr);
        splitList = Lists.newArrayList(iterable);
        Assert.assertEquals(3, splitList.size());
        Assert.assertEquals("a", splitList.get(0));
        Assert.assertEquals("c,d,e", splitList.get(2));

        // 根据正则表达式拆分
        Pattern pattern = Pattern.compile("/n");
        Splitter.on(pattern);

        // 根据CharMatcher拆分
        Splitter.on(CharMatcher.breakingWhitespace());
    }

    /**
     * 字符串匹配器测试
     */
    @Test
    public void charMatcherTest() {
        // 几种基本CharMatcher介绍
        // digit表示数字匹配
        CharMatcher.digit();
        // 匹配字母
        CharMatcher.javaLetter();
        // 大小写
        CharMatcher.javaUpperCase();
        CharMatcher.javaLowerCase();
        // 匹配ASCII码
        CharMatcher.ascii();
        // 匹配所有可换行的空白字符
        CharMatcher.breakingWhitespace();
        // 匹配所有空白字符
        CharMatcher.whitespace();

        String waitForMatchString = "aa1111swwwsss";
        // digit表示数字匹配
        CharMatcher digitMacher = CharMatcher.digit();
        // 以下列举了几种charMatcher常用的方法
        // 移除除了数字的所有字符
        String str = digitMacher.retainFrom(waitForMatchString);
        Assert.assertEquals("1111", str);

        // 移除数字字符
        str = digitMacher.removeFrom(waitForMatchString);
        Assert.assertEquals("aaswwwsss", str);

        // 用*号代替数字
        str = digitMacher.replaceFrom(waitForMatchString, "*");
        Assert.assertEquals("aa****swwwsss", str);

        // 可以对一个字符串做多个CharMatcher 创建一个数字和大写字母的匹配器
        CharMatcher digitOrUpperCaseMatcher = CharMatcher.digit().or(CharMatcher.javaUpperCase());
        waitForMatchString = "aa1111swwwsssA";
        // 去掉数字和大写字母
        str = digitOrUpperCaseMatcher.removeFrom(waitForMatchString);
        Assert.assertEquals("aaswwwsss", str);

        CharMatcher lowerCaseMatcher = CharMatcher.javaLowerCase();
        waitForMatchString = "aaswwwsss";
        // 字符串是否全部匹配这个字符匹配器
        boolean matchFlag = lowerCaseMatcher.matchesAllOf(waitForMatchString);
        assert matchFlag == true;
    }


    /**
     * 字符集编码测试
     */
    @Test
    public void charsetsTest() {
        Charset utf8 = Charsets.UTF_8;
        Assert.assertEquals("UTF-8", utf8.displayName());

        Charset utf16 = Charsets.UTF_16;
        Assert.assertEquals("UTF-16", utf16.displayName());

        Charset utf16be = Charsets.UTF_16BE;
        Assert.assertEquals("UTF-16BE", utf16be.displayName());

        Charset iso88591 = Charsets.ISO_8859_1;
        Assert.assertEquals("ISO-8859-1", iso88591.displayName());

        Charset ascii = Charsets.US_ASCII;
        Assert.assertEquals("US-ASCII", ascii.displayName());

        Charset utf16le = Charsets.UTF_16LE;
        Assert.assertEquals("UTF-16LE", utf16le.displayName());
    }

    /**
     * Strings操作
     */
    @Test
    public void stringsTest() {
        // 空或者空字符串返回true
        boolean emptyFlag = Strings.isNullOrEmpty("");
        assert emptyFlag == true;
        // 将空转换成空字符串
        String str = Strings.nullToEmpty(null);
        Assert.assertEquals("", str);
        // 将不能满足位数的前补字符或后补字符
        str = "aaaa";
        String padStartStr = Strings.padStart(str, 10, '0');
        Assert.assertEquals("000000aaaa", padStartStr);
        String padEndStr = Strings.padEnd(str, 10, '0');
        Assert.assertEquals("aaaa000000", padEndStr);

        // 将某个字符串循环复制很多遍，Guava不是用StringBuffer循环的，而是使用的arrayCopy实现的。
        String waitForRepeartStr = "repeat;";
        String repeatedStr = Strings.repeat(waitForRepeartStr, 3);
        Assert.assertEquals("repeat;repeat;repeat;", repeatedStr);
    }


}