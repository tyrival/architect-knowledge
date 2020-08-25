package com.tyrival.concurrent.lession04;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class Thread_Interrupt {

    private static ReentrantLock lock = new ReentrantLock();

    public static void reentrantLock() throws InterruptedException {
        String threadName = Thread.currentThread().getName();
        lock.lockInterruptibly();
        log.info("Thread: {}, 加锁成功。", threadName);
        while (true) {
            if (Thread.interrupted()) {
                break;
            }
        }
        lock.unlock();
        log.info("Thread: {}, 退出锁同步块。", threadName);

    }
}
