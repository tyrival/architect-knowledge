package com.tyrival.concurrent.lession04;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.LockSupport;

@Slf4j
public class Thread_LockSupport {

    public static void main(String[] args) {
//        parkAndUnpark();
        parkAndInterrupt();
    }

    public static void parkAndUnpark() {
        Thread t0 = new Thread(new Runnable() {
            @Override
            public void run() {
                Thread current = Thread.currentThread();
                for (; ; ) {
                    log.info("准备park当前线程: {}", current.getName());
                    LockSupport.park();
                    log.info("当前线程被唤醒: {}", current.getName());
                }
            }
        }, "t0");

        t0.start();

        try {
            Thread.sleep(1000);
            log.info("准备唤醒{}线程", t0.getName());
            LockSupport.unpark(t0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void parkAndInterrupt() {
        Thread t0 = new Thread(new Runnable() {
            @Override
            public void run() {
                Thread current = Thread.currentThread();
                for (; ; ) {
                    log.info("准备park当前线程: {}", current.getName());
                    LockSupport.park();
                    log.info("当前线程被唤醒: {}", current.getName());
                }
            }
        }, "t0");

        t0.start();

        try {
            Thread.sleep(1000);
            log.info("准备唤醒{}线程", t0.getName());
            t0.interrupt();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
