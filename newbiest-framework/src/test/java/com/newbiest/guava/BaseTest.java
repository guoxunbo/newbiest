package com.newbiest.guava;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import com.google.common.base.Throwables;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Ints;
import com.newbiest.base.exception.ClientException;
import org.assertj.core.util.BigDecimalComparator;
import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by guoxunbo on 2018/1/19.
 */
public class BaseTest {

    @Test
    public void baseUtilsTest() {
        preConditionTest();
    }

    @Test
    public void optionalTest() {
        String str1 = null;
        String str2 = "str2";

        Optional<String> a = Optional.fromNullable(str1);
        //返回包含给定的非空引用Optional实例
        Optional<String> b = Optional.of(str2);

        // 如果为空则isPresent为false
        assert !a.isPresent();
        assert b.isPresent();
        //返回Optional所包含的引用,若引用缺失,返回指定的值
        String value1 = a.or("1111");
        //返回所包含的实例,它必须存在, 如果不存在则会抛出异常，故通常在调用该方法时会调用isPresent()判断是否为null
        String value2 = b.get();
        Assert.assertEquals("1111str2", value1+value2);
    }

    /**
     * 先决条件检查
     */
    @Test
    public void preConditionTest() {
        try {
            // 检查失败也不会抛出异常
            Preconditions.checkArgument(1 == 1);
            // 检查失败会抛出异常 并按照errorMessageTemplate里面组合提示
            Preconditions.checkArgument(1 == 2, "Expected %s but %s", 1, 2);
        } catch (Exception e) {
            Assert.assertEquals("Expected 1 but 2", e.getMessage());
        }
        try {
            List<String> strList = Lists.newArrayList();
            // 检查index是否在当前集合之中，如果不存在则抛出IndexOutOfBoundsException
            Preconditions.checkElementIndex(5, strList.size());
        } catch (IndexOutOfBoundsException e) {
            Assert.assertEquals("index (5) must be less than size (0)", e.getMessage());
        }
    }

    @Test
    public void stopWatchTest() throws Exception {
        // 创建一个Stopwatch并开始
        Stopwatch stopwatch = Stopwatch.createStarted();
        // do something
        Thread.sleep(3000);
        // 停止
        stopwatch.stop();
        // 计算时间 执行时间可以按照毫秒，秒，分钟，小时等单位进行相应的换算
        long durationTime = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        assert durationTime > 3000 && durationTime < 3010;
    }

    /**
     * 比较器测试使用JDK1.8 因1.8更强大
     */
    @Test
    public void compareTest() {
        List<Integer> list = Lists.newArrayList(1, 2,4, 10, 6, 12);
        // 正常排序 注:Stream都是基于源集合copy一个新的集合出来。
        list = list.stream().sorted().collect(Collectors.toList());
        Assert.assertEquals("[1, 2, 4, 6, 10, 12]", list.toString());
        // 集合中可以实现Comparator自由根据属性进行排序 reversed是做倒序
        list = list.stream().sorted(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1 > o2 ? 1 : -1;
            }
        }.reversed()).collect(Collectors.toList());
        Assert.assertEquals("[12, 10, 6, 4, 2, 1]", list.toString());

        // 使用lambda表达式实现Comparator类 也可以改变返回值实现倒序
        list = list.stream().sorted((Integer o1, Integer o2) -> {
            return o1 > o2 ? -1 : 1;
        }).collect(Collectors.toList());
        Assert.assertEquals("[12, 10, 6, 4, 2, 1]", list.toString());

    }

}
