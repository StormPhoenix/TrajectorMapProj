package com.stormphoenix.graduatedesign.hotpoint;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Developer on 18-5-25.
 */
public class HotpointMap {
    private static HotpointMap INSTANCE;
    private static final Long TIME_SCALE = 5000L;
    private Integer width;
    private Integer height;
    // 初始时候，queueA用作缓存GPS数据
    private volatile Queue<Hotpoint> firstQueue = new ConcurrentLinkedQueue();
    private volatile Queue<Hotpoint> secondQueue = new ConcurrentLinkedQueue();
    // 以下用于同步控制两个队列
    private volatile Lock firstQueueLock = new ReentrantLock();
    private volatile Lock secondQueueLock = new ReentrantLock();
    private volatile Lock firstMutex = new ReentrantLock();
    private volatile Lock secondMutex = new ReentrantLock();
    private volatile Lock switchLock = new ReentrantLock();
    private volatile AtomicInteger firstReaderCount = new AtomicInteger(0);
    private volatile AtomicInteger secondReaderCount = new AtomicInteger(0);
    private volatile AtomicInteger[][] hotpointMap;
    private volatile int[][] hotpointMapResult;
    private volatile Boolean isSwitch = false;

    private ExecutorService executorService;

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
        executorService = Executors.newFixedThreadPool(3);
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                pollGpsTask();
            }
        });
    }

    int[][] getCurrentHotpointMap() {
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
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                offerGpsTask(hotpoint);
            }
        });
    }

    /**
     * offerGpsTask 和　pollGpsTask 相当于一对读写锁，
     * 这里暂时采取写锁优先的策略
     */
    private void offerGpsTask(Hotpoint hotpoint) {
        int count;
        switchLock.lock();
        if (!isSwitch) {
            // use firstQueue
            firstMutex.lock();
            switchLock.unlock();
            count = firstReaderCount.incrementAndGet();
            if (count == 1) {
                firstQueueLock.lock();
            }
            firstMutex.unlock();
            firstQueue.offer(hotpoint);
            firstMutex.lock();
            count = firstReaderCount.decrementAndGet();
            if (count == 0) {
                firstQueueLock.unlock();
            }
            firstMutex.unlock();
        } else {
            // use secondQueue
            secondMutex.lock();
            switchLock.unlock();
            count = secondReaderCount.incrementAndGet();
            if (count == 1) {
                secondMutex.lock();
            }
            secondMutex.unlock();
            secondQueue.offer(hotpoint);
            secondMutex.lock();
            count = secondReaderCount.decrementAndGet();
            if (count == 0) {
                secondQueueLock.unlock();
            }
            secondMutex.unlock();
        }
    }

    private void pollGpsTask() {
        while (true) {
            if (isSwitch) {
                // use firstQueue
                firstQueueLock.lock();
                while (!firstQueue.isEmpty()) {
                    Hotpoint hotpoint = firstQueue.peek();
                    if (System.currentTimeMillis() - hotpoint.getTimestamp() >= 5000) {
                        hotpointMap[hotpoint.getRowIndex()][hotpoint.getColIndex()].decrementAndGet();
                        firstQueue.poll();
                    }
                }
                firstQueueLock.unlock();
            } else {
                // use secondQueue
                secondQueueLock.lock();
                while (!secondQueue.isEmpty()) {
                    Hotpoint hotpoint = secondQueue.peek();
                    if (System.currentTimeMillis() - hotpoint.getTimestamp() >= 5000) {
                        hotpointMap[hotpoint.getRowIndex()][hotpoint.getColIndex()].decrementAndGet();
                        secondQueue.poll();
                    }
                }
                secondQueueLock.unlock();
            }
            switchLock.lock();
            isSwitch = !isSwitch;
            switchLock.unlock();
        }
    }
}
