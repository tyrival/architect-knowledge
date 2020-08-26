package com.tyrival.jvm.lesson08;

import java.util.ArrayList;

public class RuntimeConstantPoolOOM {
    public static void main(String[] args) {
        ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < 10000000; i++) {
            String str = String.valueOf(i).intern();
            list.add(str);
        }
    }
}
