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
    public static final String UNDERLINE_CODE = "_";

    public static final String SEMICOLON_CODE = ";";

    /**
     * 参数分割符比如name = :name
     */
    public static final String PARAMETER_CODE = ":";

    /**
     * 空格
     */
    public static final String BLANK_SPACE = " ";

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
     * 将有占位符的whereClause转换成正常语句
     * @param whereClause 查询语句比如 name = :name and age = :age
     * @param parameters {name: zhangsan, age: 10}
     * @return name = 'zhangsan' and age = '10'
     */
    public static String parseWhereClause(String whereClause, Map<String, Object> parameters) {
        if (parameters != null && parameters.size() != 0) {
            for (String parameter : parameters.keySet()) {
                whereClause = whereClause.replaceAll( PARAMETER_CODE + parameter, "'" + parameters.get(parameter) + "'");
            }
        }
        return whereClause;
    }

    /**
     * 获取whereClause里面所有的占位符
     * @param whereClause where语句比如name = :name AND age = :age
     * @return {name, age}
     */
    public static List<String> getWhereClauseParameter(String whereClause) {
        List<String> parameters = Lists.newArrayList();
        if (!isNullOrEmpty(whereClause)) {
            List<Integer> indexes = getAllIndex(whereClause, PARAMETER_CODE);
            if (CollectionUtils.isNotEmpty(indexes)) {
                for (int index : indexes) {
                    int blankSpaceIndex = whereClause.indexOf(BLANK_SPACE, index);
                    if (blankSpaceIndex == -1) {
                        blankSpaceIndex = whereClause.length();
                    }
                    String parameter = whereClause.substring(index + 1, blankSpaceIndex);
                    parameters.add(parameter);
                }
            }
        }
        return parameters;
    }

    /**
     * 获取str里面key字符串所有的下标
     * @param str 源字符串
     * @param key 关键字
     * @return
     */
    public static List<Integer> getAllIndex(String str, String key) {
        List<Integer> indexes = Lists.newArrayList();

        int index = str.indexOf(key);//*第一个出现的索引位置
        while (index != -1) {
            indexes.add(index);
            index = str.indexOf(key, index + 1);//*从这个索引往后开始第一个出现的位置
        }
        return indexes;
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
