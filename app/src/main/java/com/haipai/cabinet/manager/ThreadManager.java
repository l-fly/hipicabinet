package com.haipai.cabinet.manager;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class ThreadManager {
    static final ThreadPoolExecutor ex = new ThreadPoolExecutor(0,Integer.MAX_VALUE,60, TimeUnit.MINUTES, new SynchronousQueue<Runnable>());
    public interface IExecuteExceptionReceiver{
        void onException(Exception e);
    }

    public static void execute(Runnable runnable){
        execute(runnable, null);
    }

    public static void execute(Runnable runnable, IExecuteExceptionReceiver receiver){
        try{
            ex.execute(runnable);
        }catch (Exception e){
            if(receiver!=null){
                receiver.onException(e);
            }
        }

    }
}
