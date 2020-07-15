package com.tyrival.jvm.lession02;

import com.tyrival.jvm.lession01.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OOMTest {

    public static List<Object> list = new ArrayList<>();

    public static void main(String[] args) {
        List<Object> list = new ArrayList<>();
        int i = 0;
        int j = 0;
        while (true) {
            list.add(new User(i++, UUID.randomUUID().toString()));
            new User(j--, UUID.randomUUID().toString());
        }
    }
}
