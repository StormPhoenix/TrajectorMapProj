package com.stormphoenix.graduatedesign.hotpoint;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Developer on 18-5-25.
 */
public class HotpointMap {
    public static AtomicInteger readCount = new AtomicInteger(0);
    public static AtomicInteger writeCount = new AtomicInteger(0);

    private static HotpointMap INSTANCE;
    private static final Long TIME_SCALE = 5000L;
    private Integer width;
    private Integer height;
    // 初始时候，queueA用作缓存GPS数据
    private volatile Queue<Hotpoint> firstQueue = new ConcurrentLinkedQueue();
    private volatile Queue<Hotpoint> secondQueue = new ConcurrentLinkedQueue();
    // 以下用于同步控制两个队列
    private volatile Semaphore firstQueueSemaphore = new Semaphore(1, true);
    private volatile Semaphore secondQueueSemaphore = new Semaphore(1, true);
    private volatile Lock firstMutex = new ReentrantLock();
    private volatile Lock secondMutex = new ReentrantLock();
    private volatile Lock switchLock = new ReentrantLock();
    private volatile AtomicInteger firstReaderCount = new AtomicInteger(0);
    private volatile AtomicInteger secondReaderCount = new AtomicInteger(0);
    private volatile AtomicInteger[][] hotpointMap;
    private volatile int[][] hotpointMapResult;
    private volatile Boolean isSwitch = false;

    private ExecutorService executorService;

    // TODO 这里不能使用单例模式，后期修改
    public static HotpointMap getInstance(int width, int height) {
        if (INSTANCE == null) {
            synchronized (HotpointMap.class) {
                if (INSTANCE == null) {
                    INSTANCE = new HotpointMap(width, height);
                }
            }
        }
        return INSTANCE;
    }

    private HotpointMap(int width, int height) {
        this.width = width;
        this.height = height;
        hotpointMap = new AtomicInteger[height][width];
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                hotpointMap[row][col] = new AtomicInteger(0);
            }
        }
        executorService = Executors.newFixedThreadPool(100);
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                pollHotpointTask();
            }
        });
    }

    public int[][] getHotpointMap() {
        if (hotpointMapResult == null) {
            hotpointMapResult = new int[height][width];
        }
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                hotpointMapResult[row][col] = hotpointMap[row][col].get();
            }
        }
        return hotpointMapResult;
    }

    // 添加　GPS　数据
    public void addHotpoint(int row, int col) {
        Hotpoint hotpoint = new Hotpoint();
        hotpoint.setColIndex(col);
        hotpoint.setRowIndex(row);
        hotpoint.setTimestamp(System.currentTimeMillis());
        executorService.submit(() -> offerHotpointTask(hotpoint));
    }

    /**
     * offerHotpointTask 和　pollHotpointTask 相当于一对读写锁，
     * 这里暂时采取写锁优先的策略
     */
    private void offerHotpointTask(Hotpoint hotpoint) {
        // ---------------- 未采用优化代码 --------------------
//        synchronized (firstQueue) {
//            firstQueue.offer(hotpoint);
//            readCount.incrementAndGet();
//            hotpointMap[hotpoint.getRowIndex()][hotpoint.getColIndex()].incrementAndGet();
//        }
        // ---------------- 未采用优化代码 --------------------
        int count;
        switchLock.lock();
        if (!isSwitch) {
            // use firstQueue
            firstMutex.lock();
            switchLock.unlock();
            count = firstReaderCount.incrementAndGet();
            if (count == 1) {
                try {
                    firstQueueSemaphore.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            firstMutex.unlock();
            firstQueue.offer(hotpoint);
            readCount.incrementAndGet();
            hotpointMap[hotpoint.getRowIndex()][hotpoint.getColIndex()].incrementAndGet();
            firstMutex.lock();
            count = firstReaderCount.decrementAndGet();
            if (count == 0) {
                firstQueueSemaphore.release();
            }
            firstMutex.unlock();
        } else {
            // use secondQueue
            secondMutex.lock();
            switchLock.unlock();
            count = secondReaderCount.incrementAndGet();
            if (count == 1) {
                secondQueueSemaphore.release();
            }
            secondMutex.unlock();
            secondQueue.offer(hotpoint);
            readCount.incrementAndGet();
            hotpointMap[hotpoint.getRowIndex()][hotpoint.getColIndex()].incrementAndGet();
            secondMutex.lock();
            count = secondReaderCount.decrementAndGet();
            if (count == 0) {
                secondQueueSemaphore.release();
            }
            secondMutex.unlock();
        }
    }

    private void pollHotpointTask() {
        // ---------------- 未采用优化代码 --------------------
//        while (true) {
//            synchronized (firstQueue) {
//                while (!firstQueue.isEmpty()) {
//                    Hotpoint hotpoint = firstQueue.peek();
//                    if (System.currentTimeMillis() - hotpoint.getTimestamp() >= TIME_SCALE) {
//                        hotpointMap[hotpoint.getRowIndex()][hotpoint.getColIndex()].decrementAndGet();
//                        firstQueue.poll();
//                        writeCount.incrementAndGet();
//                    }
//                }
//            }
//        }
        // ---------------- 未采用优化代码 --------------------

        while (true) {
            if (isSwitch) {
                // use firstQueue
                try {
                    firstQueueSemaphore.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                while (!firstQueue.isEmpty()) {
                    Hotpoint hotpoint = firstQueue.peek();
                    if (System.currentTimeMillis() - hotpoint.getTimestamp() >= TIME_SCALE) {
                        hotpointMap[hotpoint.getRowIndex()][hotpoint.getColIndex()].decrementAndGet();
                        firstQueue.poll();
                        writeCount.incrementAndGet();
                    }
                }
                firstQueueSemaphore.release();
            } else {
                // use secondQueue
                try {
                    secondQueueSemaphore.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                while (!secondQueue.isEmpty()) {
                    Hotpoint hotpoint = secondQueue.peek();
                    if (System.currentTimeMillis() - hotpoint.getTimestamp() >= 5000) {
                        hotpointMap[hotpoint.getRowIndex()][hotpoint.getColIndex()].decrementAndGet();
                        secondQueue.poll();
                        writeCount.incrementAndGet();
                    }
                }
                secondQueueSemaphore.release();
            }
            switchLock.lock();
            isSwitch = !isSwitch;
            switchLock.unlock();
        }
    }
}
