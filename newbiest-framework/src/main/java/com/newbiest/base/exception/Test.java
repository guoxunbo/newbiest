package com.newbiest.base.exception;

/**
 * Created by guoxunbo on 2018/7/11.
 */
public class Test {

    public static void main(String[] args) {
        ClientParameterException clientParameterException = new ClientParameterException("aaa", 1, 2, 3);
        System.out.println(clientParameterException.getErrorCode());
    }
}
