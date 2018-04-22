package com.newbiest.guava;

import com.google.common.util.concurrent.*;
import com.newbiest.base.exception.ClientException;
import org.junit.Test;

import java.util.concurrent.*;

/**
 * 并发测试
 * Created by guoxunbo on 2018/1/23.
 */
public class ConcurrentTest {

    @Test
    public void futureTaskTest() {
        ListeningExecutorService listeningExecutorService = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(10));

        TaskThread taskThread = new TaskThread();

        ListenableFuture listenableFuture = listeningExecutorService.submit(taskThread);
        ListenableFuture listenableFuture2 = listeningExecutorService.submit(taskThread);
        ListenableFuture listenableFuture3 = listeningExecutorService.submit(taskThread);
        System.out.println(listenableFuture.isDone());
        System.out.println("1");

        try {
            Object o = Futures.successfulAsList(listenableFuture, listenableFuture2, listenableFuture3).get();
            System.out.println(o);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }


    class TaskThread implements Callable {
        @Override
        public Object call() throws Exception {
            Thread.sleep(5000);
            System.out.println("aaa");
            System.out.println("bbb");
            System.out.println("ccc");
            System.out.println("ddd");
            return "a";
        }
    }
}
