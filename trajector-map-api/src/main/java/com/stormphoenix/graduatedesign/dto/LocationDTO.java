package com.stormphoenix.graduatedesign.dto;

import lombok.Data;

/**
 * Created by Developer on 18-5-10.
 */
@Data
public class LocationDTO {
    private Double latitude;
    private Double longitude;
    private Long timestamp;
    // extra data
    private String userName;
    private Long userId;
    private Long trajectoryId;
}
