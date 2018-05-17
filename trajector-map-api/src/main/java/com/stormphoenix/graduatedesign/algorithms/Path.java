package com.stormphoenix.graduatedesign.algorithms;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Developer on 18-5-15.
 */
public class Path {
    private List<Point> points;

    public Path() {
        points = new LinkedList();
    }

    public void addPoint(List<Point> points) {
        addPoint((Point[]) points.toArray());
    }

    public void addPoint(Point... points) {
        if (points != null) {
            for (Point point : points) {
                this.points.add(point);
            }
        }
    }

    public Point getPoint(int index) {
        if (index >= 0 && index < points.size()) {
            return points.get(index);
        }
        return null;
    }
}
