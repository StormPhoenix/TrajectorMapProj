package com.stormphoenix.graduatedesign;

import com.stormphoenix.graduatedesign.hotpoint.HotpointMap;

/**
 * Created by Developer on 18-5-28.
 */
public class RandomPositionTask implements Runnable {
    private Double widthFrom;
    private Double widthEnd;
    private Double heightFrom;
    private Double gridScale;
    private Double heightEnd;
    private HotpointMap hotpointMap;

    public RandomPositionTask(Double widthFrom, Double widthEnd, Double heightFrom, Double heightEnd, Double gridScale, HotpointMap hotpointMap) {
        this.widthFrom = widthFrom;
        this.widthEnd = widthEnd;
        this.heightFrom = heightFrom;
        this.heightEnd = heightEnd;
        this.gridScale = gridScale;
        this.hotpointMap = hotpointMap;
    }

    @Override
    public void run() {
        int count = 0;
        double currentWidht, currentHeight;
        while (count < 1000) {
            currentWidht = Math.random() * (widthEnd - widthFrom) + widthFrom;
            currentHeight = Math.random() * (heightEnd - heightFrom) + heightFrom;
            hotpointMap.addHotpoint((int) ((currentHeight - heightFrom) / gridScale), (int) ((currentWidht - widthFrom) / gridScale));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            count++;
        }
    }
}
