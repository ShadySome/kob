package com.kob.botrunningsystem.service.impl.utils;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BotPool extends Thread{
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();//条件变量
    private final Queue<Bot> bots = new LinkedList<>();//待执行的任务

    public void addBot(Integer userId,String botCode,String input)
    {
        lock.lock();
        try{
            bots.add(new Bot(userId,botCode,input));
            condition.signalAll();
        }finally {
            lock.unlock();
        }
    }

    private void consume(Bot bot)
    {
        Consumer consumer = new Consumer();
        consumer.startTimeOut(2000,bot);
    }

    @Override
    public void run() {
        while(true)
        {
            lock.lock();
            if(bots.isEmpty())
            {
                try {
                    condition.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    lock.unlock();
                    break;
                }
            }
            else{
                Bot bot = bots.remove(); //取出队头任务执行
                lock.unlock();
                consume(bot); //可能耗时较长
            }
        }
    }
}
