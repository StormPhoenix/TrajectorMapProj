package com.stormphoenix.graduatedesign.utils;

import com.stormphoenix.graduatedesign.dto.LocationDTO;
import com.stormphoenix.graduatedesign.dto.TrajectorDTO;
import com.stormphoenix.graduatedesign.dto.UserTrajectoryDTO;
import com.stormphoenix.graduatedesign.vo.TrajectorPathVO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Developer on 18-5-10.
 */
public class VoConverter {
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
}
