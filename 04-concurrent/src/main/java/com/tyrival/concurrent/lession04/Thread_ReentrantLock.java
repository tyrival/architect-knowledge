package com.tyrival.concurrent.lession04;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class Thread_ReentrantLock {

    static ReentrantLock lock = new ReentrantLock();

    static boolean flag = false;

    public static void main(String[] args) {
        List<Thread> list = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            Thread t = new Thread(()-> {
                lock.lock();
                System.out.println(Thread.currentThread().getName() + " get lock");
                while(!flag) {
                    if (flag) {
                        break;
                    }
                }
                lock.unlock();
            }, "t-" + i);
            list.add(t);
            t.start();
        }

        try {
            Thread.sleep(2000);
            list.get(3).interrupt();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
