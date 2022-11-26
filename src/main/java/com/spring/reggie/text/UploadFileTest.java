package com.spring.reggie.text;

import org.junit.Test;


public class UploadFileTest {

    @Test
    public void test1(){
        String fileName="ererewe.jpg";
        String suffix=fileName.substring(fileName.lastIndexOf("."));
        System.out.println(suffix);

    }
}
