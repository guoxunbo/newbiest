package com.newbiest.base.utils;

import com.google.common.collect.Lists;
import com.newbiest.base.exception.ClientException;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by guoxunbo on 2018/1/23.
 */
public class PreConditionalUtilsTest {

    @Test
    public void checkIndexInCollection() throws Exception {
        List<String> list = Lists.newArrayList("1","S", "A");
        try {
            PreConditionalUtils.checkIndexInCollection(3, list);
        } catch (ClientException e) {
            Assert.assertEquals("index (3) must be less than size (3)", e.getErrorCode());
        }
        PreConditionalUtils.checkIndexInCollection(2, list);
    }

    @Test
    public void checkBetweenEnd() throws Exception {
        try {
            PreConditionalUtils.checkBetweenEnd(5, 6, 10);
        } catch (ClientException e) {
            Assert.assertEquals("5 is not between 6 end 10", e.getErrorCode());
        }
        PreConditionalUtils.checkBetweenEnd(5, 5, 10);
    }

    @Test
    public void checkLessThan() throws Exception {
        try {
            PreConditionalUtils.checkLessThan(5, 4);
        } catch (ClientException e) {
            Assert.assertEquals("5 is not less than 4", e.getErrorCode());
        }
        PreConditionalUtils.checkLessThan(3, 4);
    }

    @Test
    public void checkMoreThan() throws Exception {
        try {
            PreConditionalUtils.checkMoreThan(5, 6);
        } catch (ClientException e) {
            Assert.assertEquals("5 is not more than 6", e.getErrorCode());
        }
        PreConditionalUtils.checkMoreThan(7, 6);
    }

    @Test
    public void checkNotNull() throws Exception {
        try {
            PreConditionalUtils.checkNotNull(null, "Test");
        } catch (ClientException e) {
            Assert.assertEquals("Test is null", e.getErrorCode());
        }
        PreConditionalUtils.checkNotNull(1, "Test");
    }

}