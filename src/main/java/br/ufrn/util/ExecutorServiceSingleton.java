package br.ufrn.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ExecutorServiceSingleton {

    private static ExecutorService exec = null;
    private static Lock lock = new ReentrantLock();

    public ExecutorServiceSingleton() {
    }

    public static ExecutorService getExec(){
        if (exec == null){
            synchronized(lock){
                if(exec==null){
                    exec = Executors.newCachedThreadPool();
                }
            }
        }
        return exec;
    }


}
