package com.stormphoenix.graduatedesign.algorithms;

import com.stormphoenix.graduatedesign.algorithms.basic.MathTools;
import com.stormphoenix.graduatedesign.algorithms.basic.Matrix;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Developer on 18-6-1.
 * <p>
 * TODO 本算法对外提供的接口还不够直观，建议修改
 */
public class MarkovAlgorithms {
    // 样本点
    private List<Integer> exampleStatusData;
    // 轨迹经过的所有数据点总数
    private int statusCounts;
    // 概率转移矩阵
    private double[][][] probablitiesMatrix;
    // 不同马尔科夫预测矩阵的阶数的自相关系数
    private double[] selfCorrelationCoefficient;
    // 不同马尔科夫预测矩阵的阶数的权重
    private double[] stageWeights;
    // 频数矩阵
    private int[][] frequencyMatrix;
    // 马尔科夫预测矩阵的最大阶数
    private int markovStage;
    // 最终预测的概率值
    private double[][] predictValueMatrix;
    // 是否符合马尔科夫性
    private boolean hasMarkovFeature;

    /**
     * 创造一个计算 Markov 预测矩阵的实例
     *
     * @param exampleStatusData 用于计算概率矩阵的数据，包含statusCounts个状态
     * @param statusCounts      　状态的数量，该值决定了状态由 0 ~ statusCounts - 1
     * @param markovStage       　马尔科夫预测矩阵的最大阶数
     */
    public MarkovAlgorithms(List<Integer> exampleStatusData, int statusCounts, int markovStage) {
        this.markovStage = markovStage;
        this.statusCounts = statusCounts;
        this.exampleStatusData = exampleStatusData;
        frequencyMatrix = new int[statusCounts][statusCounts];
        probablitiesMatrix = new double[this.markovStage][statusCounts][statusCounts];
        // 进行频率矩阵，以及转移矩阵的计算
        calculateTransformationProbablitiesMatrix(this.exampleStatusData, statusCounts);
        Matrix matrix = new Matrix();
        if (validateMarkov()) {
            for (int i = 1; i < this.markovStage; i++) {
                matrix.setArray(probablitiesMatrix[i - 1]);
                probablitiesMatrix[i] = matrix.multiply(probablitiesMatrix[0]);
            }
            calculateCorrelationCoefficient();
            calculateStageWeight();
        }
    }

    /**
     * TODO 之前的代码在这里把传入的 data 修改了，这样做是一种很愚蠢的行为。kotlin　针对这种行为做了处理
     *
     * 预测状态概率,传进来的是带时间的测试数据
     *
     * @param data
     * @return 概率矩阵，第ｒ行第ｃ列代表第ｒ步在状态ｃ的概率
     */
    public double[][] predictProbablities(List<Integer> data) {
        List<Integer> dataTest = data;
        if (!hasMarkovFeature || dataTest.size() <= markovStage) {
            // 不符合Markov性质
            return null;
        }
        //　前markovStage个数据是用于进行预测的基础数据，因此只需要预测 dataTest.size() - markovStage个状态
        int[] basicData = new int[markovStage];
        predictValueMatrix = new double[dataTest.size() - markovStage][statusCounts];
        // 这里很关键，权重和滞时的关系要颠倒，循环计算的时候要注意
        int index = 0;
        int predictFrom = 0;
        while (dataTest.size() > predictFrom + markovStage) {
            // 先设置用于预测的基本数据
            for (int i = 0; i < markovStage; i++) {
                basicData[i] = dataTest.get(i + predictFrom);
            }
            for (int i = 0; i < statusCounts; i++) {
                // ｉ代表第ｉ个状态
                for (int j = 0; j < markovStage; j++) {
                    // 滞时期j的数据状态
                    int state = basicData[basicData.length - 1 - j];
                    if (state >= 0) {
                        predictValueMatrix[index][i] += stageWeights[j] * probablitiesMatrix[j][state][i];
                    }
                }
            }
            predictFrom ++;
            index++;
        }
        return predictValueMatrix;
    }

    /**
     * 验证是否满足马氏性,默认的显著性水平是0.05，自由度(m-1))^2
     * X^2>Xa^2((m-1))^2)，m为状态数，只需考虑涉及到的状态总数计科
     */
    private boolean validateMarkov() {
        int totalFij = exampleStatusData.size() - 1;
        double[] cp = new double[statusCounts];
        for (int i = 0; i < statusCounts; i++) {
            for (int j = 0; j < statusCounts; j++) {
                cp[i] += (double) frequencyMatrix[j][i] / totalFij;
            }
        }
        double gm = 0;
        for (int i = 0; i < statusCounts; i++) {
            for (int j = 0; j < statusCounts; j++) {
                if (frequencyMatrix[i][j] > 0 && cp[j] > 0) {
                    gm += 2 * frequencyMatrix[i][j] * Math.abs(Math.log(probablitiesMatrix[0][i][j] / cp[j]));
                }
            }
        }
        double[] table = new double[]{3.84145882069413, 9.48772903678115,
                16.9189776046204, 26.2962276048642, 37.6524841334828,
                50.9984601657106, 66.3386488629688, 83.6752607427210,
                103.009508712226, 124.342113404004, 147.673529763818,
                173.004059094245, 200.333908832898, 229.663226447109,
                260.992119636005, 294.320668884306, 329.648935544535,
                366.976967201223, 406.304801326655, 447.632467830808,
                490.959990876927, 536.287390198110, 583.614682067880,
                632.941880026341, 684.268995430845, 737.596037878713,
                792.923015535393, 850.249935391850, 909.576803468370,
                970.903624977351, 1034.23040445441, 1099.55714586474,
                1166.88385269006, 1236.21052800010, 1307.53717451179,
                1380.86379463852, 1456.19039053135, 1533.51696411377,
                1612.84351711092, 1694.17005107462,};
        Set<Integer> set = new HashSet<Integer>();
        for (Integer value : exampleStatusData) {
            set.add(value);
        }
        hasMarkovFeature = (gm >= table[set.size() - 1]);
        return hasMarkovFeature;
    }

    /**
     * 计算相关系数
     */
    private void calculateCorrelationCoefficient() {
        Integer[] exampleDataArray = exampleStatusData.toArray(new Integer[exampleStatusData.size()]);
        double average = MathTools.calculateSum(exampleDataArray) / (double) exampleStatusData.size();
        double variance = MathTools.calculateSum(exampleDataArray);
        selfCorrelationCoefficient = new double[markovStage];
        for (int stage = 0; stage < markovStage; stage++) {
            double sum = 0;
            for (int l = 0; l < exampleStatusData.size() - markovStage; l++) {
                sum += (exampleStatusData.get(l) - average) * (exampleStatusData.get(l + stage) - average);
            }
            selfCorrelationCoefficient[stage] = Math.abs(sum / variance);
        }
    }

    /**
     * 计算不同阶的权重
     */
    private void calculateStageWeight() {
        stageWeights = new double[markovStage];
        double sum = 0;
        for (int stage = 0; stage < markovStage; stage++) {
            sum += selfCorrelationCoefficient[stage];
        }
        for (int stage = 0; stage < markovStage; stage++) {
            stageWeights[stage] = selfCorrelationCoefficient[stage] / sum;
        }
    }

    /**
     * 统计频数矩阵，以及计算转移概率矩阵
     *
     * @param datas
     * @param statusCount
     */
    private void calculateTransformationProbablitiesMatrix(List<Integer> datas, int statusCount) {
        if (datas.size() < 2)
            return;
        int[] number = new int[statusCount];
        for (int i = 0; i < datas.size() - 1; i++) {
            // TODO 这里应该对datas.size()里面的数据的大小做判断，应该在 0 ~ statusCounts - 1　范围之内
            frequencyMatrix[datas.get(i)][datas.get(i + 1)]++;
            number[datas.get(i)]++;
        }

        for (int i = 0; i < statusCount; i++) {
            for (int j = 0; j < statusCount; j++) {
                if (frequencyMatrix[i][j] > 0) {
                    // 一阶马尔科夫概率转移矩阵
                    probablitiesMatrix[0][i][j] = (double) frequencyMatrix[i][j] / number[i];
                }
            }
        }
    }
//    public static void main(String[] args) {
//        MarkovAlgorithms dm = new MarkovAlgorithms();
//        // 多少阶Markov
//        int lagPeriodI = 2;//����Ʒ�Ԥ��ʱ�Ĳ���
//        int statusCounts = 6;//״̬��
//        ResultTest rst = new ResultTest();
//        List<Integer> data = new ArrayList<Integer>();//ʵ������
//        List<Integer> data2 = new ArrayList<Integer>();//��������
//        List<Integer> data3 = new ArrayList<Integer>();//���������ڴ�������лᷢ���ı䣬���Խ��б���
//        String str = "6,4,4,5,2,4,6,1,2,6,5,6,4,4,6,5,3,6,5,2,5,3,3,4,4,4,1,1,1,1,3,"
//                + "5,6,5,5,5,5,4,6,5,4,1,3,1,3,1,3,1,2,5,2,2,5,"
//                + "5,1,4,4,2,6,1,5,4,6,3,2,2,6,4,4,4,4,3,1,5,3,1,2,6,5,3,6"
//                + ",3,6,4,6,2,4,4,6,3,3,6,2,6,1,3,2,2,6,6,4,4,3,1,4,1,2,6,4,4,1,2";
//        String str2 = "6,4,4,5,2,4,6,1,2,6,5,6,4,4,6,5,3,6,5,2,5,3,3,4,4,4,1,1,1,1,3,"
//                + "5,6,5,5,5,5,4,6,5,4,1,3,1,3,1,3,1,2,5,2,2,5,"
//                + "5,1,4,4,2,6,1,5,4,6,3,2,2,6,4,4,4,4,3,1,5,3,1,2,6,5,3,6"
//                + ",3,6,4,6,2,4,4,6,3,3,6,2,6,1,3,2,2,6,6,4,4,3,1,4,1,2,6,4,4,1,2";
//        try {
//
//            String[] ss = str.split(",");
//            for (String s : ss) {
//                data.add(Integer.parseInt(s));
//            }
//            ss = str2.split(",");
//            for (String s : ss) {
//                data2.add(Integer.parseInt(s));
//                data3.add(Integer.parseInt(s));
//            }
//
//            if (dm.init(data, statusCounts, lagPeriodI)) {
//                double[][] predictmarkov = dm.predictProb(data2);
//                // ����
//                double value = rst.rightRateTotal(predictmarkov, data3,
//                        lagPeriodI);
//                System.out.println(value);
//            }
//            dm.predictProb(data2);
//
//        } catch (Exception e) {
//        }
//    }
}
