package com.newbiest.base.utils;

import com.google.common.collect.Maps;
import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;

/**
 * StringUtils类单元测试
 * Created by guoxunbo on 2018/1/23.
 */
public class StringUtilsTest {

    @Test
    public void format() {
        String str = "%s aa %s bb %s";
        Assert.assertEquals("x aa y bb z", StringUtils.format(str, "x", "y", "z"));

        List<Object> list = Lists.newArrayList("1", "2", "3");
        Assert.assertEquals("1 aa 2 bb 3", StringUtils.formatFromList(str, list));

        Object[] objects = {"q", "w", "e"};
        Assert.assertEquals("q aa w bb e", StringUtils.format(str, objects));

    }

    @Test
    public void padStart() throws Exception {
        String str = "aaaa";
        String padStartStr = StringUtils.padStart(str, 10, '0');
        Assert.assertEquals("000000aaaa", padStartStr);
    }

    @Test
    public void padEnd() throws Exception {
        String str = "aaaa";
        String padEndStr = StringUtils.padEnd(str, 10, '0');
        Assert.assertEquals("aaaa000000", padEndStr);
    }

    @Test
    public void isEmpty() throws Exception {
        assert StringUtils.isNullOrEmpty("");
    }

    @Test
    public void split() throws Exception {
        String splitterStr = "a,b,c,d,e";
        Iterable<String> iterable = StringUtils.split(splitterStr, ",");
        List splitList = Lists.newArrayList(iterable);

    }

    @Test
    public void getWhereClauseParameter() {
        String whereClause = "name = :name and age=:age";
        List<String> parameters = StringUtils.getWhereClauseParameter(whereClause);
        Assert.assertEquals(2, parameters.size());
        Assert.assertEquals("[name, age]", parameters.toString());
    }

    @Test
    public void parseWhereClause() {
        String whereClause = "name = :name and age=:age";
        Map<String, Object> map = Maps.newHashMap();
        map.put("name","zhangsan");
        map.put("age", 10);
        String clause = StringUtils.parseWhereClause(whereClause, map);
        Assert.assertEquals("name = 'zhangsan' and age='10'", clause);
    }

    @Test
    public void join() throws Exception {
        List<String> list = Lists.newArrayList("a", "b", null, "c", "d");
        // 去除空字符串后按照-拼接
        String joinStrWithOutNull = StringUtils.join(list, "-");
        Assert.assertEquals("a-b-c-d", joinStrWithOutNull);
    }

}