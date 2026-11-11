package com.mccurry.game.threading;

import com.mccurry.game.Main;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class ThreadingTools {
    private static GameProcess process;

    public static void initialize(GameProcess gameProcess){
        process = gameProcess;
    }


    public static void enqueue(Runnable runnable){
        process.getGame().enqueue(runnable);
    }

    public static <V> Future<V> enqueue(Callable<V> callable){
        return process.getGame().enqueue(callable);
    }

    public static void smartEnqueue(Runnable runnable){
        if(Thread.currentThread().equals(process.getMainThread())){
            runnable.run();
            return;
        }

        process.getGame().enqueue(runnable);
    }

    public static <V> Future<V> smartEnqueue(Callable<V> callable){
        if(!Thread.currentThread().equals(process.getMainThread())){
            return process.getGame().enqueue(callable);
        }

        return process.getGame().enqueue(callable);
    }
}
