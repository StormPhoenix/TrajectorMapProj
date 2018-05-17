package com.stormphoenix.graduatedesign.vo;

import lombok.Data;

import java.util.List;

/**
 * Created by Developer on 18-5-10.
 */
@Data
public class TrajectorPathVO {
    private String name;
    private List<List<Double>> path;
}
