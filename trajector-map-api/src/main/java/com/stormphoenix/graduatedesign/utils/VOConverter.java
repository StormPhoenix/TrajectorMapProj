package com.stormphoenix.graduatedesign.utils;

import com.stormphoenix.graduatedesign.algorithms.Path;
import com.stormphoenix.graduatedesign.algorithms.Point;
import com.stormphoenix.graduatedesign.dto.LocationDTO;
import com.stormphoenix.graduatedesign.dto.TrajectorDTO;
import com.stormphoenix.graduatedesign.dto.UserTrajectoryDTO;
import com.stormphoenix.graduatedesign.redis.RedisTrajectorPath;
import com.stormphoenix.graduatedesign.vo.TrajectorPathVO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Developer on 18-5-10.
 */
public class VOConverter {
    public static List<LocationDTO> convertPointsToLocations(List<Point> points) {
        List<LocationDTO> locationDTOList = new ArrayList();
        for (Point point : points) {
            LocationDTO locationDTO = new LocationDTO();
            locationDTO.setLongitude(point.getX());
            locationDTO.setLatitude(point.getY());
            locationDTOList.add(locationDTO);
        }
        return locationDTOList;
    }

    /**
     * @param userId
     * @param userName
     * @param paths
     * @return
     */
    public static UserTrajectoryDTO convertPathList2UserTrajectorDTO(Long userId, String userName, List<Path> paths) {
        UserTrajectoryDTO userTrajectoryDTO = new UserTrajectoryDTO();
        userTrajectoryDTO.setUserId(userId);
        userTrajectoryDTO.setUserName(userName);
        userTrajectoryDTO.setTrajectories(new ArrayList<>());
        TrajectorDTO trajectorDTO;
        for (Path path : paths) {
            trajectorDTO = new TrajectorDTO();
            // TODO trajectorDTO.setTrajectoryId(?)
            trajectorDTO.setTrajectoryId(null);
            trajectorDTO.setLocations(new ArrayList());
            for (Point point : path.getPoints()) {
                LocationDTO locationDTO = new LocationDTO();
                locationDTO.setTimestamp(null);
                locationDTO.setLongitude(point.getX());
                locationDTO.setLatitude(point.getY());
                trajectorDTO.getLocations().add(locationDTO);
            }
            userTrajectoryDTO.getTrajectories().add(trajectorDTO);
        }
        return userTrajectoryDTO;
    }

    /**
     * @param userTrajectoryDTO
     * @return
     */
    public static List<Path> convertUserTrajectorDTO2PathList(UserTrajectoryDTO userTrajectoryDTO) {
        List<Path> result = new ArrayList();
        Path path;
        for (TrajectorDTO trajectorDTO : userTrajectoryDTO.getTrajectories()) {
            path = new Path();
            for (LocationDTO locationDTO : trajectorDTO.getLocations()) {
                path.addPoint(new Point(locationDTO.getLatitude(), locationDTO.getLongitude()));
            }
            result.add(path);
        }
        return result;
    }

    /**
     * @param trajectorDTO
     * @return
     */
    public static Path convertTrajectorDTO2Path(TrajectorDTO trajectorDTO) {
        Path resultPath = new Path();
        for (LocationDTO locationDTO : trajectorDTO.getLocations()) {
            resultPath.addPoint(new Point(locationDTO.getLongitude(), locationDTO.getLatitude()));
        }
        return resultPath;
    }

    public static List<TrajectorPathVO> convertUserTrajectorDTO2TrajectorPathVO(UserTrajectoryDTO dto) {
        List<TrajectorPathVO> result = new ArrayList();
        for (TrajectorDTO trajectorDTO : dto.getTrajectories()) {
            TrajectorPathVO pathVO = new TrajectorPathVO();
            pathVO.setName(String.valueOf(dto.getUserId()));
            List<List<Double>> path = new ArrayList();
            List<Double> point;
            for (LocationDTO locationDTO : trajectorDTO.getLocations()) {
                point = new ArrayList();
                point.add(locationDTO.getLongitude());
                point.add(locationDTO.getLatitude());
                path.add(point);
            }
            pathVO.setPath(path);
            result.add(pathVO);
        }
        return result;
    }

    public static RedisTrajectorPath[] convertTrajectorPathVOList2RedisTrajectorPathArray(List<TrajectorPathVO> voList) {
        RedisTrajectorPath[] result = new RedisTrajectorPath[voList.size()];
        for (int index = 0; index < voList.size(); index++) {
            result[index] = convertTrajectorPathVO2RedisTrajectorPath(voList.get(index));
        }
        return result;
    }

    public static List<TrajectorPathVO> convertRedisTrajectorPathArray2TrajectorPathVOList(RedisTrajectorPath[] paths) {
        List<TrajectorPathVO> result = new ArrayList();
        for (RedisTrajectorPath path : paths) {
            result.add(convertRedisTrajectorPath2TrajectorPathVO(path));
        }
        return result;
    }

    public static RedisTrajectorPath convertTrajectorPathVO2RedisTrajectorPath(TrajectorPathVO vo) {
        Double[][] paths = new Double[vo.getPath().size()][2];
        for (int index = 0; index < vo.getPath().size(); index++) {
            paths[index][0] = vo.getPath().get(index).get(0);
            paths[index][1] = vo.getPath().get(index).get(1);
        }
        return new RedisTrajectorPath(vo.getName(), paths);
    }

    public static TrajectorPathVO convertRedisTrajectorPath2TrajectorPathVO(RedisTrajectorPath redisTrajectorPath) {
        List<List<Double>> paths = new ArrayList();
        List<Double> temp;
        for (int index = 0; index < redisTrajectorPath.getPath().length; index++) {
            temp = new ArrayList();
            temp.add(redisTrajectorPath.getPath()[index][0]);
            temp.add(redisTrajectorPath.getPath()[index][1]);
            paths.add(temp);
        }
        TrajectorPathVO result = new TrajectorPathVO();
        result.setName(redisTrajectorPath.getName());
        result.setPath(paths);
        return result;
    }
}
