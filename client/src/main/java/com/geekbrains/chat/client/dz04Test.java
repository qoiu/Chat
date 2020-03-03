package com.geekbrains.chat.client;

public class dz04Test {
    private final Object o = new Object();
    private volatile char lastLetter = 'c';

    public static void main(String[] args) {
        dz04Test w=new dz04Test();
        new Thread(()->{
        w.print('b','a');
        }).start();
        new Thread(()->{
            w.print('c','b');
        }).start();
        new Thread(()->{
            w.print('a','c');
        }).start();
    }

    public void print(char letter,char after){
        synchronized (o) {
            try{
                for (int i = 0; i < 5; i++) {
                    while (lastLetter!=after) {
                        o.wait();
                    }
                    lastLetter = letter;
                    System.out.print(letter);
                    o.notifyAll();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
    }

}
