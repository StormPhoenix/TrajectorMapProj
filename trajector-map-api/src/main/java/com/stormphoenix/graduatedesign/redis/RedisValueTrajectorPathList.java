package com.stormphoenix.graduatedesign.redis;

import com.stormphoenix.graduatedesign.vo.TrajectorPathVO;

import java.io.Serializable;

/**
 * Created by Developer on 18-5-25.
 */
public class RedisValueTrajectorPathList implements Serializable {
    private RedisTrajectorPath[] paths;

    public RedisValueTrajectorPathList(RedisTrajectorPath[] paths) {
        this.paths = paths;
    }

    public RedisTrajectorPath[] getPaths() {
        return paths;
    }

    public void setPaths(RedisTrajectorPath[] paths) {
        this.paths = paths;
    }
}
