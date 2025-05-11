package com.dysjsjy.Threads.多线程输出字符串;

public class NumberPrinter2 {
    private int max;
    private int cnt;

    public NumberPrinter2(int max) {
        this.max = max;
        this.cnt = 0;
    }

    public synchronized void evenPrint() {
        while (cnt <= max) {
            if (cnt % 2 == 0) {
                System.out.print(cnt + ", ");
                cnt++;
                notify();
            } else {
                try {
                    wait();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

    }

    public synchronized void oddPrint() {
        while (cnt <= max) {
            if (cnt % 2 != 0) {
                System.out.print(cnt + ", ");
                cnt++;
                notify();
            } else {
                try {
                    wait();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        NumberPrinter2 numberPrinter2 = new NumberPrinter2(100);
        Thread thread = new Thread(() -> {
            numberPrinter2.evenPrint();
        });
        Thread thread2 = new Thread(() -> {
            numberPrinter2.oddPrint();
        });

        thread.start();
        thread2.start();
    }
}
