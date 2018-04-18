package com.newbiest.base.utils;

import org.junit.Test;

/**
 * Created by guoxunbo on 2018/1/26.
 */
public class EncryptionUtilsTest {
    @Test
    public void encode() throws Exception {
        String pass1 = EncryptionUtils.encode("woaini1314");
        EncryptionUtils.matches("woaini1314",pass1);
    }

}