package com.newbiest.base.utils;

import com.google.common.base.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.newbiest.base.exception.ClientParameterException;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by guoxunbo on 2017/9/29.
 */
public class StringUtils {

    public static final String EMPTY = "";
    public static final String YES = "Y";
    public static final String NO = "N";

    public static final String SPLIT_CODE = "-";

    public static final String CHARSET_GBK = "GBK";
    public static final String CHARSET_UTF_8 = "UTF-8";
    public static final String CHARSET_ASCII = "ASCII";
    public static final String CHARSET_UTF_16 = "UTF-16";
    public static Map<String, Charset> charSetMap = Maps.newConcurrentMap();

    static {
        charSetMap.put(CHARSET_GBK, Charset.forName(CHARSET_GBK));
        charSetMap.put(CHARSET_UTF_8, Charsets.UTF_8);
        charSetMap.put(CHARSET_ASCII, Charsets.US_ASCII);
        charSetMap.put(CHARSET_UTF_16, Charsets.UTF_16);
    }

    /**
     * 补位操作，在不足长度的时候前面补字符
     * @param source 字符串
     * @param length 长度
     * @param padChar 补位的字符
     * @return
     */
    public static String padStart(String source, int length, char padChar ) {
        return Strings.padStart(source, length, padChar);
    }

    /**
     * 补位操作，在不足长度的时候后面补字符
     * @param source 字符串
     * @param length 长度
     * @param padChar 补位的字符
     * @return
     */
    public static String padEnd(String source, int length, char padChar ) {
        return Strings.padEnd(source, length, padChar);
    }

    public static boolean isNullOrEmpty(String str) {
        return Strings.isNullOrEmpty(str);
    }

    /**
     * 将字符串按照一定的字符串匹配器进行分割 去除分割后的空字符如a,b,c,d,,返回a b c d
     * @param source 字符串
     * @param charMatcher 字符串匹配器
     * @return 新产生的集合
     */
    public static List<String> split(String source, CharMatcher charMatcher) {
        Iterable<String> iterable = Splitter.on(charMatcher).trimResults().omitEmptyStrings().split(source);
        return Lists.newArrayList(iterable);
    }

    /**
     * 将字符串按照一定的正则表达式进行分割 去除分割后的空字符如a,b,c,d,,返回a b c d
     * @param source 字符串
     * @param pattern 分割的正则表达式
     * @return 新产生的集合
     */
    public static List<String> split(String source, Pattern pattern) {
        Iterable<String> iterable = Splitter.on(pattern).trimResults().omitEmptyStrings().split(source);
        return Lists.newArrayList(iterable);
    }

    /**
     * 将字符串按照一定的分隔符进行分割 去除分割后的空字符如a,b,c,d,,返回a b c d
     * @param source 字符串
     * @param splitCode 分隔符
     * @return 新产生的集合
     */
    public static List<String> split(String source, String splitCode) {
        Iterable<String> iterable = Splitter.on(splitCode).trimResults().omitEmptyStrings().split(source);
        return Lists.newArrayList(iterable);
    }

    /**
     * 将objects的toString()方法按照分隔符进行拼接
     * @param objects 待拼接的对象
     * @param splitCode 分隔符
     * @return
     */
    public static String join(List<String> objects, String splitCode) {
        if (CollectionUtils.isNotEmpty(objects)) {
            return Joiner.on(splitCode).skipNulls().join(objects);
        }
        return "";
    }

    /**
     * 对String使用%s占位符format
     * @param template 字符串如%s a %s b
     * @param objects 参数
     * @return
     */
    public static String formatFromList(String template, List<Object> objects) {
        if (CollectionUtils.isNotEmpty(objects)) {
            return format(template, objects.toArray());
        }
        return template;
    }

    /**
     * 对String使用%s占位符format
     * @param template 字符串如%s a %s b
     * @param args 参数
     * @return
     */
    public static String format(String template, Object... args) {
        template = String.valueOf(template);
        StringBuilder builder = new StringBuilder(template.length() + 16 * args.length);
        int templateStart = 0;

        int i;
        int placeholderStart;
        for(i = 0; i < args.length; templateStart = placeholderStart + 2) {
            placeholderStart = template.indexOf(ClientParameterException.PARAMETER_PLACEHOLDER, templateStart);
            if(placeholderStart == -1) {
                break;
            }

            builder.append(template, templateStart, placeholderStart);
            builder.append(args[i++]);
        }

        builder.append(template, templateStart, template.length());
        if(i < args.length) {
            builder.append(" [");
            builder.append(args[i++]);

            while(i < args.length) {
                builder.append(", ");
                builder.append(args[i++]);
            }

            builder.append(']');
        }

        return builder.toString();
    }

    /**
     * 获取Charset
     * @param charsetName 字符集名称
     * @return
     */
    public static Charset getCharset(String charsetName) {
        if (!charSetMap.containsKey(charsetName)) {
            Charset charset = Charset.forName(charsetName);
            charSetMap.put(charsetName, charset);
        }
        return charSetMap.get(charsetName);
    }

}
