package com.stormphoenix.graduatedesign.algorithms;

/**
 * Created by Developer on 18-5-15.
 */
public class Point {
    private Double x = null;
    private Double y = null;

    public Point() {
        this(0D, 0D);
    }

    public Point(Double x, Double y) {
        this.x = x;
        this.y = y;
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }
}
