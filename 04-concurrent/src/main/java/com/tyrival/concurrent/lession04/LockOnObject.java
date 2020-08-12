package com.tyrival.concurrent.lession04;

public class LockOnObject {
    public static Object object = new Object();

    private Integer stock = 10;

    public void decrStock(){
        //T1,T2
        synchronized (object){
            --stock;
            if(stock <= 0){
                System.out.println("库存售罄");
                return;
            }
        }
    }
}