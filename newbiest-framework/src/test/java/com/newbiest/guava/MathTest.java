package com.newbiest.guava;

import com.google.common.math.IntMath;
import org.junit.Assert;
import org.junit.Test;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by guoxunbo on 2018/5/8.
 */
public class MathTest {

    @Test
    public void roundingModeTest() {
        NumberFormat numberFormat = NumberFormat.getInstance();
        // 设置小数位0位即不要小数
        numberFormat.setMaximumFractionDigits(0);
        // UP: 远离0的方向舍入 根据小数位决定，如当小数位是0的时候，大于0时+1 小于0时减1
        numberFormat.setRoundingMode(RoundingMode.UP);
        Assert.assertEquals("11", numberFormat.format(10.3));
        Assert.assertEquals("-2", numberFormat.format(-1.1));
        // DOWN: 靠近0的方向舍入 根据小数位决定，如当小数位是0的时候，大于0时-1 小于0的时候舍去
        numberFormat.setRoundingMode(RoundingMode.DOWN);
        Assert.assertEquals("10", numberFormat.format(10.6));
        Assert.assertEquals("-1", numberFormat.format(-1.1));
        // CELLLING: 向正无穷大方向舍入 即>0的时候+1 小于0的时候去舍去
        numberFormat.setRoundingMode(RoundingMode.CEILING);
        Assert.assertEquals("11", numberFormat.format(10.6));
        Assert.assertEquals("-1", numberFormat.format(-1.1));
        // FLOOR: 向负无穷大的方向舍入 即>0的时候舍去 小于0的时候-1
        numberFormat.setRoundingMode(RoundingMode.FLOOR);
        Assert.assertEquals("10", numberFormat.format(10.6));
        Assert.assertEquals("-2", numberFormat.format(-1.1));
        // HALF_UP: 四舍五入
        numberFormat.setRoundingMode(RoundingMode.HALF_UP);
        Assert.assertEquals("11", numberFormat.format(10.6));
        Assert.assertEquals("-1", numberFormat.format(-1.1));
        // HALF_DOWN: 不包括0.5的四舍五入 即>0.5的时候才加1 小于0.5则舍去
        numberFormat.setRoundingMode(RoundingMode.HALF_DOWN);
        Assert.assertEquals("10", numberFormat.format(10.5));
        Assert.assertEquals("-1", numberFormat.format(-1.1));
        // HALF_EVEN: 小数位靠近那边往那边 如果间距相等则靠近偶数
        numberFormat.setRoundingMode(RoundingMode.HALF_EVEN);
        Assert.assertEquals("10", numberFormat.format(10.5));
        Assert.assertEquals("-1", numberFormat.format(-1.1));
        Assert.assertEquals("12", numberFormat.format(11.5));
        Assert.assertEquals("-2", numberFormat.format(-1.5));

        // UNNECESSARY: 不需要舍入 保证位数一样，当位数不一样的时候，抛出ArithmeticException
        numberFormat.setRoundingMode(RoundingMode.UNNECESSARY);

        try {
            Assert.assertEquals("10", numberFormat.format(10.5));
        } catch (Exception e) {
            assert e instanceof ArithmeticException;
            Assert.assertEquals("Rounding needed with the rounding mode being set to RoundingMode.UNNECESSARY", e.getMessage());
        }
        Assert.assertEquals("-1", numberFormat.format(-1.0));
    }

    @Test
    public void intMathTest() {
        // 检查两数相加后，，是否溢出
        Assert.assertEquals(5, IntMath.checkedAdd(2, 3));
        try {
            // 抛出ArithmeticException
            IntMath.checkedAdd(Integer.MAX_VALUE, 1);
        } catch (Exception e) {
            assert e instanceof ArithmeticException;
            Assert.assertEquals("overflow", e.getMessage());
        }
        // 检查两个整数相减后，是否溢出
        Assert.assertEquals(4, IntMath.checkedSubtract(5, 1));
        try {
            // 抛出ArithmeticException
            IntMath.checkedSubtract(Integer.MIN_VALUE, 1);
        } catch (Exception e) {
            assert e instanceof ArithmeticException;
            Assert.assertEquals("overflow", e.getMessage());
        }
        // 检查两个整数相乘后，是否溢出
        Assert.assertEquals(160, IntMath.checkedMultiply(20, 8));
        try {
            // 抛出ArithmeticException
            IntMath.checkedMultiply(Integer.MAX_VALUE, 2);
        } catch (Exception e) {
            assert e instanceof ArithmeticException;
        }
        // 检查一个整数的N次幂（即把一个整数自乘N次），是否溢出
        Assert.assertEquals(25, IntMath.checkedPow(5, 2));
        try {
            // 抛出ArithmeticException
            IntMath.checkedPow(Integer.MAX_VALUE, 2);
        } catch (Exception e) {
            assert e instanceof ArithmeticException;
        }

        // 验证一个数是不是2的指数即是不是2的n次方
        assert IntMath.isPowerOfTwo(16);

        // 对一个数开平方根，根据RoundingMode取值
        Assert.assertEquals(2, IntMath.sqrt(4, RoundingMode.HALF_UP));

        // 除数返回A/B的值，根据RoundingMode取值
        Assert.assertEquals(5, IntMath.divide(10, 2, RoundingMode.HALF_UP));
        Assert.assertEquals(3, IntMath.divide(10, 4, RoundingMode.HALF_UP));

        // 取余数 即A%B 和普通取余数不同的是，一直返回非负数 算法为：((x%m)+m)%m
        Assert.assertEquals(1, IntMath.mod(10, 3));
        Assert.assertEquals(3, IntMath.mod(-9, 4));

        // 最大公约数
        Assert.assertEquals(5, IntMath.gcd(10, 5));
    }
}
