package com.newbiest.base.utils;

/**
 * Created by guoxunbo on 2018/8/20.
 */
public class Person {

    static {
        System.out.println("This is a static person");
    }

    {
        System.out.println("This is a construct person");
    }

    public Person() {
        System.out.println("This is a construct method person");
    }

    void sayHello() {
        System.out.println("People say hello");
    }

}

