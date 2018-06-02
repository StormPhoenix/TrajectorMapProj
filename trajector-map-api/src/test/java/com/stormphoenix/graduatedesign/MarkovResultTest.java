package com.stormphoenix.graduatedesign;

import java.util.List;

/**
 * Created by Developer on 18-6-2.
 */
public class MarkovResultTest {
    // 仅使用马尔科夫链预测，考虑步数
    public static double rightRateTotal(double[][] predictRate, List<Integer> dataTest, int markovStage) {
        if (predictRate == null)
            return -1;
        int rightNumber = 0;
        int errorNumber = 0;
        double rightRate = 0;
        for (int i = 0; i < predictRate.length; i++) {
            double max = 0;
            int next = 0;
            for (int status = 0; status < predictRate[0].length; status++) {
                if (max < predictRate[i][status]) {
                    max = predictRate[i][status];
                    next = status;
                }
            }

            if (next == dataTest.get(i + markovStage)) {
                System.out.println("预测正确");
                rightNumber++;
            } else {
                System.out.println("预测错误");
                errorNumber++;
            }
        }
        System.out.println(rightNumber + errorNumber);
        if (rightNumber + errorNumber > 4) {
            rightRate = (double) rightNumber / (rightNumber + errorNumber);
            System.out.println("正确率 " + rightRate);
            return rightRate;
        }
        return -1;
    }
}
