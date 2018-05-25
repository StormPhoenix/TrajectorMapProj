package com.stormphoenix.graduatedesign.redis;

import java.io.Serializable;

/**
 * Created by Developer on 18-5-25.
 */
public class RedisKeyTimeRegion implements Serializable {
    private Long startTime;
    private Long endTime;
    private Double topLatitude;
    private Double bottomLatitude;
    private Double leftLongitude;

    public RedisKeyTimeRegion(Long startTime, Long endTime, Double topLatitude, Double bottomLatitude, Double leftLongitude, Double rightLongitude) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.topLatitude = topLatitude;
        this.bottomLatitude = bottomLatitude;
        this.leftLongitude = leftLongitude;
        this.rightLongitude = rightLongitude;
    }

    private Double rightLongitude;

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Double getTopLatitude() {
        return topLatitude;
    }

    public void setTopLatitude(Double topLatitude) {
        this.topLatitude = topLatitude;
    }

    public Double getBottomLatitude() {
        return bottomLatitude;
    }

    public void setBottomLatitude(Double bottomLatitude) {
        this.bottomLatitude = bottomLatitude;
    }

    public Double getLeftLongitude() {
        return leftLongitude;
    }

    public void setLeftLongitude(Double leftLongitude) {
        this.leftLongitude = leftLongitude;
    }

    public Double getRightLongitude() {
        return rightLongitude;
    }

    public void setRightLongitude(Double rightLongitude) {
        this.rightLongitude = rightLongitude;
    }
}
