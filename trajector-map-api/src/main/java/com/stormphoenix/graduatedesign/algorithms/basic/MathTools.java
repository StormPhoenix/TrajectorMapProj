package com.stormphoenix.graduatedesign.algorithms.basic;

/**
 * Created by Developer on 18-6-1.
 * <p>
 * 用于方便计算过程的类
 */
public class MathTools {
    /**
     * 计算两点之间的距离
     *
     * @param a
     * @param b
     * @return
     */
    public static double calculateDoublePointsDistance(Point a, Point b) {
        return Math.sqrt(Math.pow(a.getX() - b.getX(), 2) + Math.pow(a.getY() - b.getY(), 2));
    }

    /**
     * 计算data的方差
     *
     * @param datas
     * @return
     */
    public static double calculateVariance(Double[] datas) {
        double sum = 0;
        double average = calculateSum(datas);
        for (double data : datas) {
            sum += Math.pow(data - average, 2);
        }
        return sum;
    }

    /**
     * 计算所有数据之和
     *
     * @param datas
     * @return
     */
    public static double calculateSum(Double[] datas) {
        double sum = 0;
        for (double data : datas) {
            sum += data;
        }
        return sum;
    }

    /**
     * 计算所有数据之和
     *
     * @param datas
     * @return
     */
    public static double calculateSum(Integer[] datas) {
        double sum = 0;
        for (double data : datas) {
            sum += data;
        }
        return sum;
    }

    public static double calculateAverage(Double[] datas) {
        // TODO 这里需要检测除 0 错误
        return calculateSum(datas) / datas.length;
    }
}
