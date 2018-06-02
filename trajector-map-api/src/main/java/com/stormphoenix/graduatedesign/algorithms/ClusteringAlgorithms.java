package com.stormphoenix.graduatedesign.algorithms;

import com.stormphoenix.graduatedesign.algorithms.basic.MathTools;
import com.stormphoenix.graduatedesign.algorithms.basic.Path;
import com.stormphoenix.graduatedesign.algorithms.basic.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Developer on 18-6-2.
 */
public class ClusteringAlgorithms {
    /**
     * 聚合多余的点
     * @param path
     * @param distanceScale
     * @return
     */
    public Path clusterExtraPoints(Path path, double distanceScale) {
        // TODO 检测 path 长度是否合法
        List<Point> clusteringPoints = new ArrayList<>();
        // 在 startIndex 和　endIndex 之间的点要进行聚类
        int startIndex = 0;
        int endIndex;
        boolean clusterFlag = false;
        List<Point> points = path.getPoints();
        for (int index = 0; index < points.size(); index++) {
            endIndex = index;
            for (int i = startIndex; i < endIndex; i++) {
                if (MathTools.calculateDoublePointsDistance(points.get(i), points.get(endIndex)) > distanceScale) {
                    clusterFlag = true;
                }
            }
            if (clusterFlag) {
                clusteringPoints.add(clusterPoints(startIndex, endIndex - 1, points));
                clusterFlag = false;
                startIndex = endIndex;
                if (endIndex == points.size() - 1) {
                    clusteringPoints.add(points.get(endIndex));
                }
            } else if (endIndex == points.size() - 1) {
                clusteringPoints.add(clusterPoints(startIndex, endIndex, points));
            }
        }
        Path result = new Path();
        result.addPoints(clusteringPoints);
        return result;
    }

    private Point clusterPoints(int startIndex, int endIndex, List<Point> points) {
        double sumX = 0;
        double sumY = 0;
        for (int i = startIndex; i <= endIndex; i++) {
            sumX += points.get(i).getX();
            sumY += points.get(i).getY();
        }
        return new Point(sumX / (endIndex - startIndex), sumY / (endIndex - startIndex));
    }
}
