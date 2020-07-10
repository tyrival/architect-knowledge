package com.tyrival.jvm.lession01;

public class User {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void sout() {
        System.out.println("=======自己的加载器加载类调用方法=======");
    }
}
