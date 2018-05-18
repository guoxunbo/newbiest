package com.newbiest.guava;

import com.google.common.collect.*;
import com.google.common.primitives.Ints;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by guoxunbo on 2018/5/8.
 */
public class RangeTest {

    @Test
    public void rangeTest() {
        // BoundType是个枚举只有OPEN/CLOSE2个值即表示开区间还是闭区间如(1, 10]
        Range.range(1, BoundType.OPEN, 10, BoundType.CLOSED);
        // Range还提供了一系列方便创建的做法
        Range.open(1, 10);//(1, 10)
        Range.closed(1, 10);// [10, 10]
        Range.openClosed(1, 10);//(1, 10]
        Range.closedOpen(1, 10);//[1, 10)

        Range.greaterThan(1);//(1, +∞)
        Range.atLeast(1);//[1, +∞)
        Range.lessThan(1);//(-∞, 1)
        Range.atMost(1);//(-∞, 1]
        Range.all();//(-∞, +∞]

        // 验证区间
        Range range = Range.open(1, 10);//(1, 10)
        assert !range.isEmpty();
        assert range.contains(5);
        assert !range.contains(1);
        assert !range.contains(10);

        range = Range.range(1, BoundType.CLOSED, 10, BoundType.CLOSED);
        assert range.contains(1);
        assert range.contains(10);
        assert range.containsAll(Ints.asList(1, 2, 3, 4, 5));
        // 取边界类型，当边界存在的时候返回边界类型，边界不存在抛出IllegalStateException
        Assert.assertEquals("CLOSED", range.lowerBoundType().toString());
        Assert.assertEquals("CLOSED", range.upperBoundType().toString());

        // 取边界值，当边界存在的时候返回边界值，边界不存在抛出IllegalStateException 故取值的时候先判断边界是否存在
        Assert.assertEquals("1", range.lowerEndpoint().toString());
        Assert.assertEquals("10", range.upperEndpoint().toString());

        // 判断R1是否包含R2
        assert Range.closed(3, 6).encloses(Range.closed(4, 5));//[3..6] 包含[4..5]
        //[4, 5] (3, 6) 虽然前者包含了后者的所有值，但是不是包含关系。离散域[discrete domains]可以解决这个问题
        assert !Range.closed(4, 5).encloses(Range.open(3, 6));

        //判断2个Range里面的元素是否相连
        assert Range.closed(3, 5).isConnected(Range.open(5, 10));
        assert Range.closed(0, 9).isConnected(Range.closed(3, 4));
        assert !Range.open(3, 5).isConnected(Range.open(5, 10));

        // 2个range的交集 当2个range没有交集的时候抛出IllegalStateException
        Range r = Range.closed(3, 5).intersection(Range.open(5, 10)); // (5, 5]
        Assert.assertEquals("OPEN", r.lowerBoundType().toString());
        Assert.assertEquals("CLOSED", r.upperBoundType().toString());
        Assert.assertEquals("5", r.lowerEndpoint().toString());
        Assert.assertEquals("5", r.upperEndpoint().toString());


        r = Range.closed(0, 9).intersection(Range.closed(3, 4)); // [3, 4]
        Assert.assertEquals("CLOSED", r.lowerBoundType().toString());
        Assert.assertEquals("CLOSED", r.upperBoundType().toString());
        Assert.assertEquals("3", r.lowerEndpoint().toString());
        Assert.assertEquals("4", r.upperEndpoint().toString());

        try {
            Range.open(3, 5).intersection(Range.open(5, 10));
        } catch (Exception e) {
            Assert.assertEquals("Invalid range: (5..5)", e.getMessage());
        }

        //跨区间 即2个区间合并，如果区间相连则就是并集，不相连则取最小值和最大值
        Range.closed(3, 5).span(Range.open(5, 10)); // [3, 10)
        Range.closed(0, 9).span(Range.closed(3, 4)); // [0, 9]
        Range.closed(0, 5).span(Range.closed(3, 9)); // [0, 9]


    }


}
