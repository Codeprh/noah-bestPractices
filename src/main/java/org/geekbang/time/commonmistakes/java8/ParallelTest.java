package org.geekbang.time.commonmistakes.java8;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.util.StopWatch;

import java.time.LocalDateTime;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

@Slf4j
public class ParallelTest {

    @Test
    public void parallel() {

        IntStream.rangeClosed(1, 100).parallel().forEach(i -> {

            System.out.println(LocalDateTime.now() + " : " + i);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
        });
    }

    @Test
    public void noahTest() throws InterruptedException {

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.execute(() -> {
            try {
                Thread.sleep(1000);
                System.out.println("子线程执行完了");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        executorService.shutdown();
        executorService.awaitTermination(1100, TimeUnit.MILLISECONDS);
        System.out.println("主线程执行完毕");
    }

    @Test
    public void allMethods() throws InterruptedException, ExecutionException {

        int taskCount = 10000;
        int threadCount = 40;
        StopWatch stopWatch = new StopWatch();

        stopWatch.start("thread");
        Assert.assertEquals(taskCount, thread(taskCount, threadCount));
        stopWatch.stop();

        stopWatch.start("threadpool");
        Assert.assertEquals(taskCount, threadpool(taskCount, threadCount));
        stopWatch.stop();

        //试试把这段放到forkjoin下面？
        stopWatch.start("stream");
        Assert.assertEquals(taskCount, stream(taskCount, threadCount));
        stopWatch.stop();

        stopWatch.start("forkjoin");
        Assert.assertEquals(taskCount, forkjoin(taskCount, threadCount));
        stopWatch.stop();

        stopWatch.start("completableFuture");
        Assert.assertEquals(taskCount, completableFuture(taskCount, threadCount));
        stopWatch.stop();

        log.info(stopWatch.prettyPrint());
    }

    private void increment(AtomicInteger atomicInteger) {
        atomicInteger.incrementAndGet();
        try {
            TimeUnit.MILLISECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 第一种方式是使用线程。直接把任务按照线程数均匀分割，分配到不同的线程执行，使用 CountDownLatch 来阻塞主线程，直到所有线程都完成操作。
     * 这种方式，需要我们自己分 割任务
     *
     * @param taskCount
     * @param threadCount
     * @return
     * @throws InterruptedException
     */
    private int thread(int taskCount, int threadCount) throws InterruptedException {

        AtomicInteger atomicInteger = new AtomicInteger();

        //size的大小是线程数的大小，而不是任务数的大小
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        IntStream.rangeClosed(1, threadCount).mapToObj(i -> new Thread(() -> {
            IntStream.rangeClosed(1, taskCount / threadCount).forEach(j -> increment(atomicInteger));
            countDownLatch.countDown();
        })).forEach(Thread::start);

        countDownLatch.await();

        return atomicInteger.get();
    }

    /**
     * 第二种方式是，使用 Executors.newFixedThreadPool 来获得固定线程数的线程池，使用 execute 提交所有任务到线程池执行，最后关闭线程池等待所有任务执行完成:
     *
     * @param taskCount
     * @param threadCount
     * @return
     * @throws InterruptedException
     */
    private int threadpool(int taskCount, int threadCount) throws InterruptedException {

        AtomicInteger atomicInteger = new AtomicInteger();
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        IntStream.rangeClosed(1, taskCount).forEach(i -> executorService.execute(() -> increment(atomicInteger)));

        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.HOURS);

        return atomicInteger.get();
    }

    /**
     * 第三种方式是，使用 ForkJoinPool 而不是普通线程池执行任务。
     * <p>
     * ForkJoinPool 和传统的 ThreadPoolExecutor 区别在于，前者对于 n 并行度有 n 个独立 队列，后者是共享队列。
     * 如果有大量执行耗时比较短的任务，ThreadPoolExecutor 的单队 列就可能会成为瓶颈。
     * 这时，使用 ForkJoinPool 性能会更好。
     *
     * @param taskCount
     * @param threadCount
     * @return
     * @throws InterruptedException
     */
    private int forkjoin(int taskCount, int threadCount) throws InterruptedException {

        AtomicInteger atomicInteger = new AtomicInteger();
        //定义并行度
        ForkJoinPool forkJoinPool = new ForkJoinPool(threadCount);
        forkJoinPool.execute(() -> IntStream.rangeClosed(1, taskCount).parallel().forEach(i -> increment(atomicInteger)));

        forkJoinPool.shutdown();
        forkJoinPool.awaitTermination(1, TimeUnit.HOURS);

        return atomicInteger.get();
    }

    /**
     * 第四种方式是，直接使用并行流，并行流使用公共的 ForkJoinPool，也就是 ForkJoinPool.commonPool()。
     *
     * @param taskCount
     * @param threadCount
     * @return
     */
    private int stream(int taskCount, int threadCount) {

        //设置公共ForkJoinPool的并行度
        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", String.valueOf(threadCount));

        AtomicInteger atomicInteger = new AtomicInteger();
        IntStream.rangeClosed(1, taskCount).parallel().forEach(i -> increment(atomicInteger));

        return atomicInteger.get();
    }

    /**
     * 第五种方式是，使用 CompletableFuture 来实现
     *
     * @param taskCount
     * @param threadCount
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     */
    private int completableFuture(int taskCount, int threadCount) throws InterruptedException, ExecutionException {

        AtomicInteger atomicInteger = new AtomicInteger();
        ForkJoinPool forkJoinPool = new ForkJoinPool(threadCount);

        CompletableFuture.runAsync(() -> IntStream.rangeClosed(1, taskCount).parallel().forEach(i -> increment(atomicInteger)), forkJoinPool).get();
        return atomicInteger.get();

    }
}
