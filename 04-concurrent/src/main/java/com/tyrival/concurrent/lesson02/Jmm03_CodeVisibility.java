package com.tyrival.concurrent.lesson02;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Jmm03_CodeVisibility {
    private static boolean initFlag = false;

    // 这里有几个"卧艹"的操作
    // private static int counter = 0; 此时initFlag是不可见的
    // private static Integer counter = 0; 此时initFlag是可见的
    // private volatile static int counter = 0; 此时initFlag是可见的
    // 下面while循环中有内容和无内容，执行效果又不一样。
    // 所以具体场景下的可见性是完全无法保证的。
    // volatile只是保证了一个原则：变量一定能在修改后被及时看到。
    private volatile static int counter = 0;

    public static void refresh() {
        log.info("refresh data.......");
        initFlag = true;
        log.info("refresh data success.......");
    }

    public static void main(String[] args) {
        Thread threadA = new Thread(() -> {
            while (!initFlag) {
                //System.out.println("runing");
                counter++;
            }
            log.info("线程：" + Thread.currentThread().getName() + "当前线程嗅探到initFlag的状态的改变");
        }, "threadA");
        threadA.start();

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Thread threadB = new Thread(() -> {
            refresh();
        }, "threadB");
        threadB.start();
    }
}
