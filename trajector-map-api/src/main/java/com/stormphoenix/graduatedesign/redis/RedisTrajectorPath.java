package com.stormphoenix.graduatedesign.redis;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Developer on 18-5-25.
 */
public class RedisTrajectorPath implements Serializable {
    private String name;
    private Double[][] path;

    public RedisTrajectorPath(String name, Double[][] path) {
        this.name = name;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double[][] getPath() {
        return path;
    }

    public void setPath(Double[][] path) {
        this.path = path;
    }
}
