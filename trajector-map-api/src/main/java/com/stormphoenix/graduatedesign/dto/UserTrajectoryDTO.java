package com.stormphoenix.graduatedesign.dto;

import lombok.Data;

import java.util.List;

/**
 * Created by Developer on 18-5-9.
 */
@Data
public class UserTrajectoryDTO {
    private Long userId;
    private String userName;
    private List<TrajectorDTO> trajectories;
}
