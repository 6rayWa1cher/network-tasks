package org.a6raywa1cher.network_tasks.task2_2;

import lombok.extern.slf4j.Slf4j;
import org.a6raywa1cher.network_tasks.task2.SimpleClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@Slf4j
public class BenchmarkClient {
    public static final int CONCURRENT_THREADS = 35;

    public static final int REQUESTS_PER_THREAD = 10000;

    public static final String HOSTNAME = null;

    public static final int PORT = 25565;

    public static void main(String[] args) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(CONCURRENT_THREADS);

        List<Thread> threadList = new ArrayList<>(CONCURRENT_THREADS);

        long start = System.currentTimeMillis();

        for (int i = 0; i < CONCURRENT_THREADS; i++) {
            int finalI = i;
            Thread thread = new Thread(() -> {
                for (int j = 0; j < REQUESTS_PER_THREAD; j++) {
                    SimpleClient simpleClient = new SimpleClient(HOSTNAME, PORT);
                    try {
                        simpleClient.call();
                    } catch (IOException e) {
                        log.error("IO error", e);
                        break;
                    }
//                    if (j % 1000 == 0 && j != 0) log.info("{} called for {} time", finalI, j);
                }
                latch.countDown();
            });
            thread.start();
            threadList.add(thread);
            Thread.sleep(10);
        }

        latch.await();

        long end = System.currentTimeMillis();

        log.info("Result: {} ms", end - start);

        for (Thread thread : threadList) {
            thread.join();
        }
    }
}
