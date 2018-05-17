package com.stormphoenix.graduatedesign.dto;

import lombok.Data;

import java.util.List;

/**
 * Created by Developer on 18-5-10.
 */
@Data
public class TrajectorDTO {
    private Long trajectoryId;
    private List<LocationDTO> locations;
}
