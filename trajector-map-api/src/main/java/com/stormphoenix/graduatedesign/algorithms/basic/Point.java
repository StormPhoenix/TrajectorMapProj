package com.stormphoenix.graduatedesign.algorithms.basic;

/**
 * Created by Developer on 18-5-15.
 */
public class Point {
    private Double x = null;
    private Double y = null;
    // TODO 用于存储额外数据，由应用程序自己定义。这种做法不是很好，所以希望以后改进
    private Object tag;

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

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }
}
