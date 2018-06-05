package com.newbiest.base.utils;

import org.apache.commons.codec.cli.Digest;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;

/**
 * Created by guoxunbo on 2018/1/26.
 */
public class EncryptionUtilsTest {


    @Test
    public void encode() throws Exception {
        String pass1 = DigestUtils.md5Hex("1");
        System.out.println(pass1);
    }

}