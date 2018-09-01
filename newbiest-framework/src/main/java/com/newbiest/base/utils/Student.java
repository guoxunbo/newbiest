package com.newbiest.base.utils;

/**
 * Created by guoxunbo on 2018/8/20.
 */
public class Student extends Person {

    static {
        System.out.println("This is a static student");
    }

    {
        System.out.println("This is a construct student");
    }

    public Student() {
        System.out.println("This is a construct method student");
    }

    void sayHello() {
        System.out.println("Student say hello");
    }

}

