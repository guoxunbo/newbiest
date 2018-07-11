package com.newbiest.base.utils;

/**
 * 对Sql的一些常见操作进行封装
 * Created by guoxunbo on 2018/7/4.
 */
public class SqlUtils {

    /**
     * 给value加上双引号比如'test'
     * @return
     */
    public static String quotationMarks(String value) {
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("'");
        sqlBuffer.append(value);
        sqlBuffer.append("'");
        return sqlBuffer.toString();
    }

    /**
     * 根据value类型自动判断是否需要加引号等处理 比如"aaa"-> "aaa", 111->111
     * @param value
     * @return
     */
    public static String getValueByType(Object value) {
        if (value instanceof Number) {
            return value + "";
        } else if (value instanceof CharSequence) {
            return SqlUtils.quotationMarks(value.toString());
        }
        return SqlUtils.quotationMarks(value.toString());
    }

}
