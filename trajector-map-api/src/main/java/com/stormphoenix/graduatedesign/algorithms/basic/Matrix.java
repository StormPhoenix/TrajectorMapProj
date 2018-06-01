package com.stormphoenix.graduatedesign.algorithms.basic;

/**
 * Created by Developer on 18-6-1.
 * <p>
 * 矩阵，实现了相乘操作
 */
public class Matrix {
    private int row;
    private int col;
    private double[][] array;

    public double[][] getArray() {
        return array;
    }

    public void setArray(double[][] array) {
        // 判断 二维数组是否为合法矩阵
        int row = array.length;
        int col = array[0].length;
        for (int i = 1; i < row; i++) {
            if (col != array[i].length) {
//                输入的不是一个矩阵,请重新输入
                return;
            }
        }
        this.row = row;
        this.col = col;
        this.array = array;
    }

    @Override
    public String toString() {
        if (array == null) {
            return "\r\n";
        }
        String result = "";
        row = array.length;
        col = array[row - 1].length;

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                result += array[i][j] + " ";
            }
            result += "\r\n";
        }
        return result;
    }

    /**
     * 传入一个矩阵类进行相乘
     */
    public Matrix multiply(Matrix x) {
        Matrix m = new Matrix();
        m.setArray(multiply(x.getArray()));
        return m;

    }

    /**
     * 传入一个矩阵类进行相乘
     */
    public double[][] multiply(double[][] aim) {
        if (this.col != aim.length) {
            return null;
        }
        double[][] result = new double[this.row][aim[0].length];
        for (int row = 0; row < this.row; row++) {
            for (int col = 0; col < aim[0].length; col++) {
                double num = 0;
                for (int i = 0; i < this.col; i++) {
                    num += array[row][i] * aim[i][col];
                }
                result[row][col] = num;
            }
        }
        return result;
    }
}
