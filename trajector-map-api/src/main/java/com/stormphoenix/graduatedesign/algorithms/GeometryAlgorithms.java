package com.stormphoenix.graduatedesign.algorithms;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Developer on 18-5-15.
 */
public class GeometryAlgorithms {
    /**
     * 利用一个矩阵区域将一个 Path　切成矩阵区域内的多个 Path
     *
     * @param path
     * @return
     */
    public static List<Path> cutPathByRect(Path path, Rectangle rect) {
        List<Path> result = new ArrayList();
        Point currentPoint;
        int prePointIndex = -1;
        int currentPointIndex = 0;
        while ((currentPoint = path.getPoint(currentPointIndex)) != null) {
            switch (calculateStatus(prePointIndex, currentPointIndex, path, rect)) {
                case CUR_POT_IN_START:
                    result.add(createNewPath(currentPoint));
                    break;
                case CUR_POT_OUT_START:
                    break;
                case CUR_POT_IN_PRE_IN:
                    result.get(result.size() - 1).addPoints(currentPoint);
                    break;
                case CUR_POT_IN_PRE_OUT:
                    result.add(
                            createNewPath(
                                    calculateNewPoints(
                                            path.getPoint(prePointIndex),
                                            path.getPoint(currentPointIndex),
                                            rect,
                                            SegmentRectStatus.CUR_POT_IN_PRE_OUT)));
                    break;
                case CUR_POT_OUT_PRE_IN:
                    result.get(result.size() - 1).addPoints(
                            calculateNewPoints(
                                    path.getPoint(prePointIndex),
                                    path.getPoint(currentPointIndex),
                                    rect,
                                    SegmentRectStatus.CUR_POT_OUT_PRE_IN));
                    break;
                case CUR_POT_OUT_PRE_OUT:
                    break;
//                    result.add(
//                            createNewPath(
//                                    calculateNewPoints(
//                                            path.getPoint(prePointIndex),
//                                            path.getPoint(currentPointIndex),
//                                            rect,
//                                            SegmentRectStatus.CUR_POT_OUT_PRE_OUT)));
//                    break;
                default:
                    break;
            }
            prePointIndex++;
            currentPointIndex++;
        }
        return result;
    }

    /**
     * 1. 计算 pointStart 和 pointEnd 两点之间的线段和矩阵区域　Rectangle 之间的交点
     * 2. 通过对　pointPairStatus　进行判断，判断是否要添加新的点
     *
     * @param pointStart
     * @param pointEnd
     * @param rectangle
     * @param pointPairStatus
     * @return
     */
    private static List<Point> calculateNewPoints(Point pointStart, Point pointEnd, Rectangle rectangle, SegmentRectStatus pointPairStatus) {
        List<Point> result = new ArrayList();
        switch (pointPairStatus) {
            case CUR_POT_IN_START:
                // pointStart 应该为 null，直接返回 pointEnd
            case CUR_POT_IN_PRE_IN:
                result.add(pointEnd);
                break;
            case CUR_POT_OUT_PRE_OUT:
                result = calculateIntersectionPoints(pointStart, pointEnd, rectangle);
                break;
            case CUR_POT_IN_PRE_OUT:
                result = calculateIntersectionPoints(pointStart, pointEnd, rectangle);
                result.add(pointEnd);
                break;
            case CUR_POT_OUT_PRE_IN:
                result = calculateIntersectionPoints(pointStart, pointEnd, rectangle);
                break;
            case CUR_POT_OUT_START:
                break;
            default:
                break;
        }
        return result;
    }

    /**
     * 计算　pointStart 和 pointEnd 连成的直线与 rectangle 构成的交点
     *
     * @param pointStart
     * @param pointEnd
     * @param rectangle
     * @return
     */
    private static List<Point> calculateIntersectionPoints(Point pointStart, Point pointEnd, Rectangle rectangle) {
        List<Point> result = new ArrayList();
        // y = k * x + b
        // 斜率
        double k;
        // 截距
        double b;
        // 求出直线方程
        if (pointStart.getX() == pointEnd.getX()) {
            k = Double.MAX_VALUE;
        } else {
            k = (pointEnd.getY() - pointStart.getY()) / (pointEnd.getX() - pointStart.getX());
        }
        // 两种情况判断
        if (k == Double.MAX_VALUE) {
            // 斜率无穷大
            if (pointStart.getX() < rectangle.getRightX() && pointStart.getX() > rectangle.getLeftX()) {
                if (pointEnd.getY() > pointStart.getY()) {
                    // 向上情况
                    if (rectangle.getTopY() <= pointStart.getY()) {
                        // 无交点
                    } else if (rectangle.getTopY() > pointStart.getY()
                            && rectangle.getTopY() <= pointEnd.getY()) {
                        // 一个或两个交点
                        if (pointStart.getY() >= rectangle.getBottomY()) {
                            result.add(new Point(pointStart.getX(), rectangle.getTopY()));
                        } else {
                            result.add(new Point(pointStart.getX(), rectangle.getBottomY()));
                            result.add(new Point(pointStart.getX(), rectangle.getTopY()));
                        }
                    } else if (rectangle.getTopY() > pointEnd.getY()) {
                        if (pointEnd.getY() > rectangle.getBottomY()) {
                            if (pointStart.getY() > rectangle.getBottomY()) {
                                // ０个
                            } else {
                                result.add(new Point(pointStart.getX(), rectangle.getBottomY()));
                            }
                        } else {
                            // 无交点
                        }
                    }
                } else if (pointEnd.getY() < pointStart.getY()) {
                    // 向下情况
                    if (rectangle.getBottomY() >= pointStart.getY()) {
                        // 无交点
                    } else if (rectangle.getBottomY() < pointStart.getY()
                            && rectangle.getBottomY() >= pointEnd.getY()) {
                        if (pointStart.getY() < rectangle.getTopY()) {
                            // 一个交点
                            result.add(new Point(pointStart.getX(), rectangle.getBottomY()));
                        } else {
                            result.add(new Point(pointStart.getX(), rectangle.getTopY()));
                            result.add(new Point(pointStart.getX(), rectangle.getBottomY()));
                        }
                    } else if (rectangle.getBottomY() < pointEnd.getY()) {
                        if (pointEnd.getY() < rectangle.getTopY()) {
                            if (pointStart.getY() < rectangle.getTopY()) {
                                // ０个
                            } else {
                                result.add(new Point(pointStart.getX(), rectangle.getTopY()));
                            }
                        } else {
                            // 无交点
                        }
                    }
                } else {
                    // 两点重合，不处理
                }
            } else {
                // 无交点
            }
        } else if (k == 0) {
            // 水平方向
            if (rectangle.getTopY() > pointStart.getY()
                    && rectangle.getBottomY() < pointStart.getY()) {
                // 有交点
                if (pointStart.getX() < pointEnd.getX()) {
                    // 直线向右
                    if (pointEnd.getX() <= rectangle.getLeftX()) {
                        // 无交点
                    } else if (pointEnd.getX() > rectangle.getLeftX() && pointEnd.getX() < rectangle.getRightX()) {
                        if (pointStart.getX() <= rectangle.getLeftX()) {
                            // 一个交点
                            result.add(new Point(rectangle.getLeftX(), pointStart.getY()));
                        } else {
                            // 无交点
                        }
                    } else {
                        if (pointStart.getX() <= rectangle.getLeftX()) {
                            // 两个交点
                            result.add(new Point(rectangle.getLeftX(), pointStart.getY()));
                            result.add(new Point(rectangle.getRightX(), pointStart.getY()));
                        } else if (pointStart.getX() > rectangle.getLeftX()
                                && pointStart.getX() < rectangle.getRightX()) {
                            // 一个交点
                            result.add(new Point(rectangle.getRightX(), pointStart.getY()));
                        } else {
                            // 无交点
                        }
                    }
                } else if (pointStart.getX() > pointEnd.getX()) {
                    // 水平向左
                    if (pointEnd.getX() >= rectangle.getRightX()) {
                        // 无交点
                    } else if (pointEnd.getX() < rectangle.getRightX()
                            && pointEnd.getX() > rectangle.getLeftX()) {
                        if (pointStart.getX() >= rectangle.getRightX()) {
                            // 一个交点
                            result.add(new Point(rectangle.getRightX(), pointStart.getY()));
                        } else {
                            // ０个交点
                        }
                    } else {
                        if (pointStart.getX() >= rectangle.getRightX()) {
                            // 两个交点
                            result.add(new Point(rectangle.getRightX(), pointStart.getY()));
                            result.add(new Point(rectangle.getLeftX(), pointStart.getY()));
                        } else if (pointStart.getX() < rectangle.getRightX()
                                && pointStart.getX() > rectangle.getLeftX()) {
                            // 一个交点
                            result.add(new Point(rectangle.getLeftX(), pointStart.getY()));
                        } else {
                            // 无交点
                        }
                    }
                } else {
                    // 两点重合，不处理
                }
            } else {
                // 无交点
            }
        } else {
            // 斜率正常，构造一元一次方程
            b = pointStart.getY() - k * pointStart.getX();
            // 从 rectangle 的 leftX 开始逆时针求交点，注：交点有两个、０个
            result = new LinkedList<>();
            // 判断 rectangle.leftX
            findInsectionPoints(pointStart, pointEnd, rectangle, rectangle.getLeftX(), k * rectangle.getLeftX() + b, result);
            // 判断 rectangle.bottomY
            findInsectionPoints(pointStart, pointEnd, rectangle, (rectangle.getBottomY() - b) / k, rectangle.getBottomY(), result);
            // 判断 rectangle.rightX
            findInsectionPoints(pointStart, pointEnd, rectangle, rectangle.getRightX(), k * rectangle.getRightX() + b, result);
            // 判断 rectangle.topY
            findInsectionPoints(pointStart, pointEnd, rectangle, (rectangle.getTopY() - b) / k, rectangle.getTopY(), result);
            // 对 tempInsectionPoints 排序
            sortInsectionPoints(pointStart, pointEnd, result);
        }
        return result;
    }

    /**
     * 根据 pointStart 和 pointEnd 的方向对交点列表 tempInsectionPoints 排个序
     *
     * @param pointStart
     * @param pointEnd
     * @param tempInsectionPoints
     */
    private static void sortInsectionPoints(Point pointStart, Point pointEnd, List<Point> tempInsectionPoints) {
        switch (tempInsectionPoints.size()) {
            case 0:
            case 1:
                break;
            case 2:
                if (Math.pow(tempInsectionPoints.get(0).getX() - pointStart.getX(), 2)
                        + Math.pow(tempInsectionPoints.get(0).getY() - pointStart.getY(), 2)
                        > Math.pow(tempInsectionPoints.get(1).getX() - pointStart.getX(), 2)
                        + Math.pow(tempInsectionPoints.get(1).getY() - pointStart.getY(), 2)) {
                    Point tempPot = tempInsectionPoints.get(0);
                    tempInsectionPoints.set(0, tempInsectionPoints.get(1));
                    tempInsectionPoints.set(1, tempPot);
                }
                break;
            default:
                break;
        }
    }

    private static void findInsectionPoints(Point pointStart, Point pointEnd, Rectangle rectangle, double pointX, double pointY, List<Point> tempInsectionPoints) {
        if (rectangle.checkPointInRect(pointX, pointY)
                && checkInPointPair(pointX, pointY, pointStart, pointEnd)) {
            tempInsectionPoints.add(new Point(pointX, pointY));
        }
    }

    /**
     * 检查点在 pointStart 和 pointEnd 之间
     *
     * @param pointX
     * @param pointY
     * @param pointStart
     * @param pointEnd
     * @return
     */
    private static boolean checkInPointPair(double pointX, double pointY, Point pointStart, Point pointEnd) {
        if ((pointX - pointStart.getX()) * (pointX - pointEnd.getX()) > 0) {
            return false;
        }
        if ((pointY - pointStart.getY()) * (pointY - pointEnd.getY()) > 0) {
            return false;
        }
        return true;
    }

    /**
     * @param currentPoints
     * @return
     */
    private static Path createNewPath(List<Point> currentPoints) {
        Path path = new Path();
        if (currentPoints != null) {
            for (Point point : currentPoints) {
                if (point != null) {
                    path.addPoints(point);
                }
            }
        }
        return path;
    }

    /**
     * @param currentPoint
     * @return
     */
    private static Path createNewPath(Point currentPoint) {
        Path path = new Path();
        if (currentPoint != null) {
            path.addPoints(currentPoint);
        }
        return path;
    }

    /**
     * 计算当前两点在 Rectangle 的状态
     *
     * @param prePointIndex
     * @param currentPointIndex
     * @param path
     */
    private static SegmentRectStatus calculateStatus(int prePointIndex,
                                                     int currentPointIndex,
                                                     Path path,
                                                     Rectangle rect) {
        Point prePoint = path.getPoint(prePointIndex);
        Point currentPoint = path.getPoint(currentPointIndex);
        SegmentRectStatus status = null;
        if (prePoint == null && rect.checkPointInRect(currentPoint)) {
            status = SegmentRectStatus.CUR_POT_IN_START;
        } else if (prePoint == null && !rect.checkPointInRect(currentPoint)) {
            status = SegmentRectStatus.CUR_POT_OUT_START;
        } else if (!rect.checkPointInRect(currentPoint) && rect.checkPointInRect(prePoint)) {
            status = SegmentRectStatus.CUR_POT_OUT_PRE_IN;
        } else if (!rect.checkPointInRect(currentPoint) && !rect.checkPointInRect(prePoint)) {
            status = SegmentRectStatus.CUR_POT_OUT_PRE_OUT;
        } else if (rect.checkPointInRect(prePoint)
                && rect.checkPointInRect(currentPoint)) {
            status = SegmentRectStatus.CUR_POT_IN_PRE_IN;
        } else if (rect.checkPointInRect(currentPoint)
                && !rect.checkPointInRect(prePoint)) {
            status = SegmentRectStatus.CUR_POT_IN_PRE_OUT;
        }
        return status;
    }

    private enum SegmentRectStatus {
        CUR_POT_OUT_START,
        CUR_POT_IN_START,
        CUR_POT_OUT_PRE_IN,
        CUR_POT_OUT_PRE_OUT,
        CUR_POT_IN_PRE_IN,
        CUR_POT_IN_PRE_OUT
    }
}
