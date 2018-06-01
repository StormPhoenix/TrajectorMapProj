package com.stormphoenix.graduatedesign.algorithms.basic;

import java.util.List;

/**
 * Created by Developer on 18-6-1.
 * <p>
 * 用于方便计算过程的类
 */
public class MathTools {
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
}
