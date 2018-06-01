package com.stormphoenix.graduatedesign.algorithms.basic;

/**
 * Created by Developer on 18-5-15.
 */
public class Rectangle {
    private Double leftX;
    private Double rightX;
    private Double topY;
    private Double bottomY;

    /**
     * 检查点是否在矩阵区域内部
     *
     * @param point
     * @return
     */
    public boolean checkPointInRect(Point point) {
        return checkPointInRect(point.getX(), point.getY());
    }

    public boolean checkPointInRect(double pointX, double pointY) {
        if (pointX >= leftX && pointX <= rightX
                && pointY <= topY && pointY >= bottomY) {
            return true;
        }
        return false;
    }

    public Double getLeftX() {
        return leftX;
    }

    public void setLeftX(Double leftX) {
        this.leftX = leftX;
    }

    public Double getRightX() {
        return rightX;
    }

    public void setRightX(Double rightX) {
        this.rightX = rightX;
    }

    public Double getTopY() {
        return topY;
    }

    public void setTopY(Double topY) {
        this.topY = topY;
    }

    public Double getBottomY() {
        return bottomY;
    }

    public void setBottomY(Double bottomY) {
        this.bottomY = bottomY;
    }
}
