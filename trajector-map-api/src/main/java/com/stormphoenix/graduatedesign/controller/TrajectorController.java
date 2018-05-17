package com.stormphoenix.graduatedesign.controller;

import com.alicloud.openservices.tablestore.model.*;
import com.stormphoenix.graduatedesign.constants.Constants;
import com.stormphoenix.graduatedesign.dto.LocationDTO;
import com.stormphoenix.graduatedesign.dto.TrajectorDTO;
import com.stormphoenix.graduatedesign.dto.UserTrajectoryDTO;
import com.stormphoenix.graduatedesign.utils.VoConverter;
import com.stormphoenix.graduatedesign.vo.TrajectorPathVO;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.*;

/**
 * Created by Developer on 18-5-10.
 */
@RestController
@CrossOrigin
public class TrajectorController {
    @RequestMapping(value = "/example_data",
            method = RequestMethod.GET,
            produces = "application/json;charset=UTF-8")
    @CrossOrigin
    public String exampleData() {
        File file = new File("/home/Developer/Desktop/data.txt");
        StringBuffer buffer = new StringBuffer();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return buffer.toString();
    }

    @RequestMapping(value = "/user_and_time_scope", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @CrossOrigin
    public List<TrajectorPathVO> pathByUserAndTimeScope(@RequestParam("user_id") Long userId,
                                                        @RequestParam("start_time") Long startTime,
                                                        @RequestParam("end_time") Long endTime) {
        RangeRowQueryCriteria rangeRowQueryCriteria = new RangeRowQueryCriteria(Constants.TABLE_NAME_USER_TRAJECTOR);
        // 设置起始主键
        PrimaryKeyBuilder primaryKeyBuilder = PrimaryKeyBuilder.createPrimaryKeyBuilder();
        primaryKeyBuilder.addPrimaryKeyColumn(Constants.COLUMN_NAME_USER_ID, PrimaryKeyValue.fromLong(userId));
        primaryKeyBuilder.addPrimaryKeyColumn(Constants.COLUMN_NAME_TRAJECTORY_ID, PrimaryKeyValue.fromLong(startTime));
        primaryKeyBuilder.addPrimaryKeyColumn(Constants.COLUMN_NAME_TIMESTAMP, PrimaryKeyValue.fromLong(startTime));
        rangeRowQueryCriteria.setInclusiveStartPrimaryKey(primaryKeyBuilder.build());
//         设置结束主键
        primaryKeyBuilder = PrimaryKeyBuilder.createPrimaryKeyBuilder();
        primaryKeyBuilder.addPrimaryKeyColumn(Constants.COLUMN_NAME_USER_ID, PrimaryKeyValue.fromLong(userId));
        primaryKeyBuilder.addPrimaryKeyColumn(Constants.COLUMN_NAME_TRAJECTORY_ID, PrimaryKeyValue.fromLong(endTime));
        primaryKeyBuilder.addPrimaryKeyColumn(Constants.COLUMN_NAME_TIMESTAMP, PrimaryKeyValue.fromLong(endTime));
        rangeRowQueryCriteria.setExclusiveEndPrimaryKey(primaryKeyBuilder.build());
        // 设置读取最新版本
        rangeRowQueryCriteria.setMaxVersions(1);
        // 默认读取所有的属性列
        GetRangeResponse response = Constants.client.getRange(new GetRangeRequest(rangeRowQueryCriteria));

        UserTrajectoryDTO result = new UserTrajectoryDTO();
        result.setUserId(userId);
        result.setTrajectories(new ArrayList());
        Map<Long, TrajectorDTO> trajectorMap = new HashMap();
        List<Row> rows = response.getRows();
        for (Row row : rows) {
            Long trajectorId = row.getPrimaryKey().getPrimaryKeyColumn(Constants.COLUMN_NAME_TRAJECTORY_ID).getValue().asLong();
            TrajectorDTO tmpTraj = null;
            if (trajectorMap.containsKey(trajectorId)) {
                tmpTraj = trajectorMap.get(trajectorId);
            } else {
                tmpTraj = new TrajectorDTO();
                tmpTraj.setTrajectoryId(trajectorId);
                tmpTraj.setLocations(new ArrayList());
                trajectorMap.put(trajectorId, tmpTraj);
            }
            LocationDTO locationDTO = new LocationDTO();
            locationDTO.setTimestamp(row.getPrimaryKey().getPrimaryKeyColumn(Constants.COLUMN_NAME_TIMESTAMP).getValue().asLong());
            for (Column column : row.getColumns()) {
                if (column.getName().equals(Constants.COLUMN_NAME_LATITUDE)) {
                    locationDTO.setLatitude(column.getValue().asDouble());
                } else if (column.getName().equals(Constants.COLUMN_NAME_LONGITUDE)) {
                    locationDTO.setLongitude(column.getValue().asDouble());
                }
            }
            tmpTraj.getLocations().add(locationDTO);
        }
        Iterator<Map.Entry<Long, TrajectorDTO>> iterator = trajectorMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Long, TrajectorDTO> entry = iterator.next();
            result.getTrajectories().add(entry.getValue());
        }
        return VoConverter.convertUserTrajectorDTO2TrajectorPathVO(result);
    }

    public List<TrajectorPathVO> pathByTimeScope(@RequestParam("start_time") Long startTime,
                                                 @RequestParam("end_time") Long endTime) {
        RangeRowQueryCriteria rangeRowQueryCriteria = new RangeRowQueryCriteria(Constants.TABLE_NAME_USER_TRAJECTOR);
        // 设置起始主键
        PrimaryKeyBuilder primaryKeyBuilder = PrimaryKeyBuilder.createPrimaryKeyBuilder();
        primaryKeyBuilder.addPrimaryKeyColumn(Constants.COLUMN_NAME_USER_ID, PrimaryKeyValue.INF_MIN);
        primaryKeyBuilder.addPrimaryKeyColumn(Constants.COLUMN_NAME_TRAJECTORY_ID, PrimaryKeyValue.fromLong(startTime));
        primaryKeyBuilder.addPrimaryKeyColumn(Constants.COLUMN_NAME_TIMESTAMP, PrimaryKeyValue.fromLong(startTime));
        rangeRowQueryCriteria.setInclusiveStartPrimaryKey(primaryKeyBuilder.build());
//         设置结束主键
        primaryKeyBuilder = PrimaryKeyBuilder.createPrimaryKeyBuilder();
        primaryKeyBuilder.addPrimaryKeyColumn(Constants.COLUMN_NAME_USER_ID, PrimaryKeyValue.INF_MAX);
        primaryKeyBuilder.addPrimaryKeyColumn(Constants.COLUMN_NAME_TRAJECTORY_ID, PrimaryKeyValue.fromLong(endTime));
        primaryKeyBuilder.addPrimaryKeyColumn(Constants.COLUMN_NAME_TIMESTAMP, PrimaryKeyValue.fromLong(endTime));
        rangeRowQueryCriteria.setExclusiveEndPrimaryKey(primaryKeyBuilder.build());
        // 设置读取最新版本
        rangeRowQueryCriteria.setMaxVersions(1);
        // 默认读取所有的属性列
        GetRangeResponse response = Constants.client.getRange(new GetRangeRequest(rangeRowQueryCriteria));
        // Map<用户id, 用户轨迹信息>
//        Map<Long, UserTrajectoryDTO> userUserTrajectoryDtoMap = new HashMap();
        // Map<用户id, Map<轨迹Id, 具体轨迹信息>>
        Map<Long, Map<Long, TrajectorDTO>> userIdTrajectoriesDtoMap = new HashMap();
        List<Row> rows = response.getRows();
        for (Row row : rows) {
            // 获取所有信息
            Long userId = row.getPrimaryKey().getPrimaryKeyColumn(Constants.COLUMN_NAME_USER_ID).getValue().asLong();
            Long trajectorId = row.getPrimaryKey().getPrimaryKeyColumn(Constants.COLUMN_NAME_TRAJECTORY_ID).getValue().asLong();
            Long timestamp = row.getPrimaryKey().getPrimaryKeyColumn(Constants.COLUMN_NAME_TIMESTAMP).getValue().asLong();
            Double latitude = null, longitude = null;
            for (Column column : row.getColumns()) {
                if (column.getName().equals(Constants.COLUMN_NAME_LATITUDE)) {
                    latitude = column.getValue().asDouble();
                } else if (column.getName().equals(Constants.COLUMN_NAME_LONGITUDE)) {
                    longitude = column.getValue().asDouble();
                }
            }
            // trajector_id <-> TrajectorDTO
            Map<Long, TrajectorDTO> trajectorIdTrajectorDtoMap;
            if (userIdTrajectoriesDtoMap.containsKey(userId)) {
                trajectorIdTrajectorDtoMap = userIdTrajectoriesDtoMap.get(userId);
            } else {
                trajectorIdTrajectorDtoMap = new HashMap();
                userIdTrajectoriesDtoMap.put(userId, trajectorIdTrajectorDtoMap);
            }
            TrajectorDTO tempTrajectDto;
            if (trajectorIdTrajectorDtoMap.containsKey(trajectorId)) {
                tempTrajectDto = trajectorIdTrajectorDtoMap.get(trajectorId);
            } else {
                tempTrajectDto = new TrajectorDTO();
                tempTrajectDto.setTrajectoryId(trajectorId);
                tempTrajectDto.setLocations(new LinkedList());
                trajectorIdTrajectorDtoMap.put(trajectorId, tempTrajectDto);
            }
            LocationDTO locationDTO = new LocationDTO();
            locationDTO.setTimestamp(timestamp);
            locationDTO.setLatitude(latitude);
            locationDTO.setLongitude(longitude);
            tempTrajectDto.getLocations().add(locationDTO);
        }
        // userIdTrajectoriesDtoMap 初始化完毕
        List<UserTrajectoryDTO> userTrajectoryDtos = new ArrayList();
        Iterator<Map.Entry<Long, Map<Long, TrajectorDTO>>> iterator = userIdTrajectoriesDtoMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Long, Map<Long, TrajectorDTO>> entry = iterator.next();
            Long userId = entry.getKey();
            Map<Long, TrajectorDTO> trajectoryIdTrajectorDtoMap = entry.getValue();

        }
        /** ------------------------------------------------------------------- **/

//        Map<Long, TrajectorDTO> trajectorMap = new HashMap();
//        List<Row> rows = response.getRows();
//        for (Row row : rows) {
//            Long trajectorId = row.getPrimaryKey().getPrimaryKeyColumn(Constants.COLUMN_NAME_TRAJECTORY_ID).getValue().asLong();
//            TrajectorDTO tmpTraj = null;
//            if (trajectorMap.containsKey(trajectorId)) {
//                tmpTraj = trajectorMap.get(trajectorId);
//            } else {
//                tmpTraj = new TrajectorDTO();
//                tmpTraj.setTrajectoryId(trajectorId);
//                tmpTraj.setLocations(new ArrayList());
//                trajectorMap.put(trajectorId, tmpTraj);
//            }
//            LocationDTO locationDTO = new LocationDTO();
//            locationDTO.setTimestamp(row.getPrimaryKey().getPrimaryKeyColumn(Constants.COLUMN_NAME_TIMESTAMP).getValue().asLong());
//            for (Column column : row.getColumns()) {
//                if (column.getName().equals(Constants.COLUMN_NAME_LATITUDE)) {
//                    locationDTO.setLatitude(column.getValue().asDouble());
//                } else if (column.getName().equals(Constants.COLUMN_NAME_LONGITUDE)) {
//                    locationDTO.setLongitude(column.getValue().asDouble());
//                }
//            }
//            tmpTraj.getLocations().add(locationDTO);
//        }
//        Iterator<Map.Entry<Long, TrajectorDTO>> iterator = trajectorMap.entrySet().iterator();
//        while (iterator.hasNext()) {
//            Map.Entry<Long, TrajectorDTO> entry = iterator.next();
//            result.getTrajectories().add(entry.getValue());
//        }
//        return VoConverter.convertUserTrajectorDTO2TrajectorPathVO(result);
        return null;
    }
}
