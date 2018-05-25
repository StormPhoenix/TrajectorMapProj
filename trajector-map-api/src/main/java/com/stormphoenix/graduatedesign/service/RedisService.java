package com.stormphoenix.graduatedesign.service;

import com.stormphoenix.graduatedesign.redis.RedisKeyTimeRegion;
import com.stormphoenix.graduatedesign.redis.RedisValueTrajectorPathList;
import com.stormphoenix.graduatedesign.utils.VOConverter;
import com.stormphoenix.graduatedesign.vo.TrajectorPathVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Developer on 18-5-25.
 */
@Service
public class RedisService {
    @Autowired
    private RedisTemplate<RedisKeyTimeRegion, RedisValueTrajectorPathList> redisTemplate;

    public List<TrajectorPathVO> getRedisTimeRegionValue(Long startTime, Long endTime, Double topLatitude, Double bottomLatitude, Double leftLongitude, Double rightLongitude) {
        RedisKeyTimeRegion timeRegionKey = new RedisKeyTimeRegion(startTime, endTime, topLatitude, bottomLatitude, leftLongitude, rightLongitude);
        RedisValueTrajectorPathList redisValueTrajectorPathList = redisTemplate.opsForValue().get(timeRegionKey);
        if (redisValueTrajectorPathList != null) {
            return VOConverter.convertRedisTrajectorPathArray2TrajectorPathVOList(redisValueTrajectorPathList.getPaths());
        }
        return null;
    }

    public void putRedisTimeRegionValue(Long startTime, Long endTime, Double topLatitude, Double bottomLatitude, Double leftLongitude, Double rightLongitude, List<TrajectorPathVO> trajectorPathVOList) {
        RedisKeyTimeRegion timeRegionKey = new RedisKeyTimeRegion(startTime, endTime, topLatitude, bottomLatitude, leftLongitude, rightLongitude);
        RedisValueTrajectorPathList trajectorPathListValue;
        trajectorPathListValue = new RedisValueTrajectorPathList(VOConverter.convertTrajectorPathVOList2RedisTrajectorPathArray(trajectorPathVOList));
        redisTemplate.opsForValue().set(timeRegionKey, trajectorPathListValue);
    }
}
